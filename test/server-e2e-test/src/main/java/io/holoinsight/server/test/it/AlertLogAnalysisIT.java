/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.facade.AlarmHistoryDetailDTO;
import io.holoinsight.server.home.facade.AlarmRuleDTO;
import io.holoinsight.server.home.facade.Rule;
import io.holoinsight.server.home.facade.emuns.BoolOperationEnum;
import io.holoinsight.server.home.facade.emuns.FunctionEnum;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.trigger.Trigger;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.CustomMatcher;
import org.hamcrest.Matchers;
import org.hamcrest.core.Every;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import static io.holoinsight.server.test.it.AlertCalculateIT.buildCompareConfig;
import static io.holoinsight.server.test.it.AlertCalculateIT.buildDataSource;
import static io.holoinsight.server.test.it.AlertCalculateIT.buildTimeFilter;

/**
 * @author masaimu
 * @version 2023-05-04 15:37:00
 */
public class AlertLogAnalysisIT extends BaseIT {

  @Test
  public void test_log_analysis_rule() {
    // String name = RandomStringUtils.randomAlphabetic(6);
    //
    // JSONObject body = getJsonFromClasspath("requests/LogAnalysisIT.json");
    // body.put("name", body.get("name") + name);
    // // Create log monitoring
    // Long cpId = ((Number) given() //
    // .body(body) //
    // .when() //
    // .post("/webapi/customPlugin/create") //
    // .then().log().all() //
    // .body("success", IS_TRUE) //
    // .extract() //
    // .path("data.id")) //
    // .longValue(); //
    //
    // String metricName = "loganalysic_count_" + cpId;
    //
    // AlarmRuleDTO currentAlarmRule = new AlarmRuleDTO();
    // currentAlarmRule.setRuleName("loganalysis " + name + " 告警规则测试");
    // currentAlarmRule.setSourceType("log");
    // currentAlarmRule.setSourceId(cpId);
    // currentAlarmRule.setAlarmLevel("5");
    // currentAlarmRule.setRuleDescribe("测试告警规则");
    // currentAlarmRule.setStatus((byte) 1);
    // currentAlarmRule.setIsMerge((byte) 0);
    // currentAlarmRule.setRecover((byte) 0);
    // currentAlarmRule.setRuleType("rule");
    // currentAlarmRule.setTimeFilter(buildTimeFilter());
    // currentAlarmRule.setRule(buildLogAnalysisRule(metricName));
    // currentAlarmRule.setSourceType("log");
    // currentAlarmRule.setSourceId(cpId);
    //
    // // Create current alert
    // Long alertId = ((Number) given() //
    // .body(new JSONObject(J.toMap(J.toJson(currentAlarmRule)))).log().all() //
    // .when() //
    // .post("/webapi/alarmRule/create") //
    // .then() //
    // .body("success", IS_TRUE) //
    // .body("data", Matchers.any(Number.class)) //
    // .extract() //
    // .path("data")) //
    // .longValue(); //
    // System.out.println(alertId);
    //
    //
    // await("Alert history detail generation") //
    // .atMost(Duration.ofMinutes(10)) //
    // .untilNoException(() -> {
    // AlarmHistoryDetailDTO condition = new AlarmHistoryDetailDTO();
    // condition.setUniqueId("rule_" + alertId);
    // MonitorPageRequest<AlarmHistoryDetailDTO> pageRequest = new MonitorPageRequest<>();
    // pageRequest.setTarget(condition);
    // pageRequest.setPageNum(0);
    // pageRequest.setPageSize(3);
    // given() //
    // .body(new JSONObject(J.toMap(J.toJson(pageRequest)))) //
    // .when() //
    // .post("/webapi/alarmHistoryDetail/pageQuery") //
    // .then() //
    // .body("success", IS_TRUE) //
    // .body("data.items.size()", gt(0)) //
    // .root("data") //
    // .body("items",
    // new Every<>(new CustomMatcher<Map<String, Object>>("check extra config") {
    // @Override
    // public boolean matches(Object o) {
    // Map<String, Object> item = (Map<String, Object>) o;
    // Object extra = item.get("extra");
    // return extra != null;
    // }
    // }));
    // });
  }

  private Map<String, Object> buildLogAnalysisRule(String metricName) {
    Rule rule = new Rule();
    rule.setBoolOperation(BoolOperationEnum.AND);
    rule.setTriggers(Collections.singletonList(buildLogAnalysisTrigger(metricName)));
    return J.toMap(J.toJson(rule));
  }

  private Trigger buildLogAnalysisTrigger(String metricName) {
    Trigger trigger = new Trigger();
    trigger.setZeroFill(true);
    trigger.setQuery("a");
    trigger.setType(FunctionEnum.Current);
    trigger.setStepNum(1);
    trigger.setAggregator("avg");
    trigger.setDownsample(2L);
    trigger.setTriggerTitle("loganalysis 触发条件 title");
    trigger
        .setCompareConfigs(Collections.singletonList(buildCompareConfig("current alert test", 0)));
    trigger.setDatasources(Collections.singletonList(buildDataSource(metricName)));
    return trigger;
  }
}
