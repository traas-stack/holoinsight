/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.dal.model.UserinfoVerification;
import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

public class UserinfoVerificationFacadeIT extends BaseIT {
  Long id;
  String verificationContent;
  String contentType;

  @Order(1)
  @Test
  public void test_custom_plugin_create() {
    verificationContent = "hello";
    contentType = "email";
    UserinfoVerification item = new UserinfoVerification();
    item.setVerificationContent(verificationContent);
    item.setContentType(contentType);
    // Create custom_plugin
    id = ((Number) given() //
        .body(new JSONObject(J.toMap(J.toJson(item)))) //
        .when() //
        .post("/webapi/userinfoVerification/create") //
        .then() //
        .body("success", IS_TRUE) //
        .extract() //
        .path("data")).longValue();
  }

}
