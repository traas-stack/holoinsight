/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.dal.model.ApiKey;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

public class ApiKeyFacadeIT extends BaseIT {
  String name;
  Long id;
  String tenant;
  Boolean status;
  String apikey;

  Supplier<Response> queryById = () -> given() //
      .pathParam("id", id) //
      .when() //
      .get("/webapi/apikey/query/{id}"); //

  Supplier<Response> queryAll = () -> given() //
      .when() //
      .get("/webapi/apikey/queryAll"); //

  @Order(1)
  @Test
  public void test_apikey_create() {
    name = RandomStringUtils.randomAlphabetic(10) + "_Apikey测试";
    status = true;
    ApiKey item = new ApiKey();
    item.setName(name);
    item.setStatus(true);
    // Create custom_plugin
    id = ((Number) given() //
        .body(new JSONObject(J.toMap(J.toJson(item)))) //
        .when() //
        .post("/webapi/apikey/create") //
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

    apikey = response //
        .then() //
        .body("success", IS_TRUE) //
        .root("data").body("name", eq(name)) //
        .extract() //
        .path("data.apiKey");
    System.out.println(tenant);
    System.out.println(apikey);

    Response responseAll = queryAll.get();
    responseAll //
        .then() //
        .body("success", IS_TRUE) //
        .rootPath("data.find { it.name == '%s' }", withArgs(name)) //
        .body(NOT_NULL);
  }

  @Order(2)
  @Test
  public void test_apikey_update() {
    name = name + "_v02";
    ApiKey item = new ApiKey();
    item.setId(id);
    item.setName(name);
    item.setApiKey(apikey);
    item.setTenant(tenant);
    item.setStatus(status);
    // update
    given() //
        .body(new JSONObject(J.toMap(J.toJson(item)))) //
        .when() //
        .post("/webapi/apikey/update") //
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
  public void test_apikey_delete() {
    given() //
        .pathParam("id", id) //
        .when() //
        .delete("/webapi/apikey/delete/{id}").then() //
        .body("success", IS_TRUE) //
        .body("data", IS_NULL); //
    Response response = queryById.get();
    System.out.println(response.body().print());
    response //
        .then() //
        .body("success", IS_FALSE) //
        .body("data", IS_NULL); //
  }

  @Order(4)
  @Test
  public void test_apikey_deleteByName() {
    test_apikey_create();
    given() //
        .pathParam("name", name) //
        .when() //
        .delete("/webapi/apikey/deleteByName/{name}").then() //
        .body("success", IS_TRUE) //
        .body("data", IS_NULL); //
    Response response = queryById.get();
    System.out.println(response.body().print());
    response //
        .then() //
        .body("success", IS_FALSE) //
        .body("data", IS_NULL); //
  }
}
