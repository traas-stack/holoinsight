/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.test.it;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.dal.model.dto.AlarmWebhookDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.CustomMatcher;
import org.hamcrest.core.Every;
import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Stack;
import java.util.function.Supplier;

/**
 * <p>
 * </p>
 * <p>
 * Date: 2023-03-27 Time: 19:39
 * </p>
 *
 * @author jsy1001de
 */
public class AlertWebhookIT extends BaseIT {
  String name;
  Long id;
  String tenant;

  Supplier<Response> queryById = () -> given() //
      .pathParam("id", id) //
      .when() //
      .get("/webapi/alarmWebhook/query/{id}"); //

  @Order(1)
  @Test
  public void test_rule_create() {
    name = RandomStringUtils.randomAlphabetic(10) + "dingding robot测试";
    AlarmWebhookDTO item = buildItem(name);

    // Create folder
    id = ((Number) given() //
        .body(new JSONObject(J.toMap(J.toJson(item)))) //
        .when() //
        .post("/webapi/alarmWebhook/create") //
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
        .root("data").body("webhookName", eq(name)) //
        .extract() //
        .path("data.tenant");
    System.out.println(tenant);
  }

  @Order(1)
  @Test
  public void test_rule_update() {
    name = name + "_v2";
    AlarmWebhookDTO item = buildItem(name);
    item.setId(id);
    item.setTenant(tenant);
    given() //
        .body(new JSONObject(J.toMap(J.toJson(item)))) //
        .when() //
        .post("/webapi/alarmWebhook/update") //
        .then() //
        .body("success", IS_TRUE) //
        .body("data", IS_TRUE); //
    Response response = queryById.get();
    System.out.println(response.body().print());
    response //
        .then() //
        .body("success", IS_TRUE) //
        .root("data").body("webhookName", eq(name));
  }

  @Order(2)
  @Test
  public void test_rule_delete() {
    given() //
        .pathParam("id", id) //
        .when() //
        .delete("/webapi/alarmWebhook/delete/{id}").then() //
        .body("success", IS_TRUE) //
        .body("data", IS_TRUE); //
    Response response = queryById.get();
    System.out.println(response.body().print());
    response //
        .then() //
        .body("success", IS_TRUE) //
        .body("data", IS_NULL); //
  }

  @Order(3)
  @Test
  public void test_rule_pageQuery() {
    Stack<Long> ids = new Stack<>();
    for (int i = 0; i < 10; i++) {
      Long id = ((Number) given() //
          .body(new JSONObject(J.toMap(J.toJson(buildItem("hit_title_" + i))))) //
          .when() //
          .post("/webapi/alarmWebhook/create") //
          .then() //
          .body("success", IS_TRUE) //
          .extract() //
          .path("data")).longValue(); //
      ids.push(id);
    }
    for (int i = 0; i < 10; i++) {
      given() //
          .body(new JSONObject(J.toMap(J.toJson(buildItem("miss_title_" + i))))) //
          .when() //
          .post("/webapi/alarmWebhook/create") //
          .then() //
          .body("success", IS_TRUE);
    }
    AlarmWebhookDTO condition = new AlarmWebhookDTO();
    condition.setWebhookName("hit_title");
    MonitorPageRequest<AlarmWebhookDTO> pageRequest = new MonitorPageRequest<>();
    pageRequest.setTarget(condition);
    pageRequest.setPageNum(0);
    pageRequest.setPageSize(3);
    pageRequest.setSortRule("desc");
    pageRequest.setSortBy("id");
    given() //
        .body(new JSONObject(J.toMap(J.toJson(pageRequest)))) //
        .when() //
        .post("/webapi/alarmDingDingRobot/pageQuery") //
        .then() //
        .body("success", IS_TRUE) //
        .root("data")
        .body("items", new Every<>(new CustomMatcher<AlarmWebhookDTO>("page query id equal") {
          @Override
          public boolean matches(Object o) {
            Map<String, Object> item = (Map<String, Object>) o;
            Long queryId = ((Number) item.get("id")).longValue();
            Long id = ids.pop().longValue();
            return queryId.equals(id);
          }
        }));
  }

  private AlarmWebhookDTO buildItem(String title) {
    AlarmWebhookDTO item = new AlarmWebhookDTO();
    item.setWebhookName(title);
    item.setStatus((byte) 1);
    item.setRequestType("POST");
    item.setRequestUrl("http://localhost:8080/internal/api/home/alarmWebhook/testCase");
    item.setRequestHeaders("{\"Content-Type\":\"application/json\",\"charset\":\"utf-8\"}");
    item.setRequestBody(
        "{ \t\"ruleName\": \"${ruleName}\", \t\"alarmTimestamp\": \"${alarmTimestamp}\", \t\"alarmLevel\": "
            + "\"${alarmLevel}\",         \"alarmTags\":\"${alarmTags}\", \t\"alarmContent\": \"${alarmContent}\",          "
            + "\"alarmTenant\":\"${tenant}\",           \"alarmUrl\":\"${ruleUrl}\",           \"ruleId\": \"${ruleId}\" }");
    item.setWebhookTest(
        "{     \"ruleName\":\"业务监控告警\",     \"alarmTimestamp\": \"1659497652000\",     \"alarmLevel\":\"紧急\",  "
            + "    \"ruleUrl\":\"www.test.com\",     \"tenant\":\"dev\", "
            + "\"alarmTags\":\"{\\\"traceId\\\":\\\"04eafc111626549503754256697t5\\\"}\",     "
            + "\"alarmContent\":\"业务失败最近2个周期平均值大于1周期为一分钟,当前值:2\" }");
    item.setType((byte) 1);
    return item;
  }
}
