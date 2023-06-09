/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.test.it;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.dal.model.Dashboard;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.CustomMatcher;
import org.hamcrest.core.Every;
import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Supplier;

/**
 * <p>
 * </p>
 * <p>
 * Date: 2023-03-27 Time: 11:39
 * </p>
 *
 * @author jsy1001de
 */
public class DashboardIT extends BaseIT {
  String name;
  Long id;
  String tenant;

  Supplier<Response> queryDashBoard = () -> given() //
      .pathParam("id", id) //
      .when() //
      .get("/webapi/v1/dashboard/query/{id}"); //

  @Order(1)
  @Test
  public void test_rule_create() {
    name = RandomStringUtils.randomAlphabetic(10) + "dashboard测试";
    Dashboard dashboard = new Dashboard();
    dashboard.setTitle(name);
    dashboard.setConf(new HashMap<>());
    dashboard.setType("magi");

    // Create folder
    id = ((Number) given() //
        .body(new JSONObject(J.toMap(J.toJson(dashboard)))) //
        .when() //
        .post("/webapi/v1/dashboard/create") //
        .then() //
        .body("success", IS_TRUE) //
        .extract() //
        .path("data.id")).longValue();
    System.out.println(id);
    Response response = queryDashBoard.get();
    System.out.println(response.body().print());
    tenant = response //
        .then() //
        .body("success", IS_TRUE) //
        .root("data").body("title", eq(name)) //
        .extract() //
        .path("data.tenant");
    System.out.println(tenant);
  }

  @Order(2)
  @Test
  public void test_rule_update() {
    name = name + "_v2";
    Dashboard dashboard = new Dashboard();
    dashboard.setTitle(name);
    dashboard.setId(id);
    dashboard.setTenant(tenant);
    given() //
        .body(new JSONObject(J.toMap(J.toJson(dashboard)))) //
        .when() //
        .post("/webapi/v1/dashboard/update") //
        .then() //
        .body("success", IS_TRUE).rootPath("data") //
        .body("title", eq(name)); //
    Response response = queryDashBoard.get();
    System.out.println(response.body().print());
    response //
        .then() //
        .body("success", IS_TRUE) //
        .root("data").body("title", eq(name));
  }

  @Order(3)
  @Test
  public void test_rule_delete() {
    given() //
        .pathParam("id", id) //
        .when() //
        .delete("/webapi/v1/dashboard/{id}").then() //
        .body("success", IS_TRUE); //
    Response response = queryDashBoard.get();
    System.out.println(response.body().print());
    response //
        .then() //
        .body("success", IS_FALSE) //
        .body("data", IS_NULL); //
  }

  @Order(4)
  @Test
  public void test_rule_pageQuery() {
    Stack<Long> ids = new Stack<>();
    for (int i = 0; i < 10; i++) {
      Long id = ((Number) given() //
          .body(new JSONObject(J.toMap(J.toJson(buildDashboard("hit_title_" + i))))) //
          .when() //
          .post("/webapi/v1/dashboard/create") //
          .then() //
          .body("success", IS_TRUE) //
          .extract() //
          .path("data.id")).longValue(); //
      ids.push(id);
    }
    for (int i = 0; i < 10; i++) {
      given() //
          .body(new JSONObject(J.toMap(J.toJson(buildDashboard("miss_title_" + i))))) //
          .when() //
          .post("/webapi/v1/dashboard/create") //
          .then() //
          .body("success", IS_TRUE);
    }
    Dashboard condition = new Dashboard();
    condition.setTitle("hit_title");
    MonitorPageRequest<Dashboard> pageRequest = new MonitorPageRequest<>();
    pageRequest.setTarget(condition);
    pageRequest.setPageNum(0);
    pageRequest.setPageSize(3);
    given() //
        .body(new JSONObject(J.toMap(J.toJson(pageRequest)))) //
        .when() //
        .post("/webapi/v1/dashboard/pageQuery") //
        .then() //
        .body("success", IS_TRUE) //
        .root("data")
        .body("items", new Every<>(new CustomMatcher<Dashboard>("page query id equal") {
          @Override
          public boolean matches(Object o) {
            Map<String, Object> item = (Map<String, Object>) o;
            Long queryId = ((Number) item.get("id")).longValue();
            Long id = ids.pop().longValue();
            return queryId.equals(id);
          }
        }));
  }

  private Dashboard buildDashboard(String title) {
    Dashboard dashboard = new Dashboard();
    dashboard.setTitle(title);
    dashboard.setConf(new HashMap<>());
    dashboard.setType("magi");
    return dashboard;
  }
}
