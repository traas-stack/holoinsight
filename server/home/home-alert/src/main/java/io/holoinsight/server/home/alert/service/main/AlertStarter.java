/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.main;

import io.holoinsight.server.home.alert.plugin.AlertNotifyHandler;
import io.holoinsight.server.home.alert.plugin.AlertSaveHistoryHandler;
import io.holoinsight.server.home.alert.plugin.GetSubscriptionHandler;
import io.holoinsight.server.home.alert.service.event.AlertHandlerExecutor;
import io.holoinsight.server.home.alert.service.event.AlertServiceRegistry;
import io.holoinsight.server.home.alert.service.task.AlertTaskScheduler;
import io.holoinsight.server.home.alert.service.task.CacheAlertTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/2/22 4:27 下午
 */
@Service
public class AlertStarter implements InitializingBean {

  private static Logger LOGGER = LoggerFactory.getLogger(AlertStarter.class);

  @Resource
  private CacheAlertTask cacheAlertTask;

  @Resource
  private AlertTaskScheduler alertTaskScheduler;

  @Autowired
  private AlertServiceRegistry alertServiceRegistry;

  @Autowired
  private AlertSaveHistoryHandler alertSaveHistoryHandler;

  @Autowired
  private GetSubscriptionHandler getSubscriptionHandler;

  @Autowired
  private AlertNotifyHandler alertNotifyHandler;

  @Override
  public void afterPropertiesSet() throws Exception {
    try {
      LOGGER.info("build alert executor list.");
      List<AlertHandlerExecutor> executorList = new ArrayList<>();
      executorList.add(this.alertSaveHistoryHandler);
      executorList.add(this.getSubscriptionHandler);
      executorList.add(this.alertNotifyHandler);
      this.alertServiceRegistry.setAlertEventHanderList(executorList);

      // 启动获取告警任务缓存
      cacheAlertTask.start();

      // 启动执行告警任务
      alertTaskScheduler.start();
    } catch (Exception e) {
      LOGGER.error("fail to start alert task for {}", e.getMessage(), e);
    }
  }
}
