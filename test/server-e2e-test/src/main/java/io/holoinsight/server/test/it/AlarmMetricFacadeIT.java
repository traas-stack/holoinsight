/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import cn.hutool.json.JSONObject;
import io.holoinsight.server.common.J;
import io.holoinsight.server.home.facade.AlarmRuleDTO;
import io.holoinsight.server.home.facade.Rule;
import io.holoinsight.server.home.facade.TimeFilter;
import io.holoinsight.server.home.facade.emuns.BoolOperationEnum;
import io.holoinsight.server.home.facade.emuns.CompareOperationEnum;
import io.holoinsight.server.home.facade.emuns.FunctionEnum;
import io.holoinsight.server.home.facade.emuns.TimeFilterEnum;
import io.holoinsight.server.home.facade.trigger.CompareConfig;
import io.holoinsight.server.home.facade.trigger.CompareParam;
import io.holoinsight.server.home.facade.trigger.DataSource;
import io.holoinsight.server.home.facade.trigger.Filter;
import io.holoinsight.server.home.facade.trigger.Trigger;
import io.restassured.response.Response;

public class AlarmMetricFacadeIT extends BaseIT {

  String name;
  Integer id;
  String tenant;
  String metric;

  Supplier<Response> queryAlertRule = () -> given() //
      .pathParam("ruleId", id) //
      .when() //
      .get("/webapi/alarmRule/query/{ruleId}"); //
  Supplier<Response> queryByMetric = () -> given() //
      .pathParam("metric", metric) //
      .when() //
      .get("/webapi/alarmMetric/query/{metric}"); //

  @Order(1)
  @Test
  public void test_rule_create() {
    await().untilNoException(() -> {
      name = RandomStringUtils.randomAlphabetic(10) + "_alarm_metric_test";
      AlarmRuleDTO alarmRuleDTO = new AlarmRuleDTO();
      alarmRuleDTO.setRuleName(name);
      alarmRuleDTO.setSourceType("apm_holoinsight");
      alarmRuleDTO.setAlarmLevel("5");
      alarmRuleDTO.setRuleDescribe("测试告警规则");
      alarmRuleDTO.setStatus((byte) 1);
      alarmRuleDTO.setIsMerge((byte) 0);
      alarmRuleDTO.setRecover((byte) 0);
      alarmRuleDTO.setRuleType("rule");
      alarmRuleDTO.setTimeFilter(buildTimeFilter());
      alarmRuleDTO.setRule(buildRule());

      Response response = given() //
          .body(new JSONObject(J.toMap(J.toJson(alarmRuleDTO)))) //
          .when() //
          .post("/webapi/alarmRule/create"); //
      System.out.println(response);
    });
  }

  @Order(2)
  @Test
  public void test_query_alarm_metric() {
    metric = "system_cpu_util";
    Response response = queryByMetric.get();
    System.out.println(response.body().print());
    response //
        .then() //
        .body("success", IS_TRUE).body("data", NOT_NULL);
  }

  private Map<String, Object> buildRule() {
    Rule rule = new Rule();
    rule.setBoolOperation(BoolOperationEnum.AND);
    rule.setTriggers(Collections.singletonList(buildTrigger()));
    return J.toMap(J.toJson(rule));
  }

  private Trigger buildTrigger() {
    Trigger trigger = new Trigger();
    trigger.setZeroFill(true);
    trigger.setQuery("a");
    trigger.setType(FunctionEnum.Current);
    trigger.setStepNum(1);
    trigger.setAggregator("avg");
    trigger.setDownsample(2L);
    trigger.setTriggerTitle("触发条件 title");
    trigger.setCompareConfigs(Collections.singletonList(buildCompareConfig()));
    trigger.setTriggerContent("test trigger content");
    trigger.setDatasources(Collections.singletonList(buildDataSource()));
    return trigger;
  }

  private DataSource buildDataSource() {
    Filter filter = new Filter();
    filter.setName("app");
    filter.setType("literal_or");
    filter.setValue("holoinsight-server-example");
    DataSource dataSource = new DataSource();
    dataSource.setMetricType("app");
    dataSource.setMetric("system_cpu_util");
    dataSource.setName("a");
    dataSource.setAggregator("avg");
    dataSource.setDownsample("1m-avg");
    dataSource.setGroupBy(Arrays.asList("hostname"));
    dataSource.setFilters(Collections.singletonList(filter));
    return dataSource;
  }

  private CompareConfig buildCompareConfig() {
    CompareConfig compareConfig = new CompareConfig();
    compareConfig.setTriggerLevel("4");
    compareConfig.setCompareParam(Collections.singletonList(buildCompareParam()));
    return compareConfig;
  }

  private CompareParam buildCompareParam() {
    CompareParam param = new CompareParam();
    param.setCmp(CompareOperationEnum.GTE);
    param.setCmpValue(0d);
    return param;
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
