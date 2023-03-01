/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.facade.emuns.AlertLevel;
import io.holoinsight.server.home.facade.trigger.DataSource;
import io.holoinsight.server.home.facade.trigger.Trigger;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/3/29 8:10 下午
 */
@Data
public class TemplateValue {

  String tenant; // 租户

  String workspace; // workspace

  String ruleId; // 规则id

  String uniqueId; // 唯一id

  String ruleName; // 规则名称

  String alarmTime; // 告警时间

  String alarmStartTime; // 告警开始时间

  String alarmTimestamp; // 告警时间戳

  String metric; // 监控项

  AlertLevel alarmLevel; // 告警级别

  String alarmTags; // 告警对象

  String alarmContent; // 告警内容

  String aggregationNum; // 聚合告警数

  String ruleUrl; // 告警url

  InspectConfig ruleConfig; // 告警配置规则

  String alarmTraceId; // 告警唯一id

  Long alarmHistoryId; // 告警历史 id

  Long alarmHistoryDetailId; // 告警历史明细 id

  Double alertValue; // 告警触发值

  String sourceType; // 来源类型

  String alertQuery; // 告警查询

  String metricType; // 指标类型

  Long duration; // 持续时间

  String triggerCondition; // 触发条件

  String alertServer; // 告警机器

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
}
