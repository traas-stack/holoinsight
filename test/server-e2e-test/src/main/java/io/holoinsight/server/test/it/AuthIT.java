/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import org.junit.jupiter.api.Test;

/**
 * <p>
 * Authentication-related integration test.
 * <p>
 * created at 2023/3/10
 *
 * @author xzchaoo
 */
public class AuthIT extends BaseIT {

  @Test
  public void test_getCurrentUser() {
    // In opensource mode, the server always returns a default admin user.
    given() //
        .get("webapi/user/getCurrentUser") //
        .then() //
        .body("success", IS_TRUE)//
        .body("data.tenants", hasSize(1)) //
        .body("data.tenants[0].code", eq("default")) //
        .body("data.tenants[0].name", eq("default")) //
        .body("data.tPowers", hasKey("default")) //
        .body("data.tPowers.default", hasItems("VIEW", "EDIT")) //
        .body("data.user.identityType", eq("INNER")) //
        .body("data.user.superAdmin", IS_TRUE) //
        .body("data.user.superAdmin", IS_TRUE) //
    ;
  }
}
