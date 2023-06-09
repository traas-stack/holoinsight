/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web;

import io.holoinsight.server.common.config.ScheduleLoadTask;
import io.holoinsight.server.home.alert.service.task.coordinator.AlertClusterService;
import io.holoinsight.server.common.service.SuperCacheService;
import io.holoinsight.server.home.common.util.cache.local.LocalCacheManage;
import io.holoinsight.server.home.task.MonitorTaskManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 初始化启动任务
 * 
 * @author jsy1001de
 * @version 1.0: AppInitListener.java, v 0.1 2022年03月15日 8:15 下午 jinsong.yjs Exp $
 */
@Service
public class AppInitListener implements InitializingBean {

  @Autowired
  private SuperCacheService superCacheService;

  @Autowired
  private MonitorTaskManager monitorTaskManager;

  @Autowired
  private LocalCacheManage localCacheManage;

  @Autowired
  private AlertClusterService alertClusterService;


  @Override
  public void afterPropertiesSet() {
    try {
      ScheduleLoadTask.registerTask(superCacheService, true);
      ScheduleLoadTask.registerTask(localCacheManage, true);
      ScheduleLoadTask.registerTask(monitorTaskManager, true);
      ScheduleLoadTask.registerTask(alertClusterService, true);
    } catch (Exception e) {
      throw new RuntimeException("init config fail", e);
    }
  }
}
