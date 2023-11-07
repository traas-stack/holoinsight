/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

public class IntegrationPluginFacadeIT extends BaseIT {
  Long id;
  String product;
  String tenant;
  String type;


  Supplier<Response> queryById = () -> given() //
      .pathParam("id", id) //
      .when() //
      .get("/webapi/integration/plugin/queryById/{id}"); //

  Supplier<Response> queryByName = () -> given() //
      .pathParam("name", product) //
      .when() //
      .get("/webapi/integration/plugin/queryByName/{name}"); //
  Supplier<Response> list = () -> given() //
      .when() //
      .get("/webapi/integration/plugin/list"); //

  @Order(1)
  @Test
  public void test_integration_plugin_create() {
    product = RandomStringUtils.randomAlphabetic(10) + "_integrationPlugin测试";
    String name = RandomStringUtils.randomAlphabetic(10) + "_test";
    type = "io.holoinsight.plugin.JvmPlugin";
    IntegrationPluginDTO item = new IntegrationPluginDTO();
    item.setJson("{\"confs\":{},\"name\":\"JVM\",\"type\":\"io.holoinsight.plugin.JvmPlugin\"}");
    item.setProduct(product);
    item.setType(type);
    item.setName(name);

    // Create custom_plugin
    id = ((Number) given() //
        .body(new JSONObject(J.toMap(J.toJson(item)))) //
        .when() //
        .post("/webapi/integration/plugin/create") //
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
        .root("data").body("product", eq(product)) //
        .extract() //
        .path("data.tenant");
    System.out.println(tenant);

    Response response1 = queryByName.get();
    response1 //
        .then() //
        .body("success", IS_TRUE) //
        .rootPath("data.find { it.id == %d }", withArgs(id.intValue())) //
        .body("type", eq(type));

    Response response2 = list.get();
    response2 //
        .then() //
        .body("success", IS_TRUE) //
        .rootPath("data.find { it.id == %d }", withArgs(id.intValue())) //
        .body("product", eq(product));

  }

  @Order(2)
  @Test
  public void test_integration_plugin_update() {
    product = product + "_v02";
    IntegrationPluginDTO item = new IntegrationPluginDTO();
    item.setId(id);
    item.setTenant(tenant);
    item.setProduct(product);
    // update
    given() //
        .body(new JSONObject(J.toMap(J.toJson(item)))) //
        .when() //
        .post("/webapi/integration/plugin/update") //
        .then() //
        .body("success", IS_TRUE) //
        .body("data", NOT_NULL); //
    Response response = queryById.get();
    System.out.println(response.body().print());
    response //
        .then() //
        .body("success", IS_TRUE) //
        .root("data").body("product", eq(product));
  }

  @Order(3)
  @Test
  public void test_integration_plugin_delete() {
    given() //
        .pathParam("id", id) //
        .when() //
        .delete("/webapi/integration/plugin/delete/{id}").then() //
        .body("success", IS_TRUE).body("data", IS_NULL); //
    Response response = queryById.get();
    System.out.println(response.body().print());
    response //
        .then() //
        .body("success", IS_FALSE) //
        .body("data", IS_NULL); //
  }

}
