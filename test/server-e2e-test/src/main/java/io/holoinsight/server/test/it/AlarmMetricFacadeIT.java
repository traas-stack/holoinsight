/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import io.restassured.response.Response;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

public class AlarmMetricFacadeIT extends BaseIT {

  String metric;

  Supplier<Response> queryByMetric = () -> given() //
      .pathParam("metric", metric) //
      .when() //
      .get("/webapi/alarmMetric/query/{metric}"); //

  @Order(1)
  @Test
  public void test_query_alarm_metric() {
    Response response = queryByMetric.get();
    System.out.println(response.body().print());
    response //
        .then() //
        .body("success", IS_TRUE);
  }
}
