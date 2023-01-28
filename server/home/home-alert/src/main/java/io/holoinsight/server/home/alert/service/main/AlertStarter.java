/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.main;

import io.holoinsight.server.home.alert.service.task.AlertTaskScheduler;
import io.holoinsight.server.home.alert.service.task.CacheAlertConfig;
import io.holoinsight.server.home.alert.service.task.CacheAlertTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wangsiyuan
 * @date 2022/2/22 4:27 下午
 */
@Service
public class AlertStarter implements InitializingBean {

  private static Logger LOGGER = LoggerFactory.getLogger(AlertStarter.class);

  @Resource
  private CacheAlertConfig cacheAlertConfig;

  @Resource
  private CacheAlertTask cacheAlertTask;

  @Resource
  private AlertTaskScheduler alertTaskScheduler;

  @Override
  public void afterPropertiesSet() throws Exception {
    try {
      // 启动获取告警配置缓存
      cacheAlertConfig.start();

      // 启动获取告警任务缓存
      cacheAlertTask.start();

      // 启动执行告警任务
      alertTaskScheduler.start();
    } catch (Exception e) {
      LOGGER.error("fail to start alert task for {}", e.getMessage(), e);
    }
  }
}
