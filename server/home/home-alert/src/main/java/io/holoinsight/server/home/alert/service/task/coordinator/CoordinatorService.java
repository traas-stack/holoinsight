/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.task.coordinator;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.reflect.TypeToken;
import io.holoinsight.server.common.AddressUtil;
import io.holoinsight.server.common.J;
import io.holoinsight.server.home.alert.service.task.CacheAlertTask;
import io.holoinsight.server.home.alert.service.task.coordinator.server.NettyServer;
import io.holoinsight.server.home.biz.common.MetaDictUtil;
import io.holoinsight.server.home.common.util.CLUSTER_ROLE_CONST;
import io.holoinsight.server.home.dal.mapper.ClusterMapper;
import io.holoinsight.server.home.dal.model.Cluster;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static io.holoinsight.server.home.biz.common.MetaDictType.GLOBAL_CONFIG;

/**
 * 用来协调 alarm 集群的任务分配，主要分为两步： order 阶段： 1. 通过心跳记录，获取任务分布的预方案； 2. index * 2s 进行报数，确认能参与任务计算的节点； 3.
 * 最多等待 size*2s，所有节点报数完毕，按照报数领取告警规则任务； calculate 阶段： 1. 所有节点计算，完毕后广播 ack； 2. 对于没有 ack 的节点，由 ack
 * 的里面序号最小的负责重试 2.1 重试：使用规则 id 查询告警历史，如果有，说明计算成功，只是 ack 失败；否则说明计算失败或者该规则不会触发告警，可以安全重试；
 *
 * @author masaimu
 * @version 2022-10-18 15:06:00
 */
@Slf4j
@Service
public class CoordinatorService {

  @Resource
  private ClusterMapper clusterMapper;
  @Autowired
  protected CacheAlertTask cacheAlertTask;
  private final String role = CLUSTER_ROLE_CONST.PROD;

  private List<String> otherMembers = new ArrayList<>();
  protected static final int PORT = 9527;
  public OrderMap orderMap = new OrderMap();

  /**
   * 查询最近一分钟的心跳记录，然后根据 ip 排序，找到自己的 order 顺序
   *
   * @return
   */
  public int getOrder() {
    long minuteBefore = System.currentTimeMillis() - 60_000L;
    QueryWrapper<Cluster> condition = new QueryWrapper<>();
    condition.eq("role", this.role);
    condition.ge("last_heartbeat_time", minuteBefore);

    List<Cluster> clusters = this.clusterMapper.selectList(condition);

    clusters.sort(Comparator.comparing(Cluster::getIp));
    String myIp = AddressUtil.getLocalHostIPV4();
    log.info("get order by my ip {} in {}", myIp, J.toJson(clusters));
    this.otherMembers = new ArrayList<>();
    int order = -1;
    List<String> blackList = getBlackServerList();
    if (!CollectionUtils.isEmpty(blackList) && blackList.contains(myIp)) {
      return order;
    }
    for (int i = 0; i < clusters.size(); i++) {
      Cluster cluster = clusters.get(i);
      if (myIp.equals(cluster.getIp())) {
        order = i;
      } else {
        this.otherMembers.add(cluster.getIp());
      }
    }
    return order;
  }

  private List<String> getBlackServerList() {
    String listStr = MetaDictUtil.getStringValue(GLOBAL_CONFIG, "alert_server_black_list");
    if (StringUtils.isBlank(listStr)) {
      return Collections.emptyList();
    }
    return J.fromJson(listStr, new TypeToken<List<String>>() {}.getType());
  }

  public void spread(long heartbeat) {
    int order = getOrder();
    if (order < 0) {
      log.info("fail to get order, give up allocating task.");
      return;
    }
    long periodId = orderMap.curPeriod();
    log.info("gossip spread preorder orderId {}, periodId {}, otherMenbers {}", order, periodId,
        this.otherMembers);
    CoordinatorSender sender = new CoordinatorSender(this.otherMembers, String.valueOf(order),
        String.valueOf(periodId), this, orderMap);
    sender.sendOrder(heartbeat);

    int realOrder = orderMap.getRealOrder();
    log.info("REAL_ORDER_MONITOR,realOrder={},ip={}", realOrder, orderMap.getSelfIp());
    if (realOrder < 0) {
      return;
    }
    calculateSelectRange(realOrder);
  }

  protected void calculateSelectRange(int realOrder) {
    double realSize = orderMap.getRealSize().doubleValue();
    double ruleSize = this.cacheAlertTask.ruleSize("rule").doubleValue();
    double aiSize = this.cacheAlertTask.ruleSize("ai").doubleValue();
    double pqlSize = this.cacheAlertTask.ruleSize("pql").doubleValue();
    // 领取任务，[(order-1)*(ruleSize/realSize), order*(ruleSize/realSize))
    log.info("gossip order realOrder {}, realSize {}, ruleSize {}, aiSize {}, pqlSize {}",
        realOrder, realSize, ruleSize, aiSize, pqlSize);
    int rulePageSize = (int) Math.ceil(ruleSize / realSize);
    int rulePageNum = rulePageSize * realOrder;
    this.cacheAlertTask.setRulePageSize(rulePageSize);
    this.cacheAlertTask.setRulePageNum(rulePageNum);

    int aiPageSize = (int) Math.ceil(aiSize / realSize);
    int aiPageNum = aiPageSize * realOrder;
    this.cacheAlertTask.setAiPageSize(aiPageSize);
    this.cacheAlertTask.setAiPageNum(aiPageNum);

    int pqlPageSize = (int) Math.ceil(pqlSize / realSize);
    int pqlPageNum = pqlPageSize * realOrder;
    this.cacheAlertTask.setPqlPageSize(pqlPageSize);
    this.cacheAlertTask.setPqlPageNum(pqlPageNum);
    log.info(
        "TASK_ASSIGN_MONITOR,rulePageNum={},rulePageSize={},aiPageNum={},aiPageSize={},pqlPageNum={},pqlPageSize={}",
        rulePageNum, rulePageSize, aiPageNum, aiPageSize, pqlPageNum, pqlPageSize);
  }

  public void buildCluster() throws Exception {
    new NettyServer(this).startup(PORT);
  }

  public void putOrderingMap(String ip, String order) {
    this.orderMap.putOrdering(ip, order);
  }

  public void count(String data) {
    this.orderMap.count(data);
  }

  public void distribute(String ip, String data) {
    this.orderMap.distribute(ip, data);
  }

  public List getSortedOrderedMap() {
    return this.orderMap.getSortedOrderedMap();
  }

  public void checkOrderedMapConfig() {
    OrderConfig orderConfig = MetaDictUtil.getValue("alert", "coordinator_order",
        new com.google.gson.reflect.TypeToken<OrderConfig>() {});
    if (orderConfig == null) {
      log.info("can not get orderConfig from type {} dict_key {}", "alert", "coordinator_order");
      return;
    }
    orderMap.forceSet(orderConfig);
  }
}
