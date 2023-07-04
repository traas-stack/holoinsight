/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.dal.model.dto.AlarmBlockDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.restassured.response.Response;
import org.hamcrest.CustomMatcher;
import org.hamcrest.core.Every;
import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Stack;
import java.util.function.Supplier;

public class AlarmBlockFacadeIT extends BaseIT {
  Long id;
  String tenant;
  String uniqueId;



  Supplier<Response> queryById = () -> given() //
      .pathParam("id", id) //
      .when() //
      .get("/webapi/alarmBlock/query/{id}"); //

  @Order(1)
  @Test
  public void test_alarm_block_create() {
    uniqueId = "rule_-1";
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

  @Order(2)
  @Test
  public void test_alarm_block_update() {
    AlarmBlockDTO item = new AlarmBlockDTO();
    uniqueId = uniqueId + "0";
    item.setId(id);
    item.setTenant(tenant);
    item.setUniqueId(uniqueId);

    given() //
        .body(new JSONObject(J.toMap(J.toJson(item)))) //
        .when() //
        .post("/webapi/alarmBlock/update") //
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

  @Order(3)
  @Test
  public void test_custom_plugin_delete() {
    given() //
        .pathParam("id", id) //
        .when() //
        .delete("/webapi/alarmBlock/delete/{id}").then() //
        .body("success", IS_TRUE) //
        .body("data", IS_TRUE); //
    Response response = queryById.get();
    System.out.println(response.body().print());
    response //
        .then() //
        .body("success", IS_TRUE) //
        .body("data", IS_NULL); //
  }

  @Order(4)
  @Test
  public void test_custom_plugin_pageQuery() {
    Stack<Long> ids = new Stack<>();
    AlarmBlockDTO item = new AlarmBlockDTO();
    item.setUniqueId(uniqueId);
    for (int i = 0; i < 10; i++) {
      Long id = ((Number) given() //
          .body(new JSONObject(J.toMap(J.toJson(item)))) //
          .when() //
          .post("/webapi/alarmBlock/create") //
          .then() //
          .body("success", IS_TRUE) //
          .extract() //
          .path("data")).longValue(); //
      ids.push(id);
    }

    AlarmBlockDTO condition = new AlarmBlockDTO();
    condition.setUniqueId(uniqueId);
    MonitorPageRequest<AlarmBlockDTO> pageRequest = new MonitorPageRequest<>();
    pageRequest.setTarget(condition);
    pageRequest.setPageNum(0);
    pageRequest.setPageSize(3);
    given() //
        .body(new JSONObject(J.toMap(J.toJson(pageRequest)))) //
        .when() //
        .post("/webapi/alarmBlock/pageQuery") //
        .then() //
        .body("success", IS_TRUE) //
        .root("data")
        .body("items", new Every<>(new CustomMatcher<AlarmBlockDTO>("page query id equal") {
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
