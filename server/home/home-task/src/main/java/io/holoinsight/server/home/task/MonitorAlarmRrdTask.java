/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.task;

import io.holoinsight.server.common.model.CLUSTER_ROLE_CONST;
import io.holoinsight.server.common.service.AlarmHistoryDetailService;
import io.holoinsight.server.common.service.AlarmMetricService;
import io.holoinsight.server.common.service.FolderService;
import io.holoinsight.server.common.service.MetricInfoService;
import io.holoinsight.server.home.biz.service.CustomPluginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author limengyang
 * @version MonitorAlarmRrdTask.java, v 0.1 2024年09月19日 15:41 limengyang
 */
@Service
@TaskHandler(code = "MONITOR_ALARM_RRD")
public class MonitorAlarmRrdTask extends AbstractMonitorTask {

  public final static String TASK_ID = "MONITOR_ALARM_RRD";

  public final static Long PERIOD = 5 * MINUTE;

  @Autowired
  private AlarmHistoryDetailService alarmHistoryDetailService;

  @Autowired
  private CustomPluginService customPluginService;

  @Autowired
  private FolderService folderService;

  @Autowired
  private MetricInfoService metricInfoService;

  @Autowired
  private AlarmMetricService alarmMetricService;


  public MonitorAlarmRrdTask() {
    super(1, 10, "MONITOR_ALARM_RRD");
  }

  @Override
  public long getTaskPeriod() {
    return PERIOD;
  }

  @Override
  public boolean needRun() {
    return true;
  }

  @Override
  public List<MonitorTaskJob> buildJobs(long period) {
    List<MonitorTaskJob> jobs = new ArrayList<>();
    jobs.add(new MonitorAlarmRrdTaskJob(period, alarmHistoryDetailService, customPluginService,
        metricInfoService, alarmMetricService, folderService));
    return jobs;
  }

  public String getRole() {
    // 代表执行本任务的具体Role
    return CLUSTER_ROLE_CONST.META;
  }

}
