/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade;

import io.holoinsight.server.common.J;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author masaimu
 * @version 2023-02-21 15:59:00
 */
public class NotificationTemplate {

  public LinkedHashMap<String /* alias */, AlertTemplateField> fieldMap = new LinkedHashMap<>();
  public LinkedHashMap<String /* alias */, String /* tagk */> tagMap = new LinkedHashMap<>();
  public String text;

  public static NotificationTemplate defaultWebhookTemplate() {
    NotificationTemplate template = new NotificationTemplate();
    template.fieldMap = new LinkedHashMap<>();
    template.fieldMap.put("uniqueId", AlertTemplateField.uniqueId);
    template.fieldMap.put("ruleId", AlertTemplateField.ruleId);
    template.fieldMap.put("ruleName", AlertTemplateField.ruleName);
    template.fieldMap.put("alarmTime", AlertTemplateField.DATE);
    template.fieldMap.put("alarmLevel", AlertTemplateField.ALERT_PRIORITY);
    template.fieldMap.put("alarmContent", AlertTemplateField.EVENT_MSG);
    template.fieldMap.put("alarmTenant", AlertTemplateField.TENANT);
    template.fieldMap.put("metric", AlertTemplateField.ALERT_METRIC);
    template.fieldMap.put("alarmTags", AlertTemplateField.ALERT_SCOPE);
    template.fieldMap.put("aggregationNum", AlertTemplateField.aggregationNum);
    template.fieldMap.put("alarmUrl", AlertTemplateField.ruleUrl);
    template.fieldMap.put("link", AlertTemplateField.LINK);
    template.text = template.getTemplateJson();
    return template;
  }

  public static NotificationTemplate defaultDingtalkTemplate() {
    NotificationTemplate template = new NotificationTemplate();
    template.fieldMap = new LinkedHashMap<>();
    template.fieldMap.put("告警规则名称", AlertTemplateField.ruleName);
    template.fieldMap.put("告警严重度", AlertTemplateField.ALERT_PRIORITY);
    template.fieldMap.put("告警内容", AlertTemplateField.EVENT_MSG);
    template.fieldMap.put("告警对象", AlertTemplateField.ALERT_SCOPE);
    template.fieldMap.put("告警首次触发时间", AlertTemplateField.ALERT_START_TIME);
    template.fieldMap.put("此次评估触发时间", AlertTemplateField.alarmTime);
    template.fieldMap.put("告警触发数值", AlertTemplateField.ALERT_VALUE);
    template.fieldMap.put("日志内容", AlertTemplateField.LOG_CONTENT);
    template.text = template.getTemplateJson();
    return template;
  }

  public static NotificationTemplate defaultMiniappDingtalkTemplate(TemplateValue templateValue) {
    NotificationTemplate template = new NotificationTemplate();
    template.fieldMap = new LinkedHashMap<>();
    template.fieldMap.put("告警规则名称", AlertTemplateField.ruleName);
    template.fieldMap.put("PID", AlertTemplateField.PID);
    template.fieldMap.put("租户名", AlertTemplateField.TENANT_NAME);
    template.fieldMap.put("小程序名", AlertTemplateField.WORKSPACE_NAME);
    template.fieldMap.put("告警严重度", AlertTemplateField.ALERT_PRIORITY);
    template.fieldMap.put("告警内容", AlertTemplateField.EVENT_MSG);
    template.fieldMap.put("告警对象", AlertTemplateField.ALERT_SCOPE);
    template.fieldMap.put("告警首次触发时间", AlertTemplateField.ALERT_START_TIME);
    template.fieldMap.put("此次评估触发时间", AlertTemplateField.alarmTime);
    template.fieldMap.put("告警触发数值", AlertTemplateField.ALERT_VALUE);
    template.fieldMap.put("聚合条数", AlertTemplateField.aggregationNum);
    template.fieldMap.put("告警来源", AlertTemplateField.SOURCE_TYPE);
    if (templateValue != null && StringUtils.isNotEmpty(templateValue.getLogContent())) {
      template.fieldMap.put("日志内容", AlertTemplateField.LOG_CONTENT);
    }
    template.text = template.getTemplateJson();
    return template;
  }

  public String getTemplateJson() {
    if (CollectionUtils.isEmpty(fieldMap)) {
      return StringUtils.EMPTY;
    }
    LinkedHashMap<String, String> templateMap = new LinkedHashMap<>();
    for (Map.Entry<String, AlertTemplateField> entry : this.fieldMap.entrySet()) {
      templateMap.put(entry.getKey(), String.format("${%s}", entry.getValue().getFieldName()));
      if (entry.getValue() == AlertTemplateField.ALERT_SCOPE && !CollectionUtils.isEmpty(tagMap)) {
        for (Map.Entry<String, String> tagItem : tagMap.entrySet()) {
          templateMap.put(tagItem.getKey(),
              String.format("${%s.%s}", entry.getValue().getFieldName(), tagItem.getValue()));
        }
      }
    }
    return J.toJson(templateMap);
  }

  public String getTemplateMarkdown(String textTitle) {
    if (CollectionUtils.isEmpty(fieldMap)) {
      return StringUtils.EMPTY;
    }
    StringBuilder msg = new StringBuilder();
    msg.append("## ").append(textTitle).append("  \n\n\n  ");
    for (Map.Entry<String, AlertTemplateField> entry : this.fieldMap.entrySet()) {
      AlertTemplateField field = entry.getValue();
      if (field == AlertTemplateField.LINK || field == AlertTemplateField.ruleUrl) {
        continue;
      } else if (field == AlertTemplateField.ALERT_SCOPE || field == AlertTemplateField.alarmTags) {
        msg.append(String.format("- **%s**:  \n\n  ", entry.getKey()));
        msg.append(String.format("${%s}", entry.getValue().getFieldName()));
      } else {
        msg.append(String.format("- **%s**: ${%s}  \n\n  ", entry.getKey(),
            entry.getValue().getFieldName()));
      }
    }
    return msg.toString();
  }

  public Map<String, String> getTemplateMap(TemplateValue templateValue, boolean tagMarkdown) {
    if (CollectionUtils.isEmpty(fieldMap)) {
      return Collections.emptyMap();
    }
    Map<String, String> result = new HashMap<>();
    for (Map.Entry<String, AlertTemplateField> entry : this.fieldMap.entrySet()) {
      AlertTemplateField field = entry.getValue();
      String value = getValue(templateValue, field, tagMarkdown);
      if (value == null) {
        value = StringUtils.EMPTY;
      }
      result.put(field.getFieldName(), value);
    }
    return result;
  }

  private String getValue(TemplateValue templateValue, AlertTemplateField field,
      boolean tagMarkdown) {
    switch (field) {
      case ALERT_TRACE_ID:
        return templateValue.alarmTraceId;
      case ALERT_ID:
        return String.valueOf(templateValue.alarmHistoryDetailId);
      case ruleId:
        return templateValue.ruleId;
      case uniqueId:
        return templateValue.uniqueId;
      case ALERT_METRIC:
      case metric:
        return templateValue.metric;
      case ALERT_PRIORITY:
      case alarmLevel:
        if (templateValue.alarmLevel == null) {
          return StringUtils.EMPTY;
        }
        return templateValue.alarmLevel.getDesc();
      case ALERT_QUERY:
        return templateValue.alertQuery;
      case ALERT_SCOPE:
      case alarmTags:
        return buildTagValue(templateValue.alarmTags, tagMarkdown);
      case ALERT_STATUS:
        return buildSummary();
      case ALERT_DURATION:
        return String.valueOf(templateValue.duration);
      case ALERT_VALUE:
        return getAlertValue(templateValue.alertValue);
      case ALERT_TITLE:
        return buildAlertTitle(templateValue.ruleConfig);
      case ruleName:
        return templateValue.ruleName;
      case ALERT_TYPE:
        return templateValue.ruleConfig.getRuleType();
      case DATE:
      case alarmTime:
        return templateValue.alarmTime;
      case ALERT_START_TIME:
        return calculateStartTime(templateValue);
      case alertTimestamp:
      case alarmTimestamp:
        return templateValue.alarmTimestamp;
      case EVENT_MSG:
      case alarmContent:
        return templateValue.alarmContent;
      case EVENT_TYPE:
        return templateValue.metricType;
      case SOURCE_TYPE:
        return templateValue.sourceType;
      case HOSTNAME:
        return templateValue.alertServer;
      case IP:
        return templateValue.alertIp;
      case ALERT_ATTACHMENTS:
        return buildAttachments();
      // case LINK:
      // return link;
      case ruleUrl:
        return templateValue.ruleUrl;
      case TENANT:
      case tenant:
        return templateValue.tenant;
      case WORKSPACE:
        return templateValue.workspace;
      case aggregationNum:
        return templateValue.aggregationNum;
      case ALERT_TRIGGER_CONDITION:
        return templateValue.triggerCondition;
      case LOG_CONTENT:
        return templateValue.logContent;
    }
    return StringUtils.EMPTY;
  }

  private String buildTagValue(String alarmTags, boolean tagMarkdown) {
    if (tagMarkdown) {
      if (StringUtils.isEmpty(alarmTags)) {
        return StringUtils.EMPTY;
      } else if (alarmTags.startsWith("[")) {
        return alarmTags;
      }
      Map<String /* tagk */, String /* alias */> reverseTagMap = reverseTagMap();
      Map<String, Object> tags = J.toMap(alarmTags);
      StringBuilder msg = new StringBuilder();
      for (Map.Entry<String, Object> entry : tags.entrySet()) {
        if (!CollectionUtils.isEmpty(reverseTagMap)) {
          if (reverseTagMap.containsKey(entry.getKey())) {
            msg.append(String.format("    - %s: %s  \n\n  ", reverseTagMap.get(entry.getKey()),
                entry.getValue()));
          }
        } else {
          msg.append(String.format("    - %s: %s  \n\n  ", entry.getKey(), entry.getValue()));
        }
      }
      return msg.toString();
    } else {
      return alarmTags;
    }
  }

  private Map<String, String> reverseTagMap() {
    Map<String /* tagk */, String /* alias */> reverseTagMap = new LinkedHashMap<>();
    for (Map.Entry<String /* alias */, String /* tagk */> tagItem : tagMap.entrySet()) {
      String alias = tagItem.getKey();
      String tagk = tagItem.getValue();
      reverseTagMap.put(tagk, alias);
    }
    return reverseTagMap;
  }

  public static String getAlertValue(Double alertValue) {
    return String.format("%.4f", alertValue);
  }

  private String buildSummary() {
    return StringUtils.EMPTY;
  }

  private String buildAttachments() {
    return StringUtils.EMPTY;
  }

  private String calculateStartTime(TemplateValue templateValue) {
    if ((StringUtils.isBlank(templateValue.alarmTimestamp)
        || !StringUtils.isNumeric(templateValue.alarmTimestamp))
        || templateValue.duration == null) {
      return StringUtils.EMPTY;
    }
    long timestamp = Long.parseLong(templateValue.alarmTimestamp);
    long duration = templateValue.duration * 60000L;
    return DateUtil.getDateOf_YYMMDD_HHMMSS(new Date(timestamp - duration));
  }

  private String buildAlertTitle(InspectConfig ruleConfig) {
    if (ruleConfig == null) {
      return StringUtils.EMPTY;
    }
    return ruleConfig.getRule().getTriggers().get(0).getTriggerTitle();
  }

  private static final Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");

  public boolean parseText() {
    if (StringUtils.isEmpty(this.text)) {
      return true;
    }
    try {
      Map<String, Object> map = J.toMap(this.text);
      for (Map.Entry<String, Object> entry : map.entrySet()) {
        String alias = entry.getKey();
        String fieldName = (String) entry.getValue();
        if (StringUtils.isBlank(fieldName)) {
          continue;
        }
        Matcher matcher = pattern.matcher(fieldName);
        if (matcher.find()) {
          fieldName = matcher.group(1); // group(1) corresponds to (.*?), which is the content
          // within ${}
        }
        if (fieldName.startsWith("ALERT_SCOPE.") || fieldName.startsWith("alarmTags.")) {
          tagMap.put(alias, fieldName.split("\\.", 2)[1]);
        } else {
          fieldMap.put(alias, AlertTemplateField.valueOf(fieldName));
        }
      }
    } catch (Exception e) {
      return false;
    }
    return true;
  }
}
