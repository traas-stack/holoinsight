/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

public class InitFacadeIT extends BaseIT {
  String tenant;

  @Order(1)
  @Test
  public void test_init_tenantCheck() {
    given() //
        .when() //
        .get("/webapi/init/tenantCheck").then() //
        .body("success", IS_TRUE).body("data", IS_FALSE);
  }

  @Order(2)
  @Test
  public void test_init_tenantSwitch() {
    tenant = "default";
    given() //
        .pathParam("tenant", tenant) //
        .when() //
        .get("/webapi/init/tenantSwitch/{tenant}").then() //
        .body("success", IS_TRUE).body("data", IS_TRUE);
  }

  @Order(3)
  @Test
  public void test_init_sys() {
    given() //
        .when() //
        .get("/webapi/init/tenant").then() //
        .body("success", IS_TRUE).body("data", IS_TRUE);
  }
}
