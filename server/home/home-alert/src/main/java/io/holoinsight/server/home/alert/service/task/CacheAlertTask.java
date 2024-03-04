/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.holoinsight.server.common.dao.entity.MetricInfo;
import io.holoinsight.server.common.dao.mapper.MetricInfoMapper;
import io.holoinsight.server.home.alert.model.compute.ComputeTaskPackage;
import io.holoinsight.server.home.alert.service.converter.DoConvert;
import io.holoinsight.server.home.alert.service.data.CacheData;
import io.holoinsight.server.home.alert.service.event.RecordSucOrFailNotify;
import io.holoinsight.server.home.dal.mapper.AlarmRuleMapper;
import io.holoinsight.server.home.dal.model.AlarmRule;
import io.holoinsight.server.home.facade.AlertNotifyRecordDTO;
import io.holoinsight.server.home.facade.InspectConfig;
import io.holoinsight.server.home.facade.NotifyStage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wangsiyuan
 * @date 2022/2/22 4:35 下午
 */
@Service
public class CacheAlertTask {

  private static Logger LOGGER = LoggerFactory.getLogger(CacheAlertTask.class);

  private static final ScheduledExecutorService syncExecutorService =
      new ScheduledThreadPoolExecutor(2, r -> new Thread(r, "CacheAlarmTaskScheduled"));

  private static final Integer LIMIT = 5000;
  protected final AtomicInteger rulePageSize = new AtomicInteger();
  protected final AtomicInteger rulePageNum = new AtomicInteger();
  protected final AtomicInteger aiPageSize = new AtomicInteger();
  protected final AtomicInteger aiPageNum = new AtomicInteger();
  protected final AtomicInteger pqlPageSize = new AtomicInteger();
  protected final AtomicInteger pqlPageNum = new AtomicInteger();

  @Resource
  protected AlarmRuleMapper alarmRuleDOMapper;

  @Resource
  private CacheData cacheData;

  @Resource
  private MetricInfoMapper metricInfoMapper;
  private Map<String, MetricInfo> logPatternCache = new HashMap<>();
  private Map<String, MetricInfo> logSampleCache = new HashMap<>();

  private static final String ALARM_CONFIG = "CacheAlertTask";

  public void start() {
    LOGGER.info("[AlarmConfig] start alarm config syn!");

    syncExecutorService.scheduleAtFixedRate(this::getAlarmTaskCache, 0, 60, TimeUnit.SECONDS);
    LOGGER.info("[AlarmConfig] alarm config sync finish!");
  }

  private void getAlarmTaskCache() {
    try {
      loadLogMetric();
      LOGGER.info("complete to loadLogMetric, logPatternCache size {} logSampleCache size {}",
          logPatternCache.size(), logSampleCache.size());
      // Get alert detection tasks
      List<AlarmRule> alarmRuleDOS = getAlarmRuleListByPage();
      LOGGER.info("complete to getAlarmRuleListByPage, AlarmRule size {} ", alarmRuleDOS.size());
      ComputeTaskPackage computeTaskPackage = convert(alarmRuleDOS);
      LOGGER.info("complete to convert, computeTaskPackage inspectConfigs size {} ",
          CollectionUtils.isEmpty(computeTaskPackage.getInspectConfigs()) ? 0
              : computeTaskPackage.getInspectConfigs().size());
      TaskQueueManager.getInstance().offer(computeTaskPackage);
      LOGGER.info("complete to offer TaskQueueManager");
    } catch (Exception e) {
      LOGGER.error("InspectCtlParam Sync Exception", e);
    }
  }

  private void loadLogMetric() {
    QueryWrapper<MetricInfo> logPatternQuery = new QueryWrapper<>();
    logPatternQuery.eq("metric_type", "logpattern");
    logPatternQuery.eq("deleted", false);
    List<MetricInfo> metricInfos = this.metricInfoMapper.selectList(logPatternQuery);
    if (!CollectionUtils.isEmpty(metricInfos)) {
      for (MetricInfo metricInfo : metricInfos) {
        if (StringUtils.isEmpty(metricInfo.metricTable)) {
          continue;
        }
        logPatternCache.put(metricInfo.metricTable, metricInfo);
      }
    }

    QueryWrapper<MetricInfo> logSampleQuery = new QueryWrapper<>();
    logSampleQuery.eq("metric_type", "logsample");
    logSampleQuery.eq("deleted", false);
    metricInfos = this.metricInfoMapper.selectList(logSampleQuery);
    if (!CollectionUtils.isEmpty(metricInfos)) {
      for (MetricInfo metricInfo : metricInfos) {
        if (StringUtils.isEmpty(metricInfo.metricTable)) {
          continue;
        }
        logSampleCache.put(metricInfo.metricTable, metricInfo);
      }
    }
  }

  public ComputeTaskPackage convert(List<AlarmRule> alarmRules) {
    ComputeTaskPackage computeTaskPackage = new ComputeTaskPackage();
    computeTaskPackage.setTraceId(UUID.randomUUID().toString());
    List<InspectConfig> inspectConfigs = new ArrayList<>();
    Map<String /* metricTable */, List<InspectConfig>> logInspectConfigs = new HashMap<>();
    Map<String, InspectConfig> uniqueIdMap = new HashMap<>();
    buildAlertNotifyRecord(computeTaskPackage);

    try {
      if (!CollectionUtils.isEmpty(alarmRules)) {
        RecordSucOrFailNotify.alertNotifyProcessSuc(ALARM_CONFIG, "query alarmRule",
            computeTaskPackage.getAlertNotifyRecord());
        for (AlarmRule alarmRule : alarmRules) {
          try {
            InspectConfig inspectConfig = DoConvert.alarmRuleConverter(alarmRule);
            if (nonTemplate(alarmRule) && enableAlert(inspectConfig)) {
              // cache
              if (!CollectionUtils.isEmpty(inspectConfig.getMetrics())) {
                for (String metric : inspectConfig.getMetrics()) {
                  List<InspectConfig> configs =
                      logInspectConfigs.computeIfAbsent(metric, k -> new ArrayList<>());
                  configs.add(inspectConfig);
                }
              }
              uniqueIdMap.put(inspectConfig.getUniqueId(), inspectConfig);
              inspectConfigs.add(inspectConfig);
            }
          } catch (Exception e) {
            LOGGER.error("{} [CRITICAL] rule id {} fail to convert alarmRule",
                computeTaskPackage.getTraceId(), alarmRule.getId(), e);
          }
        }
      }
      supplementLogConfig(logInspectConfigs);
      cacheData.setUniqueIdMap(uniqueIdMap);
      if (inspectConfigs.size() != 0) {
        computeTaskPackage.setInspectConfigs(inspectConfigs);
      }
      RecordSucOrFailNotify.alertNotifyProcessSuc(ALARM_CONFIG, "convert alarmRule",
          computeTaskPackage.getAlertNotifyRecord());
    } catch (Exception e) {
      RecordSucOrFailNotify.alertNotifyProcessFail("fail to convert alarmRule", ALARM_CONFIG,
          "convert alarmRule", computeTaskPackage.getAlertNotifyRecord());
      LOGGER.error("{} [CRITICAL] fail to convert alarmRules", computeTaskPackage.getTraceId(), e);
    }
    return computeTaskPackage;
  }

  // not alarm rule template
  private boolean nonTemplate(AlarmRule alarmRule) {
    return !StringUtils.equals(alarmRule.getSourceType(), "template");
  }

  private void buildAlertNotifyRecord(ComputeTaskPackage computeTaskPackage) {
    if (Objects.nonNull(computeTaskPackage)) {
      AlertNotifyRecordDTO alertNotifyRecord = new AlertNotifyRecordDTO();
      alertNotifyRecord.setTraceId(computeTaskPackage.getTraceId());
      alertNotifyRecord.setGmtCreate(new Date());
      alertNotifyRecord.setIsSuccess((byte) 1);
      NotifyStage notifyStage = new NotifyStage();
      notifyStage.setStage(ALARM_CONFIG);
      List<NotifyStage> notifyStageList = new ArrayList<>();
      notifyStageList.add(notifyStage);
      alertNotifyRecord.setNotifyStage(notifyStageList);
      computeTaskPackage.setAlertNotifyRecord(alertNotifyRecord);
      LOGGER.info("build alert notify record ,record data is: {}", alertNotifyRecord);
    }

  }

  private void supplementLogConfig(
      Map<String /* metricTable */, List<InspectConfig>> logInspectConfigs) {
    if (CollectionUtils.isEmpty(logInspectConfigs)) {
      return;
    }
    for (Map.Entry<String /* metricTable */, List<InspectConfig>> entry : logInspectConfigs
        .entrySet()) {
      String metricTable = entry.getKey();
      List<InspectConfig> list = entry.getValue();
      if (this.logPatternCache.containsKey(metricTable)) {
        for (InspectConfig inspectConfig : list) {
          inspectConfig.setLogPatternEnable(true);
        }
      }
      if (this.logSampleCache.containsKey(metricTable)) {
        for (InspectConfig inspectConfig : list) {
          inspectConfig.setLogSampleEnable(true);
        }
      }
    }
  }

  private boolean enableAlert(InspectConfig inspectConfig) {
    Boolean status = inspectConfig.getStatus();
    return status != null && status;
  }

  public Integer ruleSize(String ruleType) {
    QueryWrapper<AlarmRule> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("rule_type", ruleType);
    return this.alarmRuleDOMapper.selectCount(queryWrapper).intValue();
  }

  protected List<AlarmRule> getAlarmRuleListByPage() {
    List<AlarmRule> alarmRules = new ArrayList<>();
    int retry = 0;
    while (retry++ < 3 && CollectionUtils.isEmpty(alarmRules)) {
      List<AlarmRule> ruleAlerts =
          getAlertRule("rule", this.rulePageNum.get(), this.rulePageSize.get());
      List<AlarmRule> aiAlerts = getAlertRule("ai", this.aiPageNum.get(), this.aiPageSize.get());
      List<AlarmRule> pqlAlerts =
          getAlertRule("pql", this.pqlPageNum.get(), this.pqlPageSize.get());
      if (!CollectionUtils.isEmpty(ruleAlerts)) {
        alarmRules.addAll(ruleAlerts);
      }
      if (!CollectionUtils.isEmpty(aiAlerts)) {
        alarmRules.addAll(aiAlerts);
      }
      if (!CollectionUtils.isEmpty(pqlAlerts)) {
        alarmRules.addAll(pqlAlerts);
      }
      LOGGER.info("try to get AlarmRuleList retry {}, size {}", retry, alarmRules.size());
    }
    return alarmRules;
  }

  private List<AlarmRule> getAlertRule(String ruleType, int pageNum, int pageSize) {
    if (pageSize <= 0) {
      return Collections.emptyList();
    }

    QueryWrapper<AlarmRule> condition = new QueryWrapper<>();
    condition.orderByDesc("id");
    condition.eq("rule_type", ruleType);
    condition.last("limit " + pageSize + " offset " + pageNum);
    List<AlarmRule> alarmRuleDOS = alarmRuleDOMapper.selectList(condition);
    LOGGER.info("TASK_GET_MONITOR,ruleType={},pageNum={},pageSize={},size={}", ruleType, pageNum,
        pageSize, CollectionUtils.isEmpty(alarmRuleDOS) ? 0 : alarmRuleDOS.size());
    return alarmRuleDOS;
  }

  public void setRulePageSize(int size) {
    this.rulePageSize.set(size);
  }

  public void setRulePageNum(int num) {
    this.rulePageNum.set(num);
  }

  public void setAiPageSize(int size) {
    this.aiPageSize.set(size);
  }

  public void setAiPageNum(int num) {
    this.aiPageNum.set(num);
  }

  public void setPqlPageSize(int size) {
    this.pqlPageSize.set(size);
  }

  public void setPqlPageNum(int num) {
    this.pqlPageNum.set(num);
  }
}
