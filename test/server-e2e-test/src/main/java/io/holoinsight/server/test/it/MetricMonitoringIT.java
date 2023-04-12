/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import io.restassured.response.ValidatableResponse;

/**
 * @author ljw
 * @date 2023/4/11
 */
public class MetricMonitoringIT extends BaseIT {

  /**
   * Test query tagValues
   */
  @Order(1)
  @Test
  public void test_query_tag_values_monitoring_item() {
    await("test query tag values") //
        .untilAsserted(() -> {
          JSONObject body = new JSONObject();
          body.put("metric", "system_mem_util");
          body.put("key", "app");
          ValidatableResponse then = given() //
              .body(body) //
              .when() //
              .post("/webapi/v1/query/tagValues") //
              .then() //
              .isSuccess() //
              .body("data.tag", eq("app")) //
              .body("data.values", hasItems("holoinsight-server-example")); //
        });
  }
}
