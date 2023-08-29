/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import java.time.Duration;

import io.restassured.http.ContentType;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * <p>
 * This IT tests the features of 'support remote pql'.
 * <p>
 * created at 2023/4/1
 *
 * @author jiwliu
 */
public class CeresdbPqlMonitoringIT extends BaseIT {

  /**
   * Test creating log monitoring item
   */
  @Order(1)
  @Test
  public void test_create_pql_monitoring_item() {
    // Create a pql dashboard item with random name
    String name = RandomStringUtils.randomAlphabetic(6) + "test_remote_pql";

    JSONObject body = getJsonFromClasspath("requests/CeresdbPqlMonitoringIT.json");
    body.put("name", name);
    given() //
        .body(body) //
        .when() //
        .post("/webapi/v1/dashboard/create") //
        .then() //
        .log().body().body("success", IS_TRUE); //
  }

  /**
   * Test querying log monitoring metrics
   */
  @Order(2)
  @Test
  public void test_wait_pql_monitoring_metric1() {
    await("Test querying pql monitoring metrics") //
        .atMost(Duration.ofMinutes(4)) //
        .untilNoException(() -> {
          long end = System.currentTimeMillis() / 60000 * 60000;
          long start = end - 60000;
          JSONObject params = json() //
              .put("query", "jvm_eden_capacity").put("start", start).put("end", end)
              .put("delta", "5m").put("fillZero", false).put("step", 1000).put("timeout", "30s");
          given() //
              .contentType(ContentType.JSON).body(params.toString()).when() //
              .post("/webapi/v1/query/pql/range") //
              .then() //
              .log().all().body("success", IS_TRUE) //
              .body("data.size()", gt(0));
          // .log()
          // .body();
        });
  }

}
