/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import io.restassured.response.Response;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

public class IntegrationProductFacadeIT extends BaseIT {
  Long id;
  String name;

  Supplier<Response> queryById = () -> given() //
      .pathParam("id", id) //
      .when() //
      .get("/webapi/integration/product/queryById/{id}"); //

  Supplier<Response> queryByName = () -> given() //
      .pathParam("name", name) //
      .when() //
      .get("/webapi/integration/product/queryByName/{name}"); //

  @Order(1)
  @Test
  public void test_integration_product_query() {
    id = Long.valueOf(1);
    name = "OpenAiPlugin";

    Response response = queryById.get();
    response //
        .then() //
        .body("success", IS_TRUE) //
        .root("data").body("name", eq(name));

    Response response1 = queryByName.get();
    response1 //
        .then() //
        .body("success", IS_TRUE) //
        .rootPath("data.find { it.name == '%s' }", withArgs(name)) //
        .body(NOT_NULL);
  }

  @Order(2)
  @Test
  public void test_integration_product_dataReceived() {
    given() //
        .when() //
        .get("/webapi/integration/product/dataReceived").then() //
        .body("success", IS_TRUE); //
  }

}
