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
   * Test query not exist metric
   */
  @Order(1)
  @Test
  public void test_query_not_exists_metrics_monitoring() {
    await("test query not exist metric") //
        .untilNoException(() -> {
          long end = System.currentTimeMillis() / 60000 * 60000;
          long start = end - 60000;
          String name = "test_query_not_exists_metrics_monitoring";
          String metric = "not_exist_metric_xxx";
          JSONObject body = json() //
              .put("query", name) //
              .put("datasources", jarray() //
                  .put(json() //
                      .put("name", name) //
                      .put("metric", metric) //
                      .put("aggregator", "none") //
                      .put("start", start) //
                      .put("end", end + 60_000) //
                      .put("downsample", "5s").put("aggregator", "avg").put("filters", jarray()) //
                      .put("groupBy", jarray().put("app").put("ip")) //
          ));
          given() //
              .body(body) //
              .when() //
              .post("/webapi/v1/query") //
              .then() //
              .body("success", IS_TRUE);
        });
  }

  /**
   * Test query tagValues
   */
  @Order(2)
  @Test
  public void test_query_tag_values_monitoring_item() {
    await("test query tag values") //
        .untilNoException(() -> {
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
