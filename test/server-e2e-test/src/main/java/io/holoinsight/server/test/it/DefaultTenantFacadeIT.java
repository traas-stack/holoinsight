/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

public class DefaultTenantFacadeIT extends BaseIT {
  final String name = "default";

  @Order(1)
  @Test
  public void test_default_tenant_config() {
    given() //
        .pathParam("name", name) //
        .when() //
        .get("/webapi/tenant/config/{name}").then() //
        .body("success", IS_TRUE);
  }
}
