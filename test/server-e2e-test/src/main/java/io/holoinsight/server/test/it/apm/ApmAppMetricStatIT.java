/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it.apm;

import java.util.Arrays;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import io.holoinsight.server.test.it.BaseIT;
import io.restassured.response.ValidatableResponse;

/**
 * <p>
 * APM app metric stat IT
 * <p>
 * created at 2023/4/5
 *
 * @author xzchaoo
 */
public class ApmAppMetricStatIT extends BaseIT {
  @Test
  public void test_serviceList() {
    await() //
        .untilNoException(() -> {

          long now = System.currentTimeMillis();
          JSONObject body = json() //
              .put("start", now - 10 * 60 * 1000L) //
              .put("end", now) //
              .put("tenant", "default");

          ValidatableResponse resp = given() //
              .body(body) //
              .when() //
              .post("/webapi/v1/trace/query/serviceList") //
              .then() //
              .isSuccess();

          for (String app : Arrays.asList("demo-server", "holoinsight-server-example")) {
            resp.rootPath("data.find{ it.name == '%s' }", withArgs(app)) //
                .body(NOT_NULL) //
                .body("metric.avgLatency", gt(0F)) //
                .body("metric.p95Latency", gt(0F)) //
                .body("metric.p99Latency", gt(0F)) //
                .body("metric.successRate", gt(0F)) //
                .body("metric.totalCount", gt(0)) //
            ;
          }
        });
  }
}
