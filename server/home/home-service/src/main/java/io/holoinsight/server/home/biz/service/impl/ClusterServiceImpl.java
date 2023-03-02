/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.holoinsight.server.common.AddressUtil;
import io.holoinsight.server.common.J;
import io.holoinsight.server.home.biz.service.ClusterService;
import io.holoinsight.server.home.common.util.CLUSTER_ROLE_CONST;
import io.holoinsight.server.common.config.ProdLog;
import io.holoinsight.server.home.common.util.StringUtil;
import io.holoinsight.server.home.dal.converter.CustomConverter;
import io.holoinsight.server.home.dal.mapper.ClusterMapper;
import io.holoinsight.server.home.dal.model.Cluster;
import io.holoinsight.server.home.dal.model.dto.ClusterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 *
 * @author jsy1001de
 * @version 1.0: ClusterServiceImpl.java, v 0.1 2022年10月20日 上午10:41 jinsong.yjs Exp $
 */
@Service
public class ClusterServiceImpl extends ServiceImpl<ClusterMapper, Cluster>
    implements ClusterService {

  public static int EXPIRE_HEARTBEAT_TIME = 60000;

  @Autowired
  private CustomConverter customConverter;

  private String role = CLUSTER_ROLE_CONST.PROD;

  public void upsert(ClusterDTO cluster) {
    long s = System.currentTimeMillis();
    // 先查，存在则update, 不存在则insert

    Map<String, Object> columnMap = new HashMap<>();
    columnMap.put("ip", cluster.getIp());

    List<ClusterDTO> clusters = customConverter.dosToDTOs(listByMap(columnMap));
    if (clusters == null || clusters.size() == 0) {
      save(customConverter.dtoToDO(cluster));
    } else {
      clusters.get(0).setLastHeartBeatTime(cluster.getLastHeartBeatTime());
      clusters.get(0).setRole(getRole());
      clusters.get(0).setHostname(cluster.getHostname());
      clusters.get(0).setGmtModified(new Date());
      updateById(customConverter.dtoToDO(clusters.get(0)));
    }
    ProdLog.monitor("HEARTBEAT", "", "", "", "", true, System.currentTimeMillis() - s, 1);
  }

  public List<ClusterDTO> getClusterAliveSortedByRole(String role) {
    List<ClusterDTO> res = new ArrayList<>();
    Map<Long, ClusterDTO> clusterMap = new HashMap<>();
    TreeSet<Long> set = new TreeSet<Long>(); // 直接set+hashmap排序，避免修改entity文件实现compare接口
    Map<String, Object> columnMap = new HashMap<>();
    columnMap.put("role", role);

    List<ClusterDTO> clusters = customConverter.dosToDTOs(listByMap(columnMap));
    for (ClusterDTO cluster : clusters) {

      if (cluster.getManualClose() != null && cluster.getManualClose()) {
        continue;// 人工关闭（所有任务相关都不会参加）
      }
      if (cluster.getLastHeartBeatTime() == null) {
        // invalid
        continue;
      }
      if (cluster.getLastHeartBeatTime() != null && cluster
          .getLastHeartBeatTime() <= (System.currentTimeMillis() - EXPIRE_HEARTBEAT_TIME)) {
        // 过期的心跳，不要了
        continue;
      }
      clusterMap.put(cluster.getId(), cluster);
      set.add(cluster.getId());
    }

    for (Long s : set) {
      res.add(clusterMap.get(s));
    }
    return res;
  }

  public boolean checkBrain(List<ClusterDTO> clusters) {
    String myIp = AddressUtil.getLocalHostIPV4();
    if (clusters == null || clusters.size() == 0) {
      ProdLog.error("some wrong i guess, coordinate is null");
      return false;
    }

    if (clusters.get(0).getIp().equals(myIp)) {
      ProdLog.info("I'am Brain..");
      ProdLog.info("all alive clusters:" + J.toJson(clusters) + ", myip is:" + myIp);
      return true;
    } else {
      ProdLog.info("Brain is:" + clusters.get(0).getIp());
    }
    return false;
  }

  private String getRole() {
    if (StringUtil.isBlank(role)) {
      return CLUSTER_ROLE_CONST.PROD;
    }

    return role;
  }
}
