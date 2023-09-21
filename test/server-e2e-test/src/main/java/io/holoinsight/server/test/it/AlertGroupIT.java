/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.dal.model.dto.AlarmGroupDTO;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

/**
 * @author masaimu
 * @version 2023-04-03 17:32:00
 */
public class AlertGroupIT extends BaseIT {

  String name;
  Integer id;
  String tenant;

  Supplier<Response> queryAlertGroup = () -> given() //
      .pathParam("id", id) //
      .when() //
      .get("/webapi/alarmGroup/query/{id}"); //

  @Order(1)
  @Test
  public void test_group_create() {
    name = RandomStringUtils.randomAlphabetic(10) + "AlertGroupTest";
    AlarmGroupDTO alarmGroupDTO = new AlarmGroupDTO();
    alarmGroupDTO.setGroupName(name);

    // Create folder
    id = given() //
        .body(new JSONObject(J.toMap(J.toJson(alarmGroupDTO)))) //
        .when() //
        .post("/webapi/alarmGroup/create") //
        .then() //
        .body("success", IS_TRUE) //
        .body("data", Matchers.any(Number.class)) //
        .extract() //
        .path("data"); //
    System.out.println(id);
    Response response = queryAlertGroup.get();
    System.out.println(response.body().print());
    tenant = response //
        .then().log().all() //
        .body("success", IS_TRUE) //
        .root("data") //
        .body("groupName", eq(name)) //
        .extract() //
        .path("data.tenant");
    System.out.println(tenant);
  }

  @Order(2)
  @Test
  public void test_check_group_name() {
    String invalidRuleName =
        RandomStringUtils.randomAlphabetic(10) + "<a href=http://www.baidu.com>点击查看详情</a>";
    AlarmGroupDTO alarmGroup = new AlarmGroupDTO();
    alarmGroup.setGroupName(invalidRuleName);
    alarmGroup.setId(id.longValue());
    alarmGroup.setTenant(tenant);

    // Create folder
    Response response = given() //
        .body(new JSONObject(J.toMap(J.toJson(alarmGroup)))) //
        .when() //
        .post("/webapi/alarmGroup/update");
    System.out.println(response.print());
    response.then() //
        .body("success", IS_FALSE) //
        .body("message", startsWith("invalid groupName"));

    response = queryAlertGroup.get();
    System.out.println(response.body().print());
    response //
        .then() //
        .body("success", IS_TRUE) //
        .root("data") //
        .body("groupName", eq(name));
  }
}
