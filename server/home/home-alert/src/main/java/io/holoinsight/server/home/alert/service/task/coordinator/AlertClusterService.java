/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.task.coordinator;

import io.holoinsight.server.common.AddressUtil;
import io.holoinsight.server.common.config.ScheduleLoadTask;
import io.holoinsight.server.home.biz.service.ClusterService;
import io.holoinsight.server.home.common.util.CLUSTER_ROLE_CONST;
import io.holoinsight.server.home.dal.model.dto.ClusterDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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

  private String role = CLUSTER_ROLE_CONST.PROD;

  @Autowired
  private CoordinatorService coordinatorService;
  @Autowired
  private ClusterService clusterService;

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
    ClusterDTO cluster = new ClusterDTO();
    cluster.setLastHeartBeatTime(System.currentTimeMillis());
    cluster.setRole(getRole());
    cluster.setIp(myIp);
    cluster.setHostname(hostName);
    cluster.setGmtModified(new Date());
    clusterService.upsert(cluster);
    this.coordinatorService.checkOrderedMapConfig();
    this.coordinatorService.spread(heartbeat);
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
