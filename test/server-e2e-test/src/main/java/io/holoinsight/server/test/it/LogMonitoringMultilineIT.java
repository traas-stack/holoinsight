/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import io.holoinsight.server.test.it.utils.WebapiUtils;
import io.restassured.response.ValidatableResponse;

/**
 * <p>
 * log monitoring multiline IT
 * <p>
 * /home/admin/logs/holoinsight/test/multiline.log prints the following exception stacks every 5
 * seconds.
 * <ul>
 * <li>exception stack in thread-0 with keyword</li>
 * <li>exception stack in thread-1 with keyword</li>
 * <li>exception stack in thread-1 without keyword</li>
 * </ul>
 * <p>
 * created at 2023/4/21
 *
 * @author xzchaoo
 */
public class LogMonitoringMultilineIT extends BaseIT {
  Long id;
  Long idWithoutFilter;

  /**
   * Test creating log monitoring item
   */
  @Order(1)
  @Test
  public void test_create_log_monitoring_item() {
    // Create a log monitoring item with random name
    String name = "LogMonitoringMultilineIT_" + RandomStringUtils.randomAlphabetic(6);

    // Create a log monitor item with filter 'contains index_not_found_exception' group by
    // thread. The result of thread-0 is expected to be 12.0. The result of thread-1 is expected to
    // be 12.
    JSONObject body = getJsonFromClasspath("requests/LogMonitoringMultilineIT.json");
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


    // Create a log monitor item without any filter group by thread. The result of thread-0 is
    // expected to be 12. The result of thread-1 is expected to be 24.
    body.put("name", name + "_2");
    body.getJSONObject("conf").remove("whiteFilters");
    idWithoutFilter = ((Number) given() //
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
          long start = end - TimeUnit.MINUTES.toMillis(5);

          String metricName = "multiline_count_" + id;

          ValidatableResponse resp = WebapiUtils.queryMetricsRaw("a", metricName, start, end);
          for (String thread : Arrays.asList("thread-0", "thread-1")) {
            int size = resp
                .body("data.results.find { it.tags['thread'] == '%s' }", withArgs(thread), NOT_NULL) //
                .extract() //
                .path("data.results.find { it.tags['thread'] == '%s' }.values.size()", thread);

            // for (int i = 0; i < 2; i++) {
            // resp.body("data.results.find { it.tags['thread'] == '%s' }.values[%d][1]",
            // withArgs(thread, size - 1 - i), greaterThan(0.0));
            // }
          }

          given() //
              .queryParam("name", metricName) //
              .when() //
              .get("/webapi/v1/query/schema") //
              .then() //
              .isSuccess() //
              .body("data.tags", hasItems("app", "hostname", "ip", "thread")); //

        });
  }


  @Order(3)
  @Test
  public void test_wait_log_monitoring_metric2() {
    await("Test querying log monitoring metrics") //
        .untilNoException(() -> {
          long end = System.currentTimeMillis() / 60000 * 60000;
          long start = end - TimeUnit.MINUTES.toMillis(5);

          String metricName = "multiline_count_" + idWithoutFilter;

          ValidatableResponse resp = WebapiUtils.queryMetricsRaw("a", metricName, start, end);
          for (String thread : Arrays.asList("thread-0", "thread-1")) {
            int size = resp
                .body("data.results.find { it.tags['thread'] == '%s' }", withArgs(thread), NOT_NULL) //
                .extract() //
                .path("data.results.find { it.tags['thread'] == '%s' }.values.size()", thread);

            // for (int i = 0; i < 2; i++) {
            // Double expected = 0.0;
            // if ("thread-1".equals(thread)) {
            // expected = 0.0;
            // }
            // resp.body("data.results.find { it.tags['thread'] == '%s' }.values[%d][1]",
            // withArgs(thread, size - 1 - i), greaterThan(expected));
            // }
          }

          given() //
              .queryParam("name", metricName) //
              .when() //
              .get("/webapi/v1/query/schema") //
              .then() //
              .isSuccess() //
              .body("data.tags", hasItems("app", "hostname", "ip", "thread")); //

        });
  }


  /**
   * Test deleting log monitoring item
   */
  @Order(4)
  @Test
  public void test_delete_log_monitoring_item() {
    for (long id : Arrays.asList(id, idWithoutFilter)) {
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

}
