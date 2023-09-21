/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.test.it;

import com.google.gson.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.home.dal.model.dto.CloudMonitorRange;
import io.holoinsight.server.home.dal.model.dto.OpenmetricsScraperDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.Stack;
import java.util.function.Supplier;

/**
 * <p>
 * </p>
 * <p>
 * Date: 2023-03-27 Time: 15:46
 * </p>
 *
 * @author jsy1001de
 */
public class OpenMetricsScraperIT extends BaseIT {
  String name;
  Long id;
  String tenant;

  Supplier<Response> queryById = () -> given() //
      .pathParam("id", id) //
      .when() //
      .get("/webapi/openmetrics/scraper/{id}"); //

  @Order(1)
  @Test
  public void test_rule_create() {
    name = RandomStringUtils.randomAlphabetic(10) + "openmetric测试";
    OpenmetricsScraperDTO item = new OpenmetricsScraperDTO();
    item.setName(name);
    item.setScrapeTimeout("1");
    item.setScrapeInterval("1m");
    item.setPort("8080");
    item.setMetricsPath("/actuator/prometheus");
    item.setCollectRanges(J.fromJson(
        "{\"table\":\"dev_server\",\"condition\":[{\"app\":[\"cloudmonitor-home\",\"cloudmonitor-meta\",\"cloudmonitor-query\"]}]}",
        new TypeToken<CloudMonitorRange>() {}.getType()));


    // Create folder
    id = ((Number) given() //
        .body(new JSONObject(J.toMap(J.toJson(item)))) //
        .when() //
        .post("/webapi/openmetrics/scraper/create") //
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
  public void test_rule_update() {
    name = name + "_v2";
    OpenmetricsScraperDTO item = new OpenmetricsScraperDTO();
    item.setId(id);
    item.setName(name);
    item.setScrapeTimeout("1");
    item.setScrapeInterval("1m");
    item.setPort("8080");
    item.setMetricsPath("/actuator/prometheus");
    item.setCollectRanges(J.fromJson(
        "{\"table\":\"dev_server\",\"condition\":[{\"app\":[\"cloudmonitor-home\",\"cloudmonitor-meta\",\"cloudmonitor-query\"]}]}",
        new TypeToken<CloudMonitorRange>() {}.getType()));

    item.setTenant(tenant);
    given() //
        .body(new JSONObject(J.toMap(J.toJson(item)))) //
        .when() //
        .post("/webapi/openmetrics/scraper/update/" + id) //
        .then() //
        .body("success", IS_TRUE).rootPath("data") //
        .body("name", eq(name)); //
    Response response = queryById.get();
    System.out.println(response.body().print());
    response //
        .then() //
        .body("success", IS_TRUE) //
        .root("data").body("name", eq(name));
  }

  @Order(3)
  @Test
  public void test_rule_delete() {
    given() //
        .pathParam("id", id) //
        .when() //
        .post("/webapi/openmetrics/scraper/delete/{id}").then() //
        .body("success", IS_TRUE); //
    Response response = queryById.get();
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
          .body(new JSONObject(J.toMap(J.toJson(buildItem("hit_title_" + i))))) //
          .when() //
          .post("/webapi/openmetrics/scraper/create") //
          .then() //
          .body("success", IS_TRUE) //
          .extract() //
          .path("data.id")).longValue(); //
      ids.push(id);
    }
    for (int i = 0; i < 10; i++) {
      given() //
          .body(new JSONObject(J.toMap(J.toJson(buildItem("miss_title_" + i))))) //
          .when() //
          .post("/webapi/openmetrics/scraper/create") //
          .then() //
          .body("success", IS_TRUE);
    }
    OpenmetricsScraperDTO condition = new OpenmetricsScraperDTO();
    condition.setName("hit_title");
    MonitorPageRequest<OpenmetricsScraperDTO> pageRequest = new MonitorPageRequest<>();
    pageRequest.setTarget(condition);
    pageRequest.setPageNum(0);
    pageRequest.setPageSize(3);
  }

  private OpenmetricsScraperDTO buildItem(String title) {
    OpenmetricsScraperDTO item = new OpenmetricsScraperDTO();
    item.setName(title);
    item.setScrapeTimeout("1");
    item.setScrapeInterval("1m");
    item.setPort("8080");
    item.setMetricsPath("/actuator/prometheus");
    item.setCollectRanges(J.fromJson(
        "{\"table\":\"dev_server\",\"condition\":[{\"app\":[\"cloudmonitor-home\",\"cloudmonitor-meta\",\"cloudmonitor-query\"]}]}",
        new TypeToken<CloudMonitorRange>() {}.getType()));
    return item;
  }
}
