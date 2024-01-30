/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.openai;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.dao.entity.MetricInfo;
import io.holoinsight.server.common.dao.mapper.MetricInfoMapper;
import io.holoinsight.server.home.biz.service.AlertRuleService;
import io.holoinsight.server.home.biz.service.openai.OpenAiService;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.model.openai.AlertRuleType;
import io.holoinsight.server.home.facade.AlarmRuleDTO;
import io.holoinsight.server.home.facade.Rule;
import io.holoinsight.server.home.facade.TimeFilter;
import io.holoinsight.server.home.facade.emuns.AlertLevel;
import io.holoinsight.server.home.facade.emuns.BoolOperationEnum;
import io.holoinsight.server.home.facade.emuns.FunctionEnum;
import io.holoinsight.server.home.facade.emuns.TimeFilterEnum;
import io.holoinsight.server.home.facade.trigger.CompareConfig;
import io.holoinsight.server.home.facade.trigger.CompareParam;
import io.holoinsight.server.home.facade.trigger.DataSource;
import io.holoinsight.server.home.facade.trigger.Trigger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author masaimu
 * @version 2023-06-20 12:39:00
 */
@Service
@Slf4j
public class AlarmRuleFcService {

  @Resource
  private MetricInfoMapper metricInfoMapper;
  @Autowired
  private AlertRuleService alertRuleService;

  @Autowired
  private OpenAiService openAiService;

  public String createAlertRule(Map<String, Object> paramMap) {
    String requestId = (String) paramMap.get("requestId");
    log.info("{} paramMap: {}", requestId, J.toJson(paramMap));
    String ruleName = (String) paramMap.get("ruleName");
    paramMap.get("metric");
    MetricInfo metricInfo = findMetricInfo(requestId, ruleName);
    if (metricInfo == null) {
      return null;
    }
    String ruleType = (String) paramMap.getOrDefault("ruleType", AlertRuleType.rule.name());
    String ruleDescribe = (String) paramMap.get("ruleDescribe");
    List<Map<String, Object>> compareConfigObjs =
        (List<Map<String, Object>>) paramMap.get("compareConfigs");
    List<CompareConfig> compareConfigs =
        J.fromJson(J.toJson(compareConfigObjs), new TypeToken<List<CompareConfig>>() {}.getType());

    MonitorScope ms = RequestContext.getContext().ms;
    MonitorUser mu = RequestContext.getContext().mu;
    AlarmRuleDTO alarmRuleDTO = new AlarmRuleDTO();
    if (null != mu) {
      alarmRuleDTO.setCreator(mu.getLoginName());
    }
    if (null != ms) {
      alarmRuleDTO.setTenant(ms.tenant);
      alarmRuleDTO.setWorkspace(ms.workspace);
    }

    alarmRuleDTO.setGmtCreate(new Date());
    alarmRuleDTO.setGmtModified(new Date());
    alarmRuleDTO.setRuleName(ruleName);
    alarmRuleDTO.setRuleType(ruleType);
    alarmRuleDTO.setAlarmLevel(AlertLevel.Medium.getCode());
    alarmRuleDTO.setRuleDescribe(ruleDescribe);
    alarmRuleDTO.setStatus((byte) 1);
    alarmRuleDTO.setIsMerge((byte) 1);
    alarmRuleDTO.setRecover((byte) 1);
    alarmRuleDTO.setRule(buildRule(compareConfigs, ruleDescribe, metricInfo));
    alarmRuleDTO.setTimeFilter(buildTimeFilter());
    alarmRuleDTO.setSourceType("gpt");
    this.alertRuleService.save(alarmRuleDTO);
    return J.toJson(alarmRuleDTO);
  }

  private MetricInfo findMetricInfo(String requestId, String description) {
    QueryWrapper<MetricInfo> queryWrapper = new QueryWrapper<>();
    List<MetricInfo> metricInfos = this.metricInfoMapper.selectList(queryWrapper);

    if (StringUtils.isBlank(description)) {
      return null;
    }
    String original = "[" + description + "]";

    List<String> combinations = new ArrayList<>();
    Map<String, MetricInfo> metricMap = new HashMap<>();
    for (MetricInfo metricInfo : metricInfos) {
      String descKey = "[" + metricInfo.description + "]";
      combinations.add(descKey + "\n");
      metricMap.put(descKey, metricInfo);
    }

    String content = this.openAiService.getSimilarKey(original, combinations, requestId);
    return metricMap.get(content);
  }

  private Map<String, Object> buildRule(List<CompareConfig> compareConfigs, String ruleDescribe,
      MetricInfo metricInfo) {
    Rule alertRule = new Rule();
    alertRule.setBoolOperation(BoolOperationEnum.AND);
    alertRule.setTriggers(buildTriggers(compareConfigs, ruleDescribe, metricInfo));
    return J.toMap(J.toJson(alertRule));
  }

  private String slidingWindowAggregator = "avg";
  private String slidingWindowSize = null;
  private String stepNum = null;
  private String functionType = null;
  private String aggregator = "none";

  private List<Trigger> buildTriggers(List<CompareConfig> compareConfigs, String ruleDescribe,
      MetricInfo metricInfo) {
    for (CompareConfig compareConfig : compareConfigs) {
      CompareParam compareParam = compareConfig.getSingleCompareParam();
      compareConfig.setCompareParam(Collections.singletonList(compareParam));
      compareConfig.setSingleCompareParam(null);
    }
    Trigger trigger = new Trigger();
    trigger.setQuery("a");
    trigger.setAggregator(slidingWindowAggregator);
    trigger.setDownsample(
        StringUtils.isEmpty(slidingWindowSize) ? 1 : Long.valueOf(slidingWindowSize));
    trigger.setStepNum(StringUtils.isEmpty(stepNum) ? 1 : Integer.valueOf(stepNum));
    trigger.setType(StringUtils.isEmpty(functionType) ? FunctionEnum.Current
        : FunctionEnum.valueOf(functionType));
    trigger.setTriggerContent(ruleDescribe);
    trigger.setDatasources(buildDatasources(metricInfo));
    trigger.setCompareConfigs(compareConfigs);
    return Arrays.asList(trigger);
  }

  private List<DataSource> buildDatasources(MetricInfo metricInfo) {
    DataSource dataSource = new DataSource();
    dataSource.setMetricType(metricInfo.metric);
    dataSource.setMetric(metricInfo.metricTable);

    dataSource.setAggregator(aggregator);
    // dataSource.setDownsample(downsample);
    dataSource.setName("a");
    // dataSource.setFilters(buildFilter(filters));
    return Arrays.asList(dataSource);
  }

  private Map<String, Object> buildTimeFilter() {
    TimeFilter timeFilter = new TimeFilter();
    timeFilter.setModel(TimeFilterEnum.DAY.getDesc());
    timeFilter.setFrom("00:00:00");
    timeFilter.setTo("23:59:59");
    timeFilter.setWeeks(Collections.emptyList());
    return J.toMap(J.toJson(timeFilter));
  }
}
