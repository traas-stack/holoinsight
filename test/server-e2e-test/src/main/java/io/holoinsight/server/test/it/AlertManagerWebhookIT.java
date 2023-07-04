/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.dal.model.AlertmanagerWebhook;
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

public class AlertManagerWebhookIT extends BaseIT {

  String name;
  Long id;
  String tenant;
  Supplier<Response> queryById = () -> given() //
      .pathParam("id", id) //
      .when() //
      .get("/webapi/alertmanager/webhook/{id}"); //

  @Order(1)
  @Test
  public void test_alert_manager_webhook_create() {
    name = RandomStringUtils.randomAlphabetic(10) + "_alertmanager-webhook测试";
    AlertmanagerWebhook item = new AlertmanagerWebhook();
    item.setName(name);
    // Create alert manager webhook
    id = ((Number) given() //
        .body(new JSONObject(J.toMap(J.toJson(item)))) //
        .when() //
        .post("/webapi/alertmanager/webhook") //
        .then() //
        .body("success", IS_TRUE) //
        .extract() //
        .path("data.id")).longValue();
    System.out.println(id);
    Response response = queryById.get();
    System.out.println(response.body().print());
    tenant = response //
        .then() //
        .body("success", IS_TRUE) //
        .root("data").body("name", eq(name)) //
        .extract() //
        .path("data.tenant");
    System.out.println(tenant);

  }

  @Order(2)
  @Test
  public void test_alert_manager_webhookr_update() {
    name = name + "_v02";
    AlertmanagerWebhook item = new AlertmanagerWebhook();
    item.setName(name);
    item.setTenant(tenant);
    // update
    given() //
        .pathParam("id", id) //
        .body(new JSONObject(J.toMap(J.toJson(item)))) //
        .when() //
        .post("/webapi/alertmanager/webhook/update/{id}") //
        .then() //
        .body("success", IS_TRUE) //
        .body("data", NOT_NULL); //
    Response response = queryById.get();
    System.out.println(response.body().print());
    response //
        .then() //
        .body("success", IS_TRUE) //
        .root("data").body("name", eq(name));
  }


  @Order(3)
  @Test
  public void test_alert_manager_webhook_delete() {
    given() //
        .pathParam("id", id) //
        .when() //
        .post("/webapi/alertmanager/webhook/delete/{id}").then() //
        .body("success", IS_TRUE) //
        .body("data", NOT_NULL); //
    Response response = queryById.get();
    System.out.println(response.body().print());
    response //
        .then() //
        .body("success", IS_FALSE) //
        .body("data", IS_NULL); //
  }

  @Order(4)
  @Test
  public void test_alert_manager_webhookn_pageQuery() {
    Stack<Long> ids = new Stack<>();
    name = RandomStringUtils.randomAlphabetic(10) + "_alertmanager-webhook测试";
    AlertmanagerWebhook item = new AlertmanagerWebhook();
    item.setName(name);
    JSONObject jsonObject = new JSONObject(J.toMap(J.toJson(item)));
    for (int i = 0; i < 10; i++) {
      Long id = ((Number) given() //
          .body(new JSONObject(J.toMap(J.toJson(item)))) //
          .when() //
          .post("/webapi/alertmanager/webhook") //
          .then() //
          .body("success", IS_TRUE) //
          .extract() //
          .path("data.id")).longValue(); //
      ids.push(id);
    }

    AlertmanagerWebhook condition = new AlertmanagerWebhook();
    condition.setName(name);
    MonitorPageRequest<AlertmanagerWebhook> pageRequest = new MonitorPageRequest<>();
    pageRequest.setTarget(condition);
    pageRequest.setPageNum(0);
    pageRequest.setPageSize(3);
    given() //
        .body(new JSONObject(J.toMap(J.toJson(pageRequest)))) //
        .when() //
        .post("/webapi/alertmanager/webhook/pageQuery") //
        .then() //
        .body("success", IS_TRUE) //
        .root("data")
        .body("items", new Every<>(new CustomMatcher<AlertmanagerWebhook>("page query id equal") {
          @Override
          public boolean matches(Object o) {
            Map<String, Object> item = (Map<String, Object>) o;
            Long queryId = ((Number) item.get("id")).longValue();
            Long id = ids.pop().longValue();
            return queryId.equals(id);
          }
        }));
  }


}
