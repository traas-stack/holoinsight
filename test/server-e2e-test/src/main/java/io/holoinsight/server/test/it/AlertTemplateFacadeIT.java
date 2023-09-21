/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import io.restassured.response.Response;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

public class AlertTemplateFacadeIT extends BaseIT {

  Supplier<Response> listAvailableFields = () -> given() //
      .when() //
      .get("/webapi/alertTemplate/listAvailableFields");

  @Order(1)
  @Test
  public void test_available_fields_list() {
    Response response = listAvailableFields.get();
    response //
        .then() //
        .body("success", IS_TRUE) //
        .body("data", NOT_NULL); //
  }
}
