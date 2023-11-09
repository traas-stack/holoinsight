/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.facade.emuns.AlertLevel;
import io.holoinsight.server.home.facade.trigger.DataSource;
import io.holoinsight.server.home.facade.trigger.Trigger;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wangsiyuan
 * @date 2022/3/29 20:10
 */
@Data
public class TemplateValue {

  String tenant;

  String workspace;

  String ruleId;

  String uniqueId;

  String ruleName;

  String alarmTime;

  String alarmStartTime;

  String alarmTimestamp;

  String metric;

  AlertLevel alarmLevel;

  String alarmTags;

  String alarmContent;

  String aggregationNum;

  String ruleUrl;

  InspectConfig ruleConfig;

  String alarmTraceId;

  Long alarmHistoryId;

  Long alarmHistoryDetailId;

  Double alertValue;

  String sourceType;

  String alertQuery;

  String metricType;

  Long duration;

  String triggerCondition;

  String alertServer;

  String alertIp;

  String pid;

  /**
   * log content triggered alert
   */
  String logContent;

  public void setAlertQuery(Trigger trigger) {
    StringBuilder query = new StringBuilder();
    query.append("slidingWindow: ").append(trigger.getAggregator()).append("[")
        .append(trigger.getDownsample()).append("]").append(", datasources[");
    if (!CollectionUtils.isEmpty(trigger.getDatasources())) {
      for (DataSource dataSource : trigger.getDatasources()) {
        query.append(buildDataSourceQuery(dataSource));
      }
    }
    query.append("]");
    this.alertQuery = query.toString();
  }

  private String buildDataSourceQuery(DataSource dataSource) {
    StringBuilder query = new StringBuilder();
    query.append("{").append(dataSource.getAggregator()).append("(").append(dataSource.getMetric())
        .append(")").append("[").append(dataSource.getDownsample()).append("]").append("}");
    return query.toString();
  }

  public void setMetricType(Trigger trigger) {
    if (!CollectionUtils.isEmpty(trigger.getDatasources())) {
      List<String> metricTypes = new ArrayList<>();
      for (DataSource dataSource : trigger.getDatasources()) {
        metricTypes.add(dataSource.getMetricType());
      }
      this.metricType = String.join(",", metricTypes);
    }
  }

  public void setTriggerCondition(Trigger trigger) {
    this.triggerCondition = J.toJson(trigger.getCompareConfigs());
  }

  public void setMetric(Trigger trigger) {
    List<String> metrics = new ArrayList<>();
    if (!CollectionUtils.isEmpty(trigger.getDatasources())) {
      for (DataSource dataSource : trigger.getDatasources()) {
        metrics.add(dataSource.getMetric());
      }
    }
    this.metric = String.join(",", metrics);
  }

  public void setMetric(String pql) {
    this.metric = pql;
  }

  public void setLogContentFromLogAnalysis(List<String> logAnalysis) {
    if (CollectionUtils.isEmpty(logAnalysis)) {
      this.logContent = StringUtils.EMPTY;
    } else {
      String json = logAnalysis.get(0);
      Map<String, Object> map = J.toMap(json);
      String sample = (String) map.getOrDefault("sample", "");
      Map<String, Object> ipCountMap =
          (Map<String, Object>) map.getOrDefault("ipCountMap", new HashMap<>());
      sample = sample + " FROM [" + String.join(",", ipCountMap.keySet()) + "]";
      this.logContent = sample;
    }
  }

  public void setLogContentFromLogSample(List<String> logSample) {
    try {
      if (!CollectionUtils.isEmpty(logSample)) {
        String json = logSample.get(0);
        Map<String, Object> map = J.toMap(json);
        List<Map<String, Object>> samples = (List<Map<String, Object>>) map.get("samples");
        if (CollectionUtils.isEmpty(samples)) {
          return;
        }
        Map<String, Object> sample = samples.get(0);
        String hostname = (String) sample.get("hostname");
        List<List<String>> logs = (List<List<String>>) sample.get("logs");
        if (CollectionUtils.isEmpty(logs) || CollectionUtils.isEmpty(logs.get(0))) {
          return;
        }
        String sampleLog = logs.get(0).get(0);
        sampleLog = sampleLog + " FROM [" + hostname + "]";
        this.logContent = sampleLog;
      }
    } finally {
      if (StringUtils.isEmpty(this.logContent)) {
        this.logContent = StringUtils.EMPTY;
      }
    }
  }
}
