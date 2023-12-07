/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.facade.AlarmHistoryDTO;
import io.holoinsight.server.home.facade.AlarmHistoryDetailDTO;
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
import io.holoinsight.server.home.facade.trigger.Filter;
import io.holoinsight.server.home.facade.trigger.Trigger;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.CustomMatcher;
import org.hamcrest.Matchers;
import org.hamcrest.core.Every;
import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Supplier;

/**
 * Alert rule lifecycle integration test.
 *
 * @author masaimu
 * @version 2023-03-17 16:55:00
 */
public class AlertRuleIT extends BaseIT {
  String name;
  Integer id;
  String tenant;

  Supplier<Response> queryAlertRule = () -> given() //
      .pathParam("ruleId", id) //
      .when() //
      .get("/webapi/alarmRule/query/{ruleId}"); //

  @Order(1)
  @Test
  public void test_rule_create() {
    name = RandomStringUtils.randomAlphabetic(10) + "告警规则测试";
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

    // Create folder
    id = given() //
        .body(new JSONObject(J.toMap(J.toJson(alarmRuleDTO)))) //
        .when() //
        .post("/webapi/alarmRule/create") //
        .then() //
        .body("success", IS_TRUE) //
        .body("data", Matchers.any(Number.class)) //
        .extract() //
        .path("data"); //
    System.out.println(id);
    Response response = queryAlertRule.get();
    System.out.println(response.body().print());
    tenant = response //
        .then().log().all() //
        .body("success", IS_TRUE) //
        .root("data") //
        .body("ruleName", eq(name)) //
        .body("alarmContent[0]", eq("test trigger content")) //
        .extract() //
        .path("data.tenant");
    System.out.println(tenant);
  }

  @Order(2)
  @Test
  public void test_triggerContent() {
    AlarmRuleDTO alarmRuleDTO = new AlarmRuleDTO();
    alarmRuleDTO.setRuleName("triggerContent_alert_test");
    alarmRuleDTO.setSourceType("apm_holoinsight");
    alarmRuleDTO.setAlarmLevel("5");
    alarmRuleDTO.setRuleDescribe("测试告警规则");
    alarmRuleDTO.setStatus((byte) 1);
    alarmRuleDTO.setIsMerge((byte) 0);
    alarmRuleDTO.setRecover((byte) 0);
    alarmRuleDTO.setRuleType("rule");
    alarmRuleDTO.setTimeFilter(buildTimeFilter());
    alarmRuleDTO.setRule(buildRuleWithMultiTriggerContent());

    // Create folder
    Integer multiId = given() //
        .body(new JSONObject(J.toMap(J.toJson(alarmRuleDTO)))) //
        .when() //
        .post("/webapi/alarmRule/create") //
        .then() //
        .body("success", IS_TRUE) //
        .body("data", Matchers.any(Number.class)) //
        .extract() //
        .path("data"); //
    System.out.println(multiId);
    Response response = given() //
        .pathParam("ruleId", multiId) //
        .when() //
        .get("/webapi/alarmRule/query/{ruleId}");
    System.out.println(response.body().print());
    response //
        .then() //
        .body("success", IS_TRUE) //
        .root("data") //
        .body("alarmContent[0]", eq("compareConfig_trigger_content_1")) //
        .body("alarmContent[1]", eq("compareConfig_trigger_content_2")) //
        .extract() //
        .path("data.tenant");
  }

  @Order(3)
  @Test
  public void test_check_rule_name() {
    String invalidRuleName =
        RandomStringUtils.randomAlphabetic(10) + "<a href=http://www.baidu.com>点击查看详情</a>";
    AlarmRuleDTO alarmRuleDTO = new AlarmRuleDTO();
    alarmRuleDTO.setRuleName(invalidRuleName);
    alarmRuleDTO.setSourceType("apm_holoinsight");
    alarmRuleDTO.setAlarmLevel("5");
    alarmRuleDTO.setRuleDescribe("Invalid rule name");
    alarmRuleDTO.setStatus((byte) 1);
    alarmRuleDTO.setIsMerge((byte) 0);
    alarmRuleDTO.setRecover((byte) 0);
    alarmRuleDTO.setRuleType("rule");
    alarmRuleDTO.setTimeFilter(buildTimeFilter());
    alarmRuleDTO.setRule(buildRule());

    // Create folder
    given() //
        .body(new JSONObject(J.toMap(J.toJson(alarmRuleDTO)))) //
        .when() //
        .post("/webapi/alarmRule/create") //
        .then() //
        .body("success", IS_FALSE) //
        .body("message", startsWith("API_SECURITY"));

    invalidRuleName = name + "<a href=http://www.baidu.com>点击查看详情</a>";
    alarmRuleDTO = new AlarmRuleDTO();
    alarmRuleDTO.setRuleName(invalidRuleName);
    alarmRuleDTO.setId(id.longValue());
    alarmRuleDTO.setTenant(tenant);
    given() //
        .body(new JSONObject(J.toMap(J.toJson(alarmRuleDTO)))) //
        .when() //
        .post("/webapi/alarmRule/update") //
        .then() //
        .body("success", IS_FALSE) //
        .body("message", startsWith("API_SECURITY"));
    Response response = queryAlertRule.get();
    System.out.println(response.body().print());
    response //
        .then() //
        .body("success", IS_TRUE) //
        .root("data").body("ruleName", eq(name));

  }

  @Order(4)
  @Test
  public void test_rule_update() {
    name = name + "_v2";
    AlarmRuleDTO alarmRuleDTO = new AlarmRuleDTO();
    alarmRuleDTO.setRuleName(name);
    alarmRuleDTO.setId(id.longValue());
    alarmRuleDTO.setTenant(tenant);
    given() //
        .body(new JSONObject(J.toMap(J.toJson(alarmRuleDTO)))) //
        .when() //
        .post("/webapi/alarmRule/update") //
        .then() //
        .body("success", IS_TRUE) //
        .body("data", IS_TRUE);
    Response response = queryAlertRule.get();
    System.out.println(response.body().print());
    response //
        .then() //
        .body("success", IS_TRUE) //
        .root("data").body("ruleName", eq(name));
  }

  @Order(5)
  @Test
  public void test_rule_delete() {
    given() //
        .pathParam("id", id) //
        .when() //
        .delete("/webapi/alarmRule/delete/{id}"); //
    Response response = queryAlertRule.get();
    System.out.println(response.body().print());
    response //
        .then() //
        .body("success", IS_TRUE) //
        .body("data", IS_NULL);
  }

  @Order(6)
  @Test
  public void test_rule_pageQuery() {
    Stack<Integer> ids = new Stack<>();
    for (int i = 0; i < 10; i++) {
      Integer id = given() //
          .body(new JSONObject(J.toMap(J.toJson(buildAlarmRule("hit_rule_" + i))))) //
          .when() //
          .post("/webapi/alarmRule/create") //
          .then() //
          .body("success", IS_TRUE) //
          .extract() //
          .path("data"); //
      ids.push(id);
    }
    for (int i = 0; i < 10; i++) {
      given() //
          .body(new JSONObject(J.toMap(J.toJson(buildAlarmRule("miss_rule_" + i))))) //
          .when() //
          .post("/webapi/alarmRule/create") //
          .then() //
          .body("success", IS_TRUE);
    }
    AlarmRuleDTO condition = new AlarmRuleDTO();
    condition.setRuleName("hit_rule");
    MonitorPageRequest<AlarmRuleDTO> pageRequest = new MonitorPageRequest<>();
    pageRequest.setTarget(condition);
    pageRequest.setPageNum(0);
    pageRequest.setPageSize(3);
    given() //
        .body(new JSONObject(J.toMap(J.toJson(pageRequest)))) //
        .when() //
        .post("/webapi/alarmRule/pageQuery") //
        .then() //
        .body("success", IS_TRUE) //
        .root("data")
        .body("items", new Every<>(new CustomMatcher<AlarmRuleDTO>("page query id equal") {
          @Override
          public boolean matches(Object o) {
            Map<String, Object> item = (Map<String, Object>) o;
            Long queryId = ((Number) item.get("id")).longValue();
            Long id = ids.pop().longValue();
            return queryId.equals(id);
          }
        }));
  }

  @Order(7)
  @Test
  public void test_alert_calculate() {
    Integer ruleId = given() //
        .body(new JSONObject(J.toMap(J.toJson(buildAlarmRule("notification"))))) //
        .when() //
        .post("/webapi/alarmRule/create") //
        .then() //
        .body("success", IS_TRUE) //
        .body("data", Matchers.any(Number.class)) //
        .extract() //
        .path("data"); //
    String uniqueId = "rule_" + ruleId;
    System.out.println(uniqueId);
    await("Test alert history generation") //
        .atMost(Duration.ofMinutes(10)) //
        .untilNoException(() -> {
          AlarmHistoryDTO condition = new AlarmHistoryDTO();
          condition.setUniqueId(uniqueId);
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

          AlarmHistoryDetailDTO detailDTO = new AlarmHistoryDetailDTO();
          detailDTO.setUniqueId(uniqueId);
          MonitorPageRequest<AlarmHistoryDetailDTO> detailPageRequest = new MonitorPageRequest<>();
          long end = System.currentTimeMillis();
          long start = end - PeriodType.MINUTE.intervalMillis() * 10L;
          detailPageRequest.setFrom(start);
          detailPageRequest.setTo(end);
          detailPageRequest.setTarget(detailDTO);
          given() //
              .body(new JSONObject(J.toMap(J.toJson(detailPageRequest)))) //
              .when() //
              .post("/webapi/alarmHistoryDetail/countTrend") //
              .then() //
              .body("success", IS_TRUE) //
              .root("data") //
              .body("results", hasItem(
                  new CustomMatcher<Map<String, Object>>("check alert history detail count") {
                    @Override
                    public boolean matches(Object o) {
                      Map<String, Object> result = (Map<String, Object>) o;
                      List<List<Object>> values = (List<List<Object>>) result.get("values");
                      for (List<Object> item : values) {
                        if (!CollectionUtils.isEmpty(item) && item.size() > 1
                            && item.get(1) instanceof Number) {
                          boolean checkResult = ((Number) item.get(1)).longValue() > 0;
                          if (checkResult) {
                            return true;
                          }
                        }
                      }
                      return false;
                    }
                  }));
        });
  }

  private AlarmRuleDTO buildAlarmRule(String ruleName) {
    AlarmRuleDTO alarmRuleDTO = new AlarmRuleDTO();
    alarmRuleDTO.setRuleName(ruleName);
    alarmRuleDTO.setSourceType("apm_holoinsight");
    alarmRuleDTO.setAlarmLevel("5");
    alarmRuleDTO.setRuleDescribe("测试告警规则");
    alarmRuleDTO.setStatus((byte) 1);
    alarmRuleDTO.setIsMerge((byte) 0);
    alarmRuleDTO.setRecover((byte) 0);
    alarmRuleDTO.setRuleType("rule");
    alarmRuleDTO.setTimeFilter(buildTimeFilter());
    alarmRuleDTO.setRule(buildRule());
    return alarmRuleDTO;
  }

  private Map<String, Object> buildRuleWithMultiTriggerContent() {
    Rule rule = new Rule();
    rule.setBoolOperation(BoolOperationEnum.AND);
    rule.setTriggers(Collections.singletonList(buildTriggerWithMultiTriggerContent()));
    return J.toMap(J.toJson(rule));
  }

  private Trigger buildTriggerWithMultiTriggerContent() {
    Trigger trigger = new Trigger();
    trigger.setZeroFill(true);
    trigger.setQuery("a");
    trigger.setType(FunctionEnum.Current);
    trigger.setStepNum(1);
    trigger.setAggregator("avg");
    trigger.setDownsample(2L);
    trigger.setTriggerTitle("触发条件 title");
    trigger.setCompareConfigs(buildCompareConfigWithMultiTriggerContent());
    trigger.setDatasources(Collections.singletonList(buildDataSource()));
    return trigger;
  }

  private List<CompareConfig> buildCompareConfigWithMultiTriggerContent() {
    CompareConfig compareConfig1 = new CompareConfig();
    compareConfig1.setTriggerLevel("4");
    compareConfig1.setCompareParam(Collections.singletonList(buildCompareParam()));
    compareConfig1.setTriggerContent("compareConfig_trigger_content_1");
    CompareConfig compareConfig2 = new CompareConfig();
    compareConfig2.setTriggerLevel("3");
    compareConfig2.setCompareParam(Collections.singletonList(buildCompareParam()));
    compareConfig2.setTriggerContent("compareConfig_trigger_content_2");
    return Arrays.asList(compareConfig1, compareConfig2);
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
