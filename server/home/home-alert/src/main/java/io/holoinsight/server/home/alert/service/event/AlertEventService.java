/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.event;

import io.holoinsight.server.home.alert.model.event.AlertNotify;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author wangsiyuan
 * @date 2022/3/28 8:58 下午
 */
@Service
public class AlertEventService {

  private static final ThreadPoolExecutor executorService = new ThreadPoolExecutor(20, 20, 3,
      TimeUnit.SECONDS, new ArrayBlockingQueue<>(100),
      new BasicThreadFactory.Builder().namingPattern("alert-event-handler").daemon(true).build());

  private static final Logger logger = LoggerFactory.getLogger(AlertEventService.class);

  @Autowired
  private AlertServiceRegistry alertServiceRegistry;

  /**
   * 生成的告警Graph
   */
  public void handleEvent(List<AlertNotify> alarmNotifies) {
    executorService.execute(() -> {
      // 获取数据处理管道
      List<? extends AlertHandlerExecutor> pipeline =
          alertServiceRegistry.getAlertEventHanderList();

      if (CollectionUtils.isEmpty(pipeline)) {
        logger.warn("AlertEventHanderList pipeline is empty.");
        return;
      }

      for (AlertHandlerExecutor handler : pipeline) {
        // 当前处理器处理数据，并返回是否继续向下处理
        handler.handle(alarmNotifies);
      }
    });
  }
}
