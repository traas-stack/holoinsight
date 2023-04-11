/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import java.time.Duration;

import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

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
          body.put("metric", "system_traffic_bytin");
          body.put("key", "app");
          ValidatableResponse then = given() //
              .body(body) //
              .when() //
              .post("/webapi/v1/query/tagValues") //
              .then();
          then //
              .isSuccess().body("data.tag", eq("app"))
              .body("data.values[0]", eq("holoinsight-server-example"));
        });
  }
}
