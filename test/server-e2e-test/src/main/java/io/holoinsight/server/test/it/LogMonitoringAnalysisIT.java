/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import java.util.Arrays;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import io.holoinsight.server.test.it.utils.WebapiUtils;
import io.restassured.response.ValidatableResponse;

/**
 * <p>
 * created at 2023/4/26
 *
 * @author xzchaoo
 */
public class LogMonitoringAnalysisIT extends BaseIT {
  long id;

  @Test
  @Order(1)
  public void create_loganalysis_config() {
    JSONObject body = getJsonFromClasspath("requests/LogMonitoringAnalysisIT.json");

    String name = "loganalysis_" + RandomStringUtils.randomAlphabetic(6);
    body.put("name", name);

    id = ((Number) given() //
        .body(body) //
        .when() //
        .post("/webapi/customPlugin/create") //
        .then() //
        .isSuccess() //
        .extract() //
        .path("data.id")) //
            .longValue(); //
  }

  @Test
  @Order(2)
  public void wait_loganalysis_metrics() {
    await() //
        .untilNoException(() -> { //
          long end = System.currentTimeMillis();
          ValidatableResponse resp =
              WebapiUtils.queryMetricsRaw("a", "loganalysis_" + id, end - 5 * 60 * 1000, end) //
                  .isSuccess();
          for (String eventName : Arrays.asList("clusterping", "__analysis")) {
            resp.body("data.results.find { it.tags['eventName'] == '%s' }.values.size()",
                withArgs(eventName), gt(0));
          }
        });
  }

  @Test
  @Order(3)
  public void wait_loganalysis_analysis() {
    await() //
        .untilNoException(() -> { //
          long now = System.currentTimeMillis();

          String name = String.format("loganalysis_%d_analysis", id);
          JSONObject body = json() //
              .put("query", "a") //
              .put("datasources", jarray() //
                  .put(json() //
                      .put("name", "a") //
                      .put("metric", name) //
                      .put("aggregator", "unknown-analysis") //
                      .put("start", now - 5 * 60 * 1000) //
                      .put("end", now) //
                      .put("filters", jarray()) //
                      .put("groupBy",
                          jarray().put("app").put("hostname").put("ip").put("eventName")) //
          )); //

          ValidatableResponse resp = given() //
              .body(body) //
              .when() //
              .post("/webapi/v1/query") //
              .then() //
              .isSuccess();


          resp.rootPath("data.results.find { it.tags['eventName'] == '%s' }",
              withArgs("clusterping")) //
              .body("values.size()", gt(0)) //
              .body("values[0][1]", containsString("fail to ping")) //
          ;

          resp.root("data.results.find { it.tags['eventName'] == '%s' }", withArgs("__analysis")) //
              .body("values.size()", gt(0)) //
              .body("values[0][1]", containsString("template=")) //
              .body("values[0][1]", containsString("cancel maintenance")) //
          ;

        });
  }
}
