/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import lombok.val;

/**
 * <p>
 * Infrastructure page integration test.
 * <p>
 * created at 2023/3/10
 *
 * @author xiangfeng.xzc
 */
public class MetaVMIT extends BaseIT {
  private static final BaseMatcher<String> STRING_NUMBER_GTE_0 = new BaseMatcher<String>() {
    @Override
    public void describeTo(Description description) {

    }

    @Override
    public boolean matches(Object o) {
      return Double.parseDouble((String) o) >= 0;
    }
  };

  String tenant = "default";
  String ip;
  String hostname;

  @Test
  @Order(1)
  public void test_has_VM_metadata() {
    await("Has VM metadata") //
        .untilAsserted(() -> {
          val extract = RestAssured.given() //
              .pathParam("tenant", tenant) //
              .body(json().put("_type", "VM")) //
              .post("/webapi/meta/{tenant}_server/queryByCondition") //
              .then() //
              .body("success", IS_TRUE) //
              .body("data", hasSize(1)) //
              .rootPath("data[0]") //
              .body("app", eq("holoinsight-server-example")) //
              .body("cluster", eq("default")) //
              .body("workspace", eq("default")) //
              .body("_type", eq("VM")) //
              .body("_status", eq("ONLINE")) //
              // .body("hostname", containsString("server")) //
              // .body("name", containsString("server")) //
              .body("_modified",
                  Matchers.greaterThan((float) (System.currentTimeMillis() - 15 * 60 * 1000L))) //
              .extract();
          ip = extract.path("data[0].ip");
          hostname = extract.path("data[0].hostname");
        }); //
  }

  @Test
  @Order(2)
  public void test_has_system_metrics() {
    await("Has system metrics") //
        .untilAsserted(() -> {
          long now = now();

          JSONObject body = json() //
              .put("tenant", tenant) //
              .put("datasources", jarray() //
                  .put(json().put("metric", "system_cpu_util") //
                      .put("aggregator", "none") //
                      .put("start", now - 5 * 60 * 1000) //
                      .put("end", now) //
                      .put("filters", jarray()) //
          ) //
                  .put(json().put("metric", "system_mem_util") //
                      .put("aggregator", "none") //
                      .put("start", now - 5 * 60 * 1000) //
                      .put("end", now) //
                      .put("filters", jarray()) //
          )); //

          // Has cpu&mem metrics
          given() //
              .body(body) //
              .post("/webapi/v1/query") //
              .then() //
              .body("success", IS_TRUE) //
              .body("data.results", hasSize(2)) //
              .body("data.results[0].metric", eq("system_cpu_util")) //
              .body("data.results[0].tags", hasEntry("app", "holoinsight-server-example")) //
              .body("data.results[0].values", not(emptyArray())) //
              .body("data.results[0].values[0][1]", STRING_NUMBER_GTE_0) //
              .body("data.results[1].metric", eq("system_mem_util")) //
              .body("data.results[1].tags", hasEntry("app", "holoinsight-server-example")) //
              .body("data.results[1].values", not(emptyArray())) //
              .body("data.results[1].values[0][1]", STRING_NUMBER_GTE_0) //
          ;
        });
  }

  @Test
  @Order(3)
  public void test_VM_inspect() {
    given() //
        .body(json().put("ip", ip).put("hostname", hostname)) //
        .when() //
        .post("/webapi/agent/inspect") //
        .then() //
        .body("success", IS_TRUE) //
        .rootPath("data") //
        .body("ip", eq(ip)) //
        .body("namespace", IS_NULL) //
        .body("variable.keySet()", hasItems("agent", "cpu", "disk", "golang", "host", "mem", "net")) //
    ;
  }
}
