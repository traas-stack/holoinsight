/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it.utils;

import org.json.JSONObject;

import io.holoinsight.server.test.it.BaseIT;
import io.restassured.response.ValidatableResponse;

/**
 * <p>
 * created at 2023/3/17
 *
 * @author xzchaoo
 */
public class WebapiUtils extends BaseIT {

  public static ValidatableResponse querySchema(String metricName) {
    return given() //
        .queryParam("name", metricName) //
        .when() //
        .get("/webapi/v1/query/schema") //
        .then();
  }

  public static ValidatableResponse queryMetricsRaw(String name, String metric, long start,
      long end) {
    JSONObject body = json() //
        .put("query", name) //
        .put("datasources", jarray() //
            .put(json() //
                .put("name", name) //
                .put("metric", metric) //
                .put("aggregator", "none") //
                .put("start", start) //
                .put("end", end + 60_000) //
                .put("filters", jarray()) //
                .put("groupBy", jarray()) //
            )) //
    ;

    return given() //
        .body(body) //
        .when() //
        .post("/webapi/v1/query") //
        .then() //
        .body("success", IS_TRUE) //
    ;
  }
}
