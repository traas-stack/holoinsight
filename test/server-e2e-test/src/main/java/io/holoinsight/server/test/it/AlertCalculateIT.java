/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.facade.AlarmHistoryDTO;
import io.holoinsight.server.home.facade.AlarmRuleDTO;
import io.holoinsight.server.home.facade.Rule;
import io.holoinsight.server.home.facade.TimeFilter;
import io.holoinsight.server.home.facade.emuns.BoolOperationEnum;
import io.holoinsight.server.home.facade.emuns.CompareOperationEnum;
import io.holoinsight.server.home.facade.emuns.FunctionEnum;
import io.holoinsight.server.home.facade.emuns.PeriodType;
import io.holoinsight.server.home.facade.emuns.TimeFilterEnum;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.trigger.CompareConfig;
import io.holoinsight.server.home.facade.trigger.CompareParam;
import io.holoinsight.server.home.facade.trigger.DataSource;
import io.holoinsight.server.home.facade.trigger.Trigger;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

/**
 * @author masaimu
 * @version 2023-03-23 17:08:00
 */
@Disabled("https://github.com/traas-stack/holoinsight/issues/290")
public class AlertCalculateIT extends BaseIT {

  Long currentId;
  Long periodValueId;
  Long periodRateId;
  Long periodAbsId;
  Long cpId;
  String metricName;

  @Test
  public void test_rule_calculate() {
    String name = RandomStringUtils.randomAlphabetic(6);

    JSONObject body = getJsonFromClasspath("requests/AlertCalculateIT.json");
    body.put("name", body.get("name") + name);
    // Create log monitoring
    cpId = ((Number) given() //
        .body(body) //
        .when() //
        .post("/webapi/customPlugin/create") //
        .then() //
        .body("success", IS_TRUE) //
        .extract() //
        .path("data.id")) //
            .longValue(); //

    metricName = "cost_" + cpId;

    AlarmRuleDTO currentAlarmRule = new AlarmRuleDTO();
    currentAlarmRule.setRuleName("current告警规则测试");
    currentAlarmRule.setSourceType("log");
    currentAlarmRule.setSourceId(cpId);
    currentAlarmRule.setAlarmLevel("5");
    currentAlarmRule.setRuleDescribe("测试告警规则");
    currentAlarmRule.setStatus((byte) 1);
    currentAlarmRule.setIsMerge((byte) 0);
    currentAlarmRule.setRecover((byte) 0);
    currentAlarmRule.setRuleType("rule");
    currentAlarmRule.setTimeFilter(buildTimeFilter());
    currentAlarmRule.setRule(buildCurrentRule());

    // Create current alert
    currentId = ((Number) given() //
        .body(new JSONObject(J.toMap(J.toJson(currentAlarmRule)))) //
        .when() //
        .post("/webapi/alarmRule/create") //
        .then() //
        .body("success", IS_TRUE) //
        .body("data", Matchers.any(Number.class)) //
        .extract() //
        .path("data")) //
            .longValue(); //
    System.out.println(currentId);

    AlarmRuleDTO periodValueAlarmRule = new AlarmRuleDTO();
    periodValueAlarmRule.setRuleName("periodValue告警规则测试");
    periodValueAlarmRule.setSourceType("log");
    periodValueAlarmRule.setSourceId(cpId);
    periodValueAlarmRule.setAlarmLevel("5");
    periodValueAlarmRule.setRuleDescribe("测试告警规则");
    periodValueAlarmRule.setStatus((byte) 1);
    periodValueAlarmRule.setIsMerge((byte) 0);
    periodValueAlarmRule.setRecover((byte) 0);
    periodValueAlarmRule.setRuleType("rule");
    periodValueAlarmRule.setTimeFilter(buildTimeFilter());
    periodValueAlarmRule.setRule(buildPeriodValueRule());

    // Create period value alert
    periodValueId = ((Number) given() //
        .body(new JSONObject(J.toMap(J.toJson(periodValueAlarmRule)))) //
        .when() //
        .post("/webapi/alarmRule/create") //
        .then() //
        .body("success", IS_TRUE) //
        .body("data", Matchers.any(Number.class)) //
        .extract() //
        .path("data")) //
            .longValue(); //
    System.out.println(periodValueId);

    AlarmRuleDTO periodRateAlarmRule = new AlarmRuleDTO();
    periodRateAlarmRule.setRuleName("periodRate告警规则测试");
    periodRateAlarmRule.setSourceType("log");
    periodRateAlarmRule.setSourceId(cpId);
    periodRateAlarmRule.setAlarmLevel("5");
    periodRateAlarmRule.setRuleDescribe("测试告警规则");
    periodRateAlarmRule.setStatus((byte) 1);
    periodRateAlarmRule.setIsMerge((byte) 0);
    periodRateAlarmRule.setRecover((byte) 0);
    periodRateAlarmRule.setRuleType("rule");
    periodRateAlarmRule.setTimeFilter(buildTimeFilter());
    periodRateAlarmRule.setRule(buildPeriodRateRule());

    // Create period rate alert
    periodRateId = ((Number) given() //
        .body(new JSONObject(J.toMap(J.toJson(periodRateAlarmRule)))) //
        .when() //
        .post("/webapi/alarmRule/create") //
        .then() //
        .body("success", IS_TRUE) //
        .body("data", Matchers.any(Number.class)) //
        .extract() //
        .path("data")) //
            .longValue(); //
    System.out.println(periodRateId);

    AlarmRuleDTO periodAbsAlarmRule = new AlarmRuleDTO();
    periodAbsAlarmRule.setRuleName("periodAbs告警规则测试");
    periodAbsAlarmRule.setSourceType("log");
    periodAbsAlarmRule.setSourceId(cpId);
    periodAbsAlarmRule.setAlarmLevel("5");
    periodAbsAlarmRule.setRuleDescribe("测试告警规则");
    periodAbsAlarmRule.setStatus((byte) 1);
    periodAbsAlarmRule.setIsMerge((byte) 0);
    periodAbsAlarmRule.setRecover((byte) 0);
    periodAbsAlarmRule.setRuleType("rule");
    periodAbsAlarmRule.setTimeFilter(buildTimeFilter());
    periodAbsAlarmRule.setRule(buildPeriodAbsRule());

    // Create period abs alert
    periodAbsId = ((Number) given() //
        .body(new JSONObject(J.toMap(J.toJson(periodAbsAlarmRule)))) //
        .when() //
        .post("/webapi/alarmRule/create") //
        .then() //
        .body("success", IS_TRUE) //
        .body("data", Matchers.any(Number.class)) //
        .extract() //
        .path("data")) //
            .longValue(); //
    System.out.println(periodAbsId);

    await("Alert history generation") //
        .atMost(Duration.ofMinutes(10)) //
        .untilNoException(() -> {
          AlarmHistoryDTO condition = new AlarmHistoryDTO();
          condition.setUniqueId("rule_" + currentId);
          MonitorPageRequest<AlarmHistoryDTO> pageRequest = new MonitorPageRequest<>();
          pageRequest.setTarget(condition);
          pageRequest.setPageNum(0);
          pageRequest.setPageSize(3);
          given() //
              .body(new JSONObject(J.toMap(J.toJson(pageRequest)))) //
              .when() //
              .post("/webapi/alarmHistory/pageQuery") //
              .then() //
              .body("success", IS_TRUE) //
              .body("data.items.size()", gt(0));

          condition = new AlarmHistoryDTO();
          condition.setUniqueId("rule_" + periodValueId);
          pageRequest = new MonitorPageRequest<>();
          pageRequest.setTarget(condition);
          pageRequest.setPageNum(0);
          pageRequest.setPageSize(3);
          given() //
              .body(new JSONObject(J.toMap(J.toJson(pageRequest)))) //
              .when() //
              .post("/webapi/alarmHistory/pageQuery") //
              .then() //
              .body("success", IS_TRUE) //
              .body("data.items.size()", gt(0));

          condition = new AlarmHistoryDTO();
          condition.setUniqueId("rule_" + periodAbsId);
          pageRequest = new MonitorPageRequest<>();
          pageRequest.setTarget(condition);
          pageRequest.setPageNum(0);
          pageRequest.setPageSize(3);
          given() //
              .body(new JSONObject(J.toMap(J.toJson(pageRequest)))) //
              .when() //
              .post("/webapi/alarmHistory/pageQuery") //
              .then() //
              .body("success", IS_TRUE) //
              .body("data.items.size()", gt(0));

          condition = new AlarmHistoryDTO();
          condition.setUniqueId("rule_" + periodRateId);
          pageRequest = new MonitorPageRequest<>();
          pageRequest.setTarget(condition);
          pageRequest.setPageNum(0);
          pageRequest.setPageSize(3);
          given() //
              .body(new JSONObject(J.toMap(J.toJson(pageRequest)))) //
              .when() //
              .post("/webapi/alarmHistory/pageQuery") //
              .then() //
              .body("success", IS_TRUE) //
              .body("data.items.size()", gt(0));
        });
  }



  private Map<String, Object> buildPeriodAbsRule() {
    Rule rule = new Rule();
    rule.setBoolOperation(BoolOperationEnum.AND);
    rule.setTriggers(Collections.singletonList(buildPeriodAbsTrigger()));
    return J.toMap(J.toJson(rule));
  }

  private Trigger buildPeriodAbsTrigger() {
    Trigger trigger = new Trigger();
    trigger.setZeroFill(true);
    trigger.setQuery("a");
    trigger.setType(FunctionEnum.PeriodAbs);
    trigger.setStepNum(1);
    trigger.setAggregator("avg");
    trigger.setDownsample(2L);
    trigger.setCompareConfigs(
        Collections.singletonList(buildCompareConfig("periodAbs alert test", 10)));
    trigger.setDatasources(Collections.singletonList(buildDataSource(this.metricName)));
    trigger.setPeriodType(PeriodType.MINUTE);
    return trigger;
  }

  private Map<String, Object> buildPeriodRateRule() {
    Rule rule = new Rule();
    rule.setBoolOperation(BoolOperationEnum.AND);
    rule.setTriggers(Collections.singletonList(buildPeriodRateTrigger()));
    return J.toMap(J.toJson(rule));
  }

  private Trigger buildPeriodRateTrigger() {
    Trigger trigger = new Trigger();
    trigger.setZeroFill(true);
    trigger.setQuery("a");
    trigger.setType(FunctionEnum.PeriodRate);
    trigger.setStepNum(1);
    trigger.setAggregator("avg");
    trigger.setDownsample(2L);
    trigger.setCompareConfigs(
        Collections.singletonList(buildCompareConfig("periodRate alert test", 0.1)));
    trigger.setDatasources(Collections.singletonList(buildDataSource(this.metricName)));
    trigger.setPeriodType(PeriodType.MINUTE);
    return trigger;
  }

  private Map<String, Object> buildPeriodValueRule() {
    Rule rule = new Rule();
    rule.setBoolOperation(BoolOperationEnum.AND);
    rule.setTriggers(Collections.singletonList(buildPeriodValueTrigger()));
    return J.toMap(J.toJson(rule));
  }

  private Trigger buildPeriodValueTrigger() {
    Trigger trigger = new Trigger();
    trigger.setZeroFill(true);
    trigger.setQuery("a");
    trigger.setType(FunctionEnum.PeriodValue);
    trigger.setStepNum(1);
    trigger.setAggregator("avg");
    trigger.setDownsample(2L);
    trigger.setCompareConfigs(
        Collections.singletonList(buildCompareConfig("periodValue alert test", 10)));
    trigger.setDatasources(Collections.singletonList(buildDataSource(this.metricName)));
    trigger.setPeriodType(PeriodType.MINUTE);
    return trigger;
  }

  private Map<String, Object> buildCurrentRule() {
    Rule rule = new Rule();
    rule.setBoolOperation(BoolOperationEnum.AND);
    rule.setTriggers(Collections.singletonList(buildCurrentTrigger()));
    return J.toMap(J.toJson(rule));
  }

  private Trigger buildCurrentTrigger() {
    Trigger trigger = new Trigger();
    trigger.setZeroFill(true);
    trigger.setQuery("a");
    trigger.setType(FunctionEnum.Current);
    trigger.setStepNum(1);
    trigger.setAggregator("avg");
    trigger.setDownsample(2L);
    trigger.setTriggerTitle("触发条件 title");
    trigger
        .setCompareConfigs(Collections.singletonList(buildCompareConfig("current alert test", 10)));
    trigger.setDatasources(Collections.singletonList(buildDataSource(this.metricName)));
    return trigger;
  }

  protected static DataSource buildDataSource(String metricName) {
    DataSource dataSource = new DataSource();
    dataSource.setMetricType("log");
    dataSource.setMetric(metricName);
    dataSource.setName("a");
    dataSource.setAggregator("none");
    return dataSource;
  }

  protected static CompareConfig buildCompareConfig(String triggerContent, double cmpValue) {
    CompareConfig compareConfig = new CompareConfig();
    compareConfig.setTriggerLevel("4");
    compareConfig.setCompareParam(Collections.singletonList(buildCompareParam(cmpValue)));
    compareConfig.setTriggerContent(triggerContent);
    return compareConfig;
  }

  protected static Map<String, Object> buildTimeFilter() {
    TimeFilter timeFilter = new TimeFilter();
    timeFilter.setModel(TimeFilterEnum.DAY.getDesc());
    timeFilter.setFrom("00:00:00");
    timeFilter.setTo("23:59:59");
    timeFilter.setWeeks(Collections.emptyList());
    return J.toMap(J.toJson(timeFilter));
  }

  protected static CompareParam buildCompareParam(double cmpValue) {
    CompareParam param = new CompareParam();
    param.setCmp(CompareOperationEnum.GT);
    param.setCmpValue(cmpValue);
    return param;
  }
}
