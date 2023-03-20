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

    JSONObject body = json();
    JSONObject conf = json();
    body.put("conf", conf);
    body.put("name", name);
    body.put("parentFolderId", "-1");
    body.put("periodType", "MINUTE");
    body.put("pluginType", "custom");
    body.put("status", "ONLINE");

    conf.put("whiteFilters", jarray() //
        .put( //
            json() //
                .put("type", "CONTAINS") //
                .put("rule", json()) //
                .put("values", jarray().put("delta sync success"))));
    conf.put("blackFilters", jarray());

    conf.put("logPaths", jarray() //
        .put( //
            json() //
                .put("path", "/home/admin/logs/holoinsight-server/agent.log") //
                .put("charset", "utf-8") //
                .put("type", "path")));

    conf.put("logParse", json().put("multiLine", json("multi", false)));

    conf.put("collectMetrics", jarray() //
        .put( //
            json() //
                .put("metricType", "count") //
                .put("tableName", "linecount") //
                .put("metrics", jarray() //
                    .put( //
                        json() //
                            .put("name", "vale") //
                            .put("func", "count")))));

    conf.put("collectRanges", json() //
        .put("table", "default_server") //
        .put("condition", jarray() //
            .put( //
                json("app", jarray() //
                    .put("holoinsight-server-example")))));

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
        .atMost(Duration.ofMinutes(10)) //
        .untilAsserted(() -> {
          long end = System.currentTimeMillis() / 60000 * 60000;
          long start = end - 60000 * 10;

          String metricName = "linecount_" + id;

          WebapiUtils.queryMetricsRaw("a", metricName, start, end) //
              .body("data.results[0].values.size()", gt(2)) //
              // the first point is incomplete so we ignore it.
              // the value of second and third points must be 12
              // because the server now print this log once per 5s
              .body("data.results[0].values[1][1]", eq("12.0")) //
              .body("data.results[0].values[2][1]", eq("12.0")) //
          ;

          given() //
              .queryParam("name", metricName) //
              .when() //
              .get("/webapi/v1/query/schema") //
              .then() //
              .isSuccess() //
              .body("data.tags", hasItems("app", "hostname", "ip", "host")); //

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
