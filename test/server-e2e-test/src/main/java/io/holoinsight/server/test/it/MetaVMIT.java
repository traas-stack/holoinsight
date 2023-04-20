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

import lombok.val;

/**
 * <p>
 * Infrastructure page integration test.
 * <p>
 * created at 2023/3/10
 *
 * @author xzchaoo
 */
public class MetaVMIT extends BaseIT {
  private static final BaseMatcher<String> STRING_NUMBER_GTE_0 = new BaseMatcher<String>() {
    @Override
    public void describeTo(Description description) {

    }

    @Override
    public boolean matches(Object o) {
      return o != null && Double.parseDouble((String) o) >= 0;
    }
  };
  private static final String APP = "holoinsight-server-example";
  String tenant = "default";
  String ip;
  String hostname;

  @Test
  @Order(1)
  public void test_has_VM_metadata() {
    await("Has VM metadata") //
        .untilNoException(() -> {
          val extract = given() //
              .pathParam("tenant", tenant) //
              .body(json().put("_type", "VM")) //
              .post("/webapi/meta/{tenant}_server/queryByCondition") //
              .then() //
              .isSuccess() //
              .rootPath("data.find{ it.app = '%s' }", withArgs(APP)) //
              .body("app", eq(APP)) //
              .body("cluster", eq("default")) //
              .body("workspace", eq("default")) //
              .body("_type", eq("VM")) //
              .body("_status", eq("ONLINE")) //
              // .body("hostname", containsString("server")) //
              // .body("name", containsString("server")) //
              .body("_modified",
                  Matchers.greaterThan((float) (System.currentTimeMillis() - 15 * 60 * 1000L))) //
              .extract();
          ip = extract.path("data.find{ it.app == '%s' }.ip", APP);
          hostname = extract.path("data.find{ it.app == '%s'}.hostname", APP);
        }); //
  }

  @Test
  @Order(2)
  public void test_has_system_metrics() {
    await("Has system metrics") //
        .untilNoException(() -> {
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
              .isSuccess() //
              .rootPath("data.results.find{ it.metric=='%s' && it.tags.app == '%s' }", //
                  withArgs("system_cpu_util", APP)) //
              .body(NOT_NULL) //
              .body("values", not(emptyArray())) // //
              .body("values[0][1]", STRING_NUMBER_GTE_0) //
              .rootPath("data.results.find{ it.metric=='%s' && it.tags.app == '%s' }", //
                  withArgs("system_mem_util", APP)) //
              .body(NOT_NULL) //
              .body("values", not(emptyArray())) //
              .body("values[0][1]", STRING_NUMBER_GTE_0) //
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
