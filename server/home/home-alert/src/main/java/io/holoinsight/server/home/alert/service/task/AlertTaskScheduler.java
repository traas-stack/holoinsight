/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.task;

import io.holoinsight.server.home.alert.model.compute.ComputeTaskPackage;
import io.holoinsight.server.home.alert.model.data.InspectConfig;
import io.holoinsight.server.home.alert.model.emuns.PeriodType;
import io.holoinsight.server.home.alert.service.calculate.AlertTaskCompute;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author wangsiyuan
 * @date 2022/2/28 3:34 下午
 */
@Service
public class AlertTaskScheduler {

  private static final Logger logger = LoggerFactory.getLogger(AlertTaskScheduler.class);

  private static final ThreadPoolExecutor executorService =
      new ThreadPoolExecutor(5, 5, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100),
          new BasicThreadFactory.Builder().namingPattern("alert-handler").daemon(true).build());

  private static final ScheduledExecutorService syncExecutorService =
      new ScheduledThreadPoolExecutor(1, r -> new Thread(r, "AlertTaskSubmitScheduler"));

  @Resource
  private AlertTaskCompute alertTaskCompute;

  public void start() {
    logger.info("[startTaskConsumer] start! ");

    startTaskConsumer();
    // 定时任务
    syncExecutorService.scheduleAtFixedRate(this::startTaskConsumer, 5, 60, TimeUnit.SECONDS);
    logger.info("[startTaskConsumer] start finish! ");
  }

  protected void startTaskConsumer() {
    Thread graphThread = new Thread(() -> {
      while (true) {
        try {
          final ComputeTaskPackage computeTaskPackage = TaskQueueManager.getInstance().poll();
          if (computeTaskPackage != null) {
            graphProcess(computeTaskPackage);
          } else {
            break;
          }
        } catch (Throwable e) {
          logger.error("fail to consume compute task for {}", e.getMessage(), e);
        }
      }
    });
    graphThread.setName("AlarmTaskScheduler-Thread");
    graphThread.start();
  }

  /**
   * 生成的告警Graph
   */
  public void graphProcess(ComputeTaskPackage computeTaskPackage) {
    executorService.execute(() -> {
      // 可根据computeTask区分executor
      generateComputeTask(computeTaskPackage);
      if (!CollectionUtils.isEmpty(computeTaskPackage.getInspectConfigs())) {
        alertTaskCompute.process(computeTaskPackage);
      }
    });
  }

  private void generateComputeTask(ComputeTaskPackage computeTaskPackage) {
    long current = System.currentTimeMillis();
    long timestamp = PeriodType.MINUTE.rounding(current) - PeriodType.MINUTE.intervalMillis() * 2L;
    if (CollectionUtils.isEmpty(computeTaskPackage.getInspectConfigs())) {
      return;
    }

    List<InspectConfig> inspectConfigs = computeTaskPackage.getInspectConfigs().parallelStream()
        .filter(inspectConfig -> inspectConfig.getTimeFilter().timeInMe(timestamp, "GMT+8"))
        .collect(Collectors.toList());

    computeTaskPackage.setTimestamp(timestamp);
    computeTaskPackage.setInspectConfigs(inspectConfigs);
  }


}
