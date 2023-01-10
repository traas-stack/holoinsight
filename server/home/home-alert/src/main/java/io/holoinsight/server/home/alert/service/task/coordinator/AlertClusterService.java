/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.task.coordinator;

import io.holoinsight.server.common.AddressUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.holoinsight.server.home.common.util.ScheduleLoadTask;
import io.holoinsight.server.home.dal.mapper.ClusterMapper;
import io.holoinsight.server.home.dal.model.Cluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author jsy1001de
 * @version 1.0: ClusterDTOService.java, v 0.1 2022年03月17日 5:45 下午 jinsong.yjs Exp $
 */
@Service
public class AlertClusterService extends ScheduleLoadTask {
  private static Logger LOGGER = LoggerFactory.getLogger(AlertClusterService.class);

  private static AtomicBoolean STARTED = new AtomicBoolean(false);
  public static int HEARTBEAT_PERIOD_SECOND = 10;
  public static int EXPIRE_HEARTBEAT_TIME = 60000;

  protected static final String role = "holoinsight-alert";

  @Resource
  private ClusterMapper clusterMapper;
  @Autowired
  private CoordinatorService coordinatorService;

  @Override
  public void load() throws Exception {
    try {
      long s = System.currentTimeMillis();
      startHeartBeat();
      LOGGER.info("alert update heart beat success, cost:" + (System.currentTimeMillis() - s));
    } catch (Exception e) {
      LOGGER.error("alert update heartbeat error", e);
    }
  }

  private void startHeartBeat() {
    long heartbeat = System.currentTimeMillis();
    String myIp = AddressUtil.getLocalHostIPV4();
    String hostName = AddressUtil.getLocalHostName();
    LOGGER.info("hostname {}, size {}", hostName, hostName.length());
    Cluster cluster = new Cluster();
    cluster.setLastHeartBeatTime(heartbeat);
    cluster.setRole(getRole());
    cluster.setIp(myIp);
    cluster.setHostname(hostName);
    cluster.setGmtModified(new Date());
    upsert(cluster);
    this.coordinatorService.spread(heartbeat);
  }

  public void upsert(Cluster cluster) {
    long s = System.currentTimeMillis();
    QueryWrapper<Cluster> condition = new QueryWrapper<>();
    condition.eq("ip", cluster.getIp());
    List<Cluster> clusters = this.clusterMapper.selectList(condition);
    int result;
    if (CollectionUtils.isEmpty(clusters)) {
      result = this.clusterMapper.insert(cluster);
    } else {
      Cluster res = clusters.get(0);
      res.setLastHeartBeatTime(cluster.getLastHeartBeatTime());
      res.setRole(getRole());
      res.setHostname(cluster.getHostname());
      res.setGmtModified(new Date());
      QueryWrapper<Cluster> updateWrapper = new QueryWrapper<>();
      updateWrapper.eq("id", res.getId());
      result = this.clusterMapper.update(res, updateWrapper);
    }
    LOGGER.info("HEARTBEAT {} {} {}", true, System.currentTimeMillis() - s, result);
  }

  private String getRole() {
    return role;
  }

  @Override
  public int periodInSeconds() {
    return HEARTBEAT_PERIOD_SECOND;
  }

  @Override
  public String getTaskName() {
    return "AlertClusterHeartbeatTask";
  }

  @Override
  public void init() throws Exception {
    coordinatorService.getOrder();
    // 建立集群，增加 receive 处理函数
    coordinatorService.buildCluster();
  }
}
