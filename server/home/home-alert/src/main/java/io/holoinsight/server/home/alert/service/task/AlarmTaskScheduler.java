/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.task;

import io.holoinsight.server.home.alert.model.compute.ComputeTask;
import io.holoinsight.server.home.alert.model.compute.ComputeTaskPackage;
import io.holoinsight.server.home.alert.model.data.InspectConfig;
import io.holoinsight.server.home.alert.model.emuns.PeriodType;
import io.holoinsight.server.home.alert.service.calculate.AlertTaskCompute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author wangsiyuan
 * @date 2022/2/28 3:34 下午
 */
@Service
public class AlarmTaskScheduler {

  private static final Logger logger = LoggerFactory.getLogger(AlarmTaskScheduler.class);

  private static final ScheduledExecutorService executorService =
      Executors.newScheduledThreadPool(5);

  private static final ScheduledExecutorService syncExecutorService =
      new ScheduledThreadPoolExecutor(1, r -> new Thread(r, "AlarmTaskSubmitScheduler"));

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
        String traceId = null;
        try {
          final ComputeTaskPackage computeTaskPackage = TaskQueueManager.getInstance().poll();
          if (computeTaskPackage != null) {
            graphProcess(computeTaskPackage);
          } else {
            break;
          }
        } catch (Throwable e) {
          logger.error("{},AlarmGraphSchedulerError", traceId);
          logger.error(e.getMessage(), e);
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
    try {
      executorService.schedule(() -> {
        try {
          // 可根据computeTask区分executor
          generateComputeTask(computeTaskPackage);
          if (!CollectionUtils.isEmpty(computeTaskPackage.getComputeTaskList())) {
            alertTaskCompute.process(computeTaskPackage);
          }
        } catch (Throwable e) {
          logger.error("executeTaskError with graph: {}", computeTaskPackage, e);
        }
      }, 0, TimeUnit.SECONDS);
    } catch (Throwable e) {
      logger.error("[DriverGraphSchedulerError] for {}", e.getMessage(), e);
    }
  }

  private void generateComputeTask(ComputeTaskPackage computeTaskPackage) {
    long current = System.currentTimeMillis();
    long timestamp = PeriodType.MINUTE.rounding(current) - PeriodType.MINUTE.intervalMillis() * 2L;
    if (computeTaskPackage != null && computeTaskPackage.getComputeTaskList() != null) {

      List<ComputeTask> computeTaskList =
          computeTaskPackage.getComputeTaskList().parallelStream().filter(computeTask -> {
            List<InspectConfig> inspectConfigs = computeTask.getInspectConfigs().parallelStream()
                .filter(inspectConfig -> inspectConfig.getTimeFilter().timeInMe(timestamp, "GMT+8"))
                .collect(Collectors.toList());
            // 过滤空值
            if (inspectConfigs.isEmpty()) {
              return false;
            } else {
              computeTask.setTimestamp(timestamp);
              computeTask.setInspectConfigs(inspectConfigs);
              return true;
            }
          }).collect(Collectors.toList());
      computeTaskPackage.setComputeTaskList(computeTaskList);
    }

  }
}
