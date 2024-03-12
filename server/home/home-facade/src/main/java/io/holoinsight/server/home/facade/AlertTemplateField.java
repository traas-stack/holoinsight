/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade;

/**
 * @author masaimu
 * @version 2023-02-21 17:09:00
 */
public enum AlertTemplateField {
  ALERT_TRACE_ID("ALERT_TRACE_ID", "告警traceId", "string"), //
  ALERT_ID("ALERT_ID", "告警结果id", "string"), //
  ruleId("ruleId", "告警规则id", "string"), //
  uniqueId("uniqueId", "规则类型+规则id", "string"), //
  ALERT_METRIC("ALERT_METRIC", "告警指标", "string"), //
  metric("metric", "告警指标", "string", true), //
  ALERT_PRIORITY("ALERT_PRIORITY", "告警等级", "string"), //
  alarmLevel("alarmLevel", "告警等级", "string", true), //
  ALERT_QUERY("ALERT_QUERY", "告警数据源查询", "string"), //
  ALERT_SCOPE("ALERT_SCOPE", "告警对象", "string"), //
  alarmTags("alarmTags", "告警对象", "string", true), //
  ALERT_STATUS("ALERT_STATUS", "告警检测概要", "string"), //
  ALERT_DURATION("ALERT_DURATION", "持续时间(单位：sec)", "numeric"), //
  ALERT_VALUE("ALERT_VALUE", "告警触发数值", "numeric"), //
  ALERT_TITLE("ALERT_TITLE", "告警名称", "string"), //
  ruleName("ruleName", "规则名称", "string"), //
  ALERT_TYPE("ALERT_TYPE", "告警类型", "string"), //
  DATE("DATE", "告警时间(yyyy-MM-dd HH:mm:ss)", "string"), //
  ALERT_START_TIME("ALERT_START_TIME", "告警首次触发时间(yyyy-MM-dd HH:mm:ss)", "string"), //
  alarmTime("alarmTime", "告警时间(yyyy-MM-dd HH:mm:ss)", "string", true), //
  alertTimestamp("alertTimestamp", "告警时间戳(Example: 1406662672000)", "numeric"), //
  alarmTimestamp("alarmTimestamp", "告警时间戳(Example: 1406662672000)", "numeric", true), //
  EVENT_MSG("EVENT_MSG", "告警内容", "string"), //
  alarmContent("alarmContent", "告警内容", "string", true), //
  EVENT_TYPE("EVENT_TYPE", "指标类型", "string"), //
  HOSTNAME("HOSTNAME", "告警计算节点", "string"), //
  IP("IP", "告警计算节点ip", "string"), //
  ALERT_ATTACHMENTS("ALERT_ATTACHMENTS", "告警付文(json)", "string"), //
  LINK("LINK", "监控链接", "string"), //
  ruleUrl("ruleUrl", "告警规则链接", "string"), //
  TENANT("TENANT", "租户", "string"), //
  tenant("tenant", "租户", "string", true), //
  WORKSPACE("WORKSPACE", "workspace", "string"), //
  aggregationNum("aggregationNum", "告警聚合数量", "numeric"), //
  ALERT_TRIGGER_CONDITION("ALERT_TRIGGER_CONDITION", "告警触发条件", "string"), //
  SOURCE_TYPE("SOURCE_TYPE", "告警来源类型", "string"), //
  LOG_CONTENT("LOG_CONTENT", "告警来源日志采样内容", "string"), //
  TENANT_NAME("TENANT_NAME", "租户名", "string"), //
  WORKSPACE_NAME("WORKSPACE_NAME", "工作空间名", "string"), //
  PID("PID", "pid", "string"), //

  ;

  private String fieldName;
  private String describe;
  private String format;
  private boolean compatible;

  AlertTemplateField(String fieldName, String describe, String format) {
    this.fieldName = fieldName;
    this.describe = describe;
    this.format = format;
    this.compatible = false;
  }

  AlertTemplateField(String fieldName, String describe, String format, boolean compatible) {
    this.fieldName = fieldName;
    this.describe = describe;
    this.format = format;
    this.compatible = compatible;
  }

  public String getFieldName() {
    return fieldName;
  }

  public String getDescribe() {
    return describe;
  }

  public String getFormat() {
    return format;
  }

  public boolean isCompatible() {
    return compatible;
  }

  public static AlertTemplateField parse(String fieldName) {
    try {
      return AlertTemplateField.valueOf(fieldName);
    } catch (Exception e) {
      return null;
    }
  }
}
