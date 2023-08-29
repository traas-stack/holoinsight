/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import java.time.Duration;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import io.holoinsight.server.test.it.utils.WebapiUtils;
import io.restassured.response.ValidatableResponse;

/**
 * This IT tests the features of 'Log Monitoring'.
 * <p>
 * created at 2023/3/17
 *
 * @author xzchaoo
 */
public class LogMonitoringIT extends BaseIT {
  Long id;

  /**
   * Test creating log monitoring item
   */
  @Order(1)
  @Test
  public void test_create_log_monitoring_item() {
    // Create a log monitoring item with random name
    String name = RandomStringUtils.randomAlphabetic(6);
    JSONObject body = getJsonFromClasspath("requests/LogMonitoringIT.json");
    body.put("name", name);

    id = ((Number) given() //
        .body(body) //
        .when() //
        .post("/webapi/customPlugin/create") //
        .then() //
        .body("success", IS_TRUE) //
        .extract() //
        .path("data.id")) //
            .longValue(); //
  }

  /**
   * Test querying log monitoring metrics
   */
  @Order(2)
  @Test
  public void test_wait_log_monitoring_metrics() {
    await("Test querying log monitoring metrics") //
        .atMost(Duration.ofMinutes(6)) //
        .untilNoException(() -> {
          long end = System.currentTimeMillis() / 60000 * 60000;
          long start = end - 60000 * 10;

          String metricName = "linecount_" + id;

          ValidatableResponse resp = WebapiUtils.queryMetricsRaw("a", metricName, start, end);
          int size = resp.body("data.results[0].values.size()", gt(2)).extract()
              .path("data.results[0].values.size()");

          for (int i = size - 2; i < size; i++) {
            // the first point is incomplete so we ignore it.
            // the value of second and third points must be 180
            resp.body("data.results[0].values[%d][1]", withArgs(i), eq("180.0"));
          }

          given() //
              .queryParam("name", metricName) //
              .when() //
              .get("/webapi/v1/query/schema") //
              .then() //
              .isSuccess() //
              .body("data.tags", hasItems("app", "hostname", "ip")); //

        });
  }

  /**
   * Test deleting log monitoring item
   */
  @Order(3)
  @Test
  public void test_delete_log_monitoring_item() {
    given() //
        .pathParam("id", id) //
        .when() //
        .delete("/webapi/customPlugin/delete/{id}") //
        .then() //
        .body("success", IS_TRUE); //

    given() //
        .pathParam("id", id) //
        .when() //
        .delete("/webapi/customPlugin/delete/{id}") //
        .then() //
        .body("success", IS_FALSE); //
  }

}
