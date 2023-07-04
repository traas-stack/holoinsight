/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.dal.model.dto.AlarmSubscribeDTO;
import io.holoinsight.server.home.dal.model.dto.AlarmSubscribeInfo;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


public class AlarmSubscribeFacadeIT extends BaseIT {

  String uniqueId;
  String tenant;
  Supplier<Response> queryByUniqueId = () -> given() //
      .pathParam("uniqueId", uniqueId) //
      .when() //
      .get("/webapi/alarmSubscribe/queryByUniqueId/{uniqueId}"); //

  Supplier<Response> querySubUsers = () -> given() //
      .pathParam("uniqueId", uniqueId) //
      .when() //
      .get("/webapi/alarmSubscribe/querySubUsers/{uniqueId}"); //

  @Order(1)
  @Test
  public void test_alert_manager_webhook_create() {
    AlarmSubscribeDTO item = new AlarmSubscribeDTO();
    uniqueId = "1";
    item.setUniqueId(uniqueId);
    List<AlarmSubscribeInfo> alarmSubscribe = new ArrayList<>();
    AlarmSubscribeInfo alarmSubscribeInfo = new AlarmSubscribeInfo();
    alarmSubscribe.add(alarmSubscribeInfo);
    item.setAlarmSubscribe(alarmSubscribe);
    item.setEnvType("dev");
    // Create alert manager webhook
    given() //
        .body(new JSONObject(J.toMap(J.toJson(item)))) //
        .when() //
        .post("/webapi/alarmSubscribe/submit") //
        .then() //
        .body("success", IS_TRUE) //
        .body("data", IS_TRUE); //
    Response response = queryByUniqueId.get();
    System.out.println(response.body().print());
    String json = response //
        .then() //
        .body("success", IS_TRUE) //
        .rootPath("data.alarmSubscribe.find { it.uniqueId == '%s' }", withArgs(uniqueId)) //
        .body(NOT_NULL).extract() //
        .asString();
    JSONObject jsonObject = new JSONObject(json);
    JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("alarmSubscribe");
    JSONObject tenantJson = (JSONObject) jsonArray.get(0);
    tenant = tenantJson.getString("tenant");
  }

  @Order(2)
  @Test
  public void test_query_sub_users() {
    Response res = querySubUsers.get();
    System.out.println(res.body().print());
    res //
        .then() //
        .body("success", IS_TRUE); //


  }
}
