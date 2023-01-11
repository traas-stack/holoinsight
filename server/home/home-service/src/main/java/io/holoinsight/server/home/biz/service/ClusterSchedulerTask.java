/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service;

import io.holoinsight.server.common.AddressUtil;
import io.holoinsight.server.home.common.util.CLUSTER_ROLE_CONST;
import io.holoinsight.server.home.common.util.ProdLog;
import io.holoinsight.server.home.common.util.ScheduleLoadTask;
import io.holoinsight.server.home.common.util.StringUtil;
import io.holoinsight.server.home.dal.model.dto.ClusterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 *
 * @author jsy1001de
 * @version 1.0: ClusterSchedulerTask.java, v 0.1 2022年10月20日 上午11:03 jinsong.yjs Exp $
 */
@Service
public class ClusterSchedulerTask extends ScheduleLoadTask {
  public static int HEARTBEAT_PERIOD_SECOND = 10;

  @Autowired
  private ClusterService clusterService;

  private String         role = CLUSTER_ROLE_CONST.PROD;

  @Override
  public void load() throws Exception {
    try {
      long s = System.currentTimeMillis();
//            startHeartBeat();
      ProdLog.info("update heart beat success, cost:" + (System.currentTimeMillis() - s));
    } catch (Exception e) {
      ProdLog.error("update heartbeat error", e);
    }
  }

  private void startHeartBeat() {
    String myIp = AddressUtil.getLocalHostIPV4();
    String hostName = AddressUtil.getLocalHostName();
    ClusterDTO cluster = new ClusterDTO();
    cluster.setLastHeartBeatTime(System.currentTimeMillis());
    cluster.setRole(getRole());
    cluster.setIp(myIp);
    cluster.setHostname(hostName);
    cluster.setGmtModified(new Date());
    clusterService.upsert(cluster);
  }

  @Override
  public int periodInSeconds() {
    return HEARTBEAT_PERIOD_SECOND;
  }

  @Override
  public String getTaskName() {
    return "ClusterHeartbeatTask";
  }

  private String getRole() {
    if (StringUtil.isBlank(role)) {
      return CLUSTER_ROLE_CONST.PROD;
    }

    return role;
  }
}
