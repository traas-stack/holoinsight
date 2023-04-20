/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it.apm;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import io.holoinsight.server.test.it.BaseIT;

/**
 * <p>
 * created at 2023/3/31
 *
 * @author xzchaoo
 */
public class ApmTopologyIT extends BaseIT {
  @Test
  public void test_topology() {
    await() //
        .untilNoException(() -> {

          long now = System.currentTimeMillis();
          JSONObject r = json() //
              .put("category", "service") //
              .put("depth", 2) //
              .put("start", now - 10 * 60 * 1000L) //
              .put("end", now) //
              .put("tenant", "default") //
              .put("serviceName", "demo-client") //
              .put("time", json() //
                  .put("env", "prod") //
                  .put("starttime", now - 10 * 60 * 1000L) //
                  .put("endtime", now) //
                  .put("range", "1hour,now") //
          ) //
          ;
          given() //
              .body(r) //
              .when() //
              .post("/webapi/v1/trace/query/topology") //
              .then() //
              .isSuccess() //
              .body("data.calls.find { it.sourceName=='%s' && it.destName=='%s' }", //
                  withArgs("demo-client", "demo-server"), NOT_NULL) //
              .body("data.calls.find { it.sourceName=='%s' && it.destName=='%s' }", //
                  withArgs("demo-client", "demo-redis:6379"), NOT_NULL) //
              .body("data.calls.find { it.sourceName=='%s' && it.destName=='%s' }", //
                  withArgs("demo-server", "mysql:3306"), NOT_NULL) //
              .body("data.calls.find { it.sourceName=='%s' && it.destName=='%s' }", //
                  withArgs("demo-server", "www.httpbin.org:443"), NOT_NULL) //
              .body("data.nodes.find { it.name=='%s' }.type", withArgs("mysql:3306"), eq("Mysql")) //
              .body("data.nodes.find { it.name=='%s' }", withArgs("www.httpbin.org:443"), NOT_NULL) //
              .body("data.nodes.find { it.name=='%s' }", withArgs("demo-client"), NOT_NULL) //
              .body("data.nodes.find { it.name=='%s' }", withArgs("demo-server"), NOT_NULL) //
              .body("data.nodes.find { it.name=='%s' }.metric.totalCount", withArgs("demo-server"),
                  gt(0)) //
              .body("data.nodes.find { it.name=='%s' }.type", withArgs("demo-redis:6379"),
                  eq("Redis")) //
          ;
        });
  }
}
