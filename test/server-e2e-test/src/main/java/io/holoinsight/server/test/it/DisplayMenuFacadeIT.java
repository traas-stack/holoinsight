/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

public class DisplayMenuFacadeIT extends BaseIT {
  Long id;
  Long refId;
  String type;
  String name;

  @Order(1)
  @Test
  public void test_display_menu_query() {
    id = Long.valueOf(1);
    given() //
        .pathParam("id", id) //
        .when() //
        .get("/webapi/displaymenu/query/{id}").then() //
        .body("success", IS_TRUE).body("data", NOT_NULL).body("data.id", eq(id.intValue()));
  }

  @Order(2)
  @Test
  public void test_display_menu_query_type_refId() {
    type = "apm";
    refId = Long.valueOf(-1);
    given() //
        .pathParam("type", type) //
        .pathParam("refId", refId) //
        .when() //
        .get("/webapi/displaymenu/query/{type}/{refId}").then() //
        .body("success", IS_TRUE).body("data", NOT_NULL);
  }

  @Order(3)
  @Test
  public void test_query_apm_name() {
    name = RandomStringUtils.randomAlphabetic(10) + "_create_integrationGenerated测试";
    given() //
        .pathParam("name", name) //
        .when() //
        .get("/webapi/displaymenu/query/apm/{name}").then() //
        .body("success", IS_TRUE).body("data", NOT_NULL);
  }
}
