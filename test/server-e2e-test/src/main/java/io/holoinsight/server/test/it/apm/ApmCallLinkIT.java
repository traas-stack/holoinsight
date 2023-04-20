/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it.apm;

import java.util.Arrays;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import io.holoinsight.server.test.it.BaseIT;

/**
 * <p>
 * created at 2023/3/31
 *
 * @author xzchaoo
 */
public class ApmCallLinkIT extends BaseIT {
  @Test
  public void test_trace_search_by_app() {
    await() //
        .untilNoException(() -> {
          for (String app : Arrays.asList("demo-client", "demo-server",
              "holoinsight-server-example")) {
            long now = System.currentTimeMillis();
            JSONObject r = json() //
                .put("duration", json() //
                    .put("start", now - 10 * 60 * 1000L) //
                    .put("end", now) //
            ).put("paging", json() //
                .put("pageNum", 1) //
                .put("pageSize", 10) //
            ).put("queryOrder", "BY_DURATION") //
                .put("serviceName", app) //
                .put("tenant", "default") //
                .put("traceState", "ALL") //
            ;

            mustHaveTrace(r);
          }
        });
  }

  @Test
  public void test_trace_search_by_endpoint() {
    await() //
        .untilNoException(() -> {
          for (String endpoint : Arrays.asList("Jedis/incr", "GET:/demo-server")) {
            long now = System.currentTimeMillis();
            JSONObject r = json() //
                .put("duration", json() //
                    .put("start", now - 10 * 60 * 1000L) //
                    .put("end", now) //
            ).put("paging", json() //
                .put("pageNum", 1) //
                .put("pageSize", 10) //
            ).put("queryOrder", "BY_DURATION") //
                .put("endpointName", endpoint) //
                .put("tenant", "default") //
                .put("traceState", "ALL") //
            ;

            mustHaveTrace(r);
          }
        });
  }

  private static void mustHaveTrace(JSONObject body) {
    given() //
        .body(body) //
        .when() //
        .post("/webapi/v1/trace/query/basic") //
        .then() //
        .isSuccess() //
        .body("data.traces.size()", gt(0));
  }
}
