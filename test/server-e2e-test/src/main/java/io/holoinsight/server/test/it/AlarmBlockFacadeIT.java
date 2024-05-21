/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import io.holoinsight.server.common.J;
import io.holoinsight.server.common.dao.entity.dto.AlarmBlockDTO;
import io.holoinsight.server.common.MonitorPageRequest;
import io.holoinsight.server.common.dao.entity.dto.AlarmRuleDTO;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.CustomMatcher;
import org.hamcrest.Matchers;
import org.hamcrest.core.Every;
import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Stack;
import java.util.function.Supplier;

import static io.holoinsight.server.test.it.AlertRuleIT.buildRule;
import static io.holoinsight.server.test.it.AlertRuleIT.buildTimeFilter;

public class AlarmBlockFacadeIT extends BaseIT {
  Long id;
  Integer ruleId;
  String tenant;
  String uniqueId;



  Supplier<Response> queryById = () -> given() //
      .pathParam("id", id) //
      .when() //
      .get("/webapi/alarmBlock/query/{id}"); //

  @Order(1)
  @Test
  public void test_rule_create() {
    String name = RandomStringUtils.randomAlphabetic(10) + "告警屏蔽测试用的炮灰规则";
    AlarmRuleDTO alarmRuleDTO = new AlarmRuleDTO();
    alarmRuleDTO.setRuleName(name);
    alarmRuleDTO.setSourceType("apm_holoinsight");
    alarmRuleDTO.setAlarmLevel("5");
    alarmRuleDTO.setRuleDescribe("告警屏蔽测试用的炮灰规则");
    alarmRuleDTO.setStatus((byte) 1);
    alarmRuleDTO.setIsMerge((byte) 0);
    alarmRuleDTO.setRecover((byte) 0);
    alarmRuleDTO.setRuleType("rule");
    alarmRuleDTO.setTimeFilter(buildTimeFilter());
    alarmRuleDTO.setRule(buildRule());

    // Create folder
    ruleId = given() //
        .body(new JSONObject(J.toMap(J.toJson(alarmRuleDTO)))) //
        .when() //
        .post("/webapi/alarmRule/create") //
        .prettyPeek() //
        .then() //
        .body("success", IS_TRUE) //
        .body("data", Matchers.any(Number.class)) //
        .extract() //
        .path("data"); //

  }

  @Order(2)
  @Test
  public void test_alarm_block_create() {
    uniqueId = "rule_" + ruleId;
    AlarmBlockDTO item = new AlarmBlockDTO();
    item.setUniqueId(uniqueId);
    // Create custom_plugin
    id = ((Number) given() //
        .body(new JSONObject(J.toMap(J.toJson(item)))) //
        .when() //
        .post("/webapi/alarmBlock/create") //
        .then() //
        .body("success", IS_TRUE) //
        .extract() //
        .path("data")).longValue();
    System.out.println(id);
    Response response = queryById.get();
    System.out.println(response.body().print());
    tenant = response //
        .then() //
        .body("success", IS_TRUE) //
        .root("data").body("uniqueId", eq(uniqueId)) //
        .extract() //
        .path("data.tenant");
    System.out.println(tenant);
  }

  @Order(3)
  @Test
  public void test_alarm_block_update() {
    AlarmBlockDTO item = new AlarmBlockDTO();
    item.setId(id);
    item.setTenant(tenant);
    item.setTags("{}");

    given() //
        .body(new JSONObject(J.toMap(J.toJson(item)))) //
        .when() //
        .post("/webapi/alarmBlock/update") //
        .prettyPeek() //
        .then() //
        .body("success", IS_TRUE) //
        .body("data", NOT_NULL); //
    Response response = queryById.get();
    System.out.println(response.body().print());
    response //
        .then() //
        .body("success", IS_TRUE) //
        .root("data").body("uniqueId", eq(uniqueId));
  }

  @Order(4)
  @Test
  public void test_alarm_block_delete() {
    given() //
        .pathParam("id", id) //
        .when() //
        .delete("/webapi/alarmBlock/delete/{id}") //
        .prettyPeek() //
        .then() //
        .body("success", IS_TRUE) //
        .body("data", IS_TRUE); //
    Response response = queryById.get();
    System.out.println(response.body().print());
    response //
        .then() //
        .body("success", IS_FALSE); //
  }
}
