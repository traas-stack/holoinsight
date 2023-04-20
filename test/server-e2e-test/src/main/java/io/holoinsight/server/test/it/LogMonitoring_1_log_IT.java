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

/**
 * <p>
 * This IT tests the features of 'Log Monitoring'.
 * <p>
 * We use `log-generator.py` to generate logs with expected contents at a fixed frequency.
 * <p>
 * created at 2023/3/17
 *
 * @author xzchaoo
 */
public class LogMonitoring_1_log_IT extends BaseIT {
  Long id;

  /**
   * Test creating log monitoring item
   */
  @Order(1)
  @Test
  public void test_create_log_monitoring_item() {
    // Create a log monitoring item with random name
    String name = RandomStringUtils.randomAlphabetic(6);

    JSONObject body = getJsonFromClasspath("requests/LogMonitoring_1_log_IT.json");
    body.put("name", name);
    body.put("periodType", "SECOND");
    // body.put("periodType", "MINUTE");
    // body.getJSONObject("conf") //
    // .getJSONArray("collectMetrics") //
    // .getJSONObject(0) //
    // .put("tableName", metricPrefix); //

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
  public void test_wait_log_monitoring_metric1() {
    await("Test querying log monitoring metrics") //
        .untilNoException(() -> {
          long end = System.currentTimeMillis() / 60000 * 60000;
          long start = end - 60000;

          String metricName = "metric1_" + id;

          WebapiUtils.queryMetricsRaw("a", metricName, start, end) //
              .rootPath("data.results.find { it.tags['level'] == 'WARN' }") //
              .body("values.size()", gt(2)) //
              .body("values[1][1]", eq("4.0")) //
              .body("values[2][1]", eq("4.0")) //
              .rootPath("data.results.find { it.tags['level'] == 'ERROR' }") //
              .body("values.size()", gt(2)) //
              .body("values[1][1]", eq("5.0")) //
              .body("values[2][1]", eq("5.0")) //
              .rootPath("data.results.find { it.tags['level'] == 'INFO' }") //
              .body("values.size()", gt(2)) //
              .body("values[1][1]", eq("6.0")) //
              .body("values[2][1]", eq("6.0")) //
              .rootPath("data.results.find { it.tags['level'] == '错误' }") //
              .body("values.size()", gt(2)) //
              .body("values[1][1]", eq("5.0")) //
              .body("values[2][1]", eq("5.0")) //
          ;

          given() //
              .queryParam("name", metricName) //
              .when() //
              .get("/webapi/v1/query/schema") //
              .then() //
              .isSuccess() //
              .body("data.tags", hasItems("app", "hostname", "ip", "level")); //

        });
  }


  /**
   * Test querying log monitoring metrics
   */
  @Order(3)
  @Test
  public void test_wait_log_monitoring_metric2() {
    await("Test querying log monitoring metrics") //
        .atMost(Duration.ofMinutes(1)) //
        .untilNoException(() -> {
          long end = System.currentTimeMillis() / 60000 * 60000;
          long start = end - 60000;

          String metricName = "metric2_" + id;

          WebapiUtils.queryMetricsRaw("a", metricName, start, end) //
              .rootPath("data.results.find { it.tags['biz'] == 'biz1' }") //
              .body("values.size()", gt(2)) //
              .body("values[1][1]", eq("1.0")) //
              .body("values[2][1]", eq("1.0")) //
              .rootPath("data.results.find { it.tags['biz'] == 'biz2' }") //
              .body("values.size()", gt(2)) //
              .body("values[1][1]", eq("2.0")) //
              .body("values[2][1]", eq("2.0")) //
              .rootPath("data.results.find { it.tags['biz'] == '-' }") //
              .body("values.size()", gt(2)) //
              .body("values[1][1]", eq("17.0")) //
              .body("values[2][1]", eq("17.0")) //
          ;

          given() //
              .queryParam("name", metricName) //
              .when() //
              .get("/webapi/v1/query/schema") //
              .then() //
              .isSuccess() //
              .body("data.tags", hasItems("app", "hostname", "ip", "biz")); //

        });
  }

  /**
   * Test deleting log monitoring item
   */
  @Order(4)
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
