/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

public class DisplayTemplateFacadeIT extends BaseIT {
  Long id;
  Long refId;
  String type;

  @Order(1)
  @Test
  public void test_display_template_query() {
    id = Long.valueOf(1);
    given() //
        .pathParam("id", id) //
        .when() //
        .get("/webapi/displaytemplate/query/{id}").then() //
        .body("success", IS_TRUE).body("data", NOT_NULL).body("data.id", eq(id.intValue()));
  }

  @Order(2)
  @Test
  public void test_display_template_query_type_refId() {
    type = "pod_app_metric";
    refId = Long.valueOf(-1);
    given() //
        .pathParam("type", type) //
        .pathParam("refId", refId) //
        .when() //
        .get("/webapi/displaytemplate/query/{type}/{refId}").then() //
        .body("success", IS_TRUE).body("data", NOT_NULL);
  }

}
