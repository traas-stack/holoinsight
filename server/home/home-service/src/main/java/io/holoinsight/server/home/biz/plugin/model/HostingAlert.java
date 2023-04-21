/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin.model;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.dal.model.AlarmRule;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import io.holoinsight.server.home.dal.model.dto.IntegrationProductDTO;
import io.holoinsight.server.home.facade.AlarmRuleDTO;
import io.holoinsight.server.home.facade.Rule;
import io.holoinsight.server.home.facade.TimeFilter;
import io.holoinsight.server.home.facade.emuns.AlertLevel;
import io.holoinsight.server.home.facade.emuns.BoolOperationEnum;
import io.holoinsight.server.home.facade.emuns.CompareOperationEnum;
import io.holoinsight.server.home.facade.emuns.FunctionEnum;
import io.holoinsight.server.home.facade.emuns.TimeFilterEnum;
import io.holoinsight.server.home.facade.trigger.CompareConfig;
import io.holoinsight.server.home.facade.trigger.CompareParam;
import io.holoinsight.server.home.facade.trigger.DataSource;
import io.holoinsight.server.home.facade.trigger.Filter;
import io.holoinsight.server.home.facade.trigger.RuleConfig;
import io.holoinsight.server.home.facade.trigger.Trigger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author masaimu
 * @version 2023-02-17 13:56:00
 */
public class HostingAlert {
  public String ruleId;
  public String alertType;
  public String alertMetric;
  public String metricType;
  public String alertMetricAlias;
  public String alertScope;
  public String alertTitle;
  public String downsample;
  public String aggregator;
  public String functionType;
  public String stepNum;
  public String slidingWindowAggregator;
  public String slidingWindowSize;
  public List<String> groupBy;
  public List<Filter> filters;
  public RuleConfig ruleConfig;
  public String tenant;
  public List<CompareConfig> compareConfigs;

  public static HostingAlert parseAlarmRuleVO(AlarmRule alarmRule) {
    Rule rule = J.fromJson(alarmRule.getRule(), Rule.class);
    HostingAlert vo = new HostingAlert();
    vo.ruleId = String.valueOf(alarmRule.getId());
    vo.alertType = alarmRule.getRuleType();
    vo.alertMetric = getMetric(rule);
    vo.alertScope = getScope(rule);
    vo.alertTitle = alarmRule.getRuleName();
    vo.tenant = alarmRule.getTenant();
    return vo;
  }

  private static String getScope(Rule rule) {
    if (CollectionUtils.isEmpty(rule.getTriggers())) {
      return StringUtils.EMPTY;
    }
    Trigger trigger = rule.getTriggers().get(0);
    if (CollectionUtils.isEmpty(trigger.getDatasources())) {
      return StringUtils.EMPTY;
    }
    DataSource dataSource = trigger.getDatasources().get(0);
    List<Filter> filters = dataSource.getFilters();
    if (CollectionUtils.isEmpty(filters)) {
      return StringUtils.EMPTY;
    }
    Map<String, String> map = new HashMap<>();
    for (Filter filter : filters) {
      map.put(filter.getName(), filter.getValue());
    }
    return J.toJson(map);
  }

  private static String getMetric(Rule rule) {
    if (CollectionUtils.isEmpty(rule.getTriggers())) {
      return StringUtils.EMPTY;
    }
    Trigger trigger = rule.getTriggers().get(0);
    if (CollectionUtils.isEmpty(trigger.getDatasources())) {
      return StringUtils.EMPTY;
    }
    DataSource dataSource = trigger.getDatasources().get(0);
    return dataSource.getMetric();
  }

  public AlarmRuleDTO parseAlertRule(IntegrationProductDTO product, IntegrationPluginDTO pluginDTO,
      List<Filter> filters, String sourceType) {
    if (pluginDTO == null) {
      return null;
    }
    AlarmRuleDTO alarmRuleDTO = new AlarmRuleDTO();
    alarmRuleDTO.setRuleName(alertTitle);
    alarmRuleDTO.setRuleType(alertType);
    alarmRuleDTO.setCreator(pluginDTO.creator);
    alarmRuleDTO.setAlarmLevel(AlertLevel.Medium.getCode());
    alarmRuleDTO
        .setRuleDescribe(pluginDTO.getProduct() + " " + getAlertTypeDesc(alertType) + " 告警托管");
    alarmRuleDTO.setStatus((byte) 1);
    alarmRuleDTO.setIsMerge((byte) 1);
    alarmRuleDTO.setRecover((byte) 1);
    alarmRuleDTO.setTenant(pluginDTO.tenant);
    alarmRuleDTO.setRule(buildRule(product, filters));
    alarmRuleDTO.setTimeFilter(buildTimeFilter());
    alarmRuleDTO.setSourceType(sourceType);
    alarmRuleDTO.setSourceId(pluginDTO.id);
    alarmRuleDTO.setEnvType(parseEnvType(pluginDTO.json));
    alarmRuleDTO.setWorkspace(parseWorkspace(pluginDTO.json));

    return alarmRuleDTO;
  }

  private String parseWorkspace(String json) {
    Map<String, Object> map = J.toMap(json);
    return (String) map.get("workspace");
  }

  private String parseEnvType(String json) {
    Map<String, Object> map = J.toMap(json);
    return (String) map.get("envType");
  }

  private String getAlertTypeDesc(String alertType) {
    String desc = "规则";
    if ("ai".equalsIgnoreCase(alertType)) {
      desc = "智能";
    }
    return desc;
  }

  private Map<String, Object> buildTimeFilter() {
    TimeFilter timeFilter = new TimeFilter();
    timeFilter.setModel(TimeFilterEnum.DAY.getDesc());
    timeFilter.setFrom("00:00:00");
    timeFilter.setTo("23:59:59");
    timeFilter.setWeeks(Collections.emptyList());
    return J.toMap(J.toJson(timeFilter));
  }

  private Map<String, Object> buildRule(IntegrationProductDTO product, List<Filter> filters) {
    Rule alertRule = new Rule();
    alertRule.setBoolOperation(BoolOperationEnum.AND);
    alertRule.setTriggers(buildTriggers(product, filters));
    return J.toMap(J.toJson(alertRule));
  }

  private List<Trigger> buildTriggers(IntegrationProductDTO product, List<Filter> filters) {
    Trigger trigger = new Trigger();
    trigger.setQuery("a");
    trigger.setAggregator(slidingWindowAggregator);
    trigger.setDownsample(
        StringUtils.isEmpty(slidingWindowSize) ? 1 : Long.valueOf(slidingWindowSize));
    trigger.setStepNum(StringUtils.isEmpty(stepNum) ? 1 : Integer.valueOf(stepNum));
    trigger.setType(FunctionEnum.valueOf(functionType));
    trigger.setTriggerContent(buildTriggerContent());
    trigger.setDatasources(buildDatasources(filters));
    trigger.setRuleConfig(this.ruleConfig);
    trigger.setCompareConfigs(this.compareConfigs);
    return Arrays.asList(trigger);
  }

  // sample: CPU使用率 最近5个周期平均值大于等于80 周期为一分钟
  protected String buildTriggerContent() {
    StringBuilder content = new StringBuilder();
    if ("ai".equalsIgnoreCase(this.alertType)) {
      content.append(this.alertMetricAlias) //
          .append(this.functionType) //
          .append("智能告警");
    } else {
      content.append(alertMetricAlias) //
          .append("最近").append(stepNum) //
          .append("个周期的").append(slidingWindowAggregator) //
          .append("值").append(parseCompare(compareConfigs)) //
          .append(", 周期为").append(slidingWindowSize) //
          .append("分钟");
    }
    return content.toString();
  }

  private String parseCompare(List<CompareConfig> compareConfigs) {
    if (CollectionUtils.isEmpty(compareConfigs)) {
      return StringUtils.EMPTY;
    }
    List<String> result = new ArrayList<>();
    for (CompareConfig compareConfig : compareConfigs) {
      List<CompareParam> compareParams = compareConfig.getCompareParam();

      List<String> contents = new ArrayList<>();
      for (CompareParam param : compareParams) {
        contents.add(getCompareDesc(param.getCmp()) + param.getCmpValue());
      }
      String condition = String.join(" and ", contents);
      result.add("[" + condition + "]");
    }
    return String.join(" or ", result);
  }

  private String getCompareDesc(CompareOperationEnum cmp) {
    switch (cmp) {
      case EQ:
        return "等于";
      case GT:
        return "大于";
      case LT:
        return "小于";
      case GTE:
        return "大于等于";
      case LTE:
        return "小于等于";
      case NEQ:
        return "不等于";
      default:
        return "";
    }
  }

  private List<DataSource> buildDatasources(List<Filter> filters) {
    DataSource dataSource = new DataSource();
    dataSource.setMetricType(metricType);
    dataSource.setMetric(alertMetric);
    if (!CollectionUtils.isEmpty(groupBy)) {
      dataSource.setGroupBy(groupBy);
    }
    dataSource.setAggregator(aggregator);
    dataSource.setDownsample(downsample);
    dataSource.setName("a");
    dataSource.setFilters(buildFilter(filters));
    return Arrays.asList(dataSource);
  }

  private List<Filter> buildFilter(List<Filter> filters) {
    List<Filter> list = new ArrayList<>();
    if (!CollectionUtils.isEmpty(filters)) {
      list.addAll(filters);
    }
    if (!CollectionUtils.isEmpty(this.filters)) {
      list.addAll(this.filters);
    }
    return list;
  }
}
