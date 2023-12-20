/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.task;

import io.holoinsight.server.home.alert.model.compute.ComputeTaskPackage;
import io.holoinsight.server.home.alert.service.calculate.AlertTaskCompute;
import io.holoinsight.server.home.alert.service.event.RecordSucOrFailNotify;
import io.holoinsight.server.home.common.exception.HoloinsightAlertInternalException;
import io.holoinsight.server.home.facade.AlertNotifyRecordDTO;
import io.holoinsight.server.home.facade.InspectConfig;
import io.holoinsight.server.home.facade.emuns.PeriodType;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
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

  @Resource
  private AlertTaskCompute alertTaskCompute;
  private static final String ALERT_TASK_SCHEDULE = "AlertTaskScheduler";

  public void start() {
    try {
      StdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory(getScheduleProperties());
      Scheduler scheduler = stdSchedulerFactory.getScheduler();

      // 创建需要执行的任务
      String jobName = "alert-consume-job";
      JobDetail jobDetail = JobBuilder.newJob(CronProcessJob.class).withIdentity(jobName).build();
      jobDetail.getJobDataMap().put("alertTaskCompute", alertTaskCompute);
      // 创建触发器，指定任务执行时间
      CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity("PerMinTrigger")
          .withSchedule(CronScheduleBuilder.cronSchedule("1 0/1 * * * ?")).build();

      scheduler.scheduleJob(jobDetail, cronTrigger);
      scheduler.start();
      logger.info("[startTaskConsumer] start! ");

    } catch (Exception e) {
      logger.error(
          "[HoloinsightAlertInternalException][AlertTaskScheduler] fail to schedule alert task for {}",
          e.getMessage());
      throw new HoloinsightAlertInternalException(e);
    }
  }

  private Properties getScheduleProperties() {
    Properties properties = new Properties();
    properties.setProperty("org.quartz.scheduler.instanceName", "AlertTaskScheduler");
    properties.setProperty("org.quartz.threadPool.threadCount", "1");
    properties.setProperty("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
    properties.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
    properties.setProperty("org.quartz.threadPool.threadPriority", "5");
    return properties;
  }

  /**
   * 生成的告警Graph
   */
  public static void graphProcess(ComputeTaskPackage computeTaskPackage,
      AlertTaskCompute alertTaskCompute) {
    executorService.execute(() -> {
      // 可根据computeTask区分executor
      generateComputeTask(computeTaskPackage);
      if (!CollectionUtils.isEmpty(computeTaskPackage.getInspectConfigs())) {
        alertTaskCompute.process(computeTaskPackage);
      }
    });
  }

  private static void generateComputeTask(ComputeTaskPackage computeTaskPackage) {
    long current = System.currentTimeMillis();
    long timestamp = PeriodType.MINUTE.rounding(current) - PeriodType.MINUTE.intervalMillis() * 2L;
    if (CollectionUtils.isEmpty(computeTaskPackage.getInspectConfigs())) {
      return;
    }

    List<InspectConfig> inspectConfigs = computeTaskPackage.getInspectConfigs().parallelStream()
        .filter(inspectConfig -> inspectConfig.getTimeFilter().timeIsInMe(timestamp, "GMT+8"))
        .collect(Collectors.toList());

    computeTaskPackage.setTimestamp(timestamp);
    computeTaskPackage.setInspectConfigs(inspectConfigs);
  }

  public static class CronProcessJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
      logger.info("startTaskConsumer execute once.");
      AlertTaskCompute alertTaskCompute =
          (AlertTaskCompute) context.getJobDetail().getJobDataMap().get("alertTaskCompute");
      while (true) {
        AlertNotifyRecordDTO alertNotifyRecordDTO = new AlertNotifyRecordDTO();
        try {
          final ComputeTaskPackage computeTaskPackage = TaskQueueManager.getInstance().poll();
          if (computeTaskPackage != null) {
            alertNotifyRecordDTO = computeTaskPackage.getAlertNotifyRecord();
            RecordSucOrFailNotify.alertNotifyProcessSuc(ALERT_TASK_SCHEDULE, "consume compute task",
                computeTaskPackage.getAlertNotifyRecord());
            graphProcess(computeTaskPackage, alertTaskCompute);
          } else {
            break;
          }
        } catch (Throwable e) {
          RecordSucOrFailNotify.alertNotifyProcessFail(
              "fail to consume compute task for {}" + e.getMessage(), ALERT_TASK_SCHEDULE,
              "consume compute task", alertNotifyRecordDTO);
          logger.error("fail to consume compute task for {}", e.getMessage(), e);
        }
      }
    }
  }


}
