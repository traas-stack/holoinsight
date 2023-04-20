/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it.apm;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import io.holoinsight.server.test.it.BaseIT;
import io.restassured.response.ValidatableResponse;

/**
 * <p>
 * Single trace detail IT.
 * <p>
 * created at 2023/3/31
 *
 * @author xzchaoo
 */
public class ApmCallLinkDetailIT extends BaseIT {
  @Test
  public void test_trace_detail() {
    await() //
        .untilNoException(() -> {
          String endpoint = "Jedis/incr";
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

          String traceId = given() //
              .body(r) //
              .when() //
              .post("/webapi/v1/trace/query/basic") //
              .then() //
              .isSuccess() //
              .body("data.traces.size()", gt(0)) //
              .extract() //
              .body() //
              .path("data.traces[0].traceIds[0]"); //

          r = new JSONObject();
          r.put("tenant", "default");
          r.put("traceIds", jarray().put(traceId));

          ValidatableResponse resp = given() //
              .body(r) //
              .when() //
              .post("/webapi/v1/trace/query") //
              .then() //
              .isSuccess() //
              .body("data.spans.size()", gt(0));

          String rootPath = "data.spans.find{ it.root }";
          String spanIdRoot = resp.rootPath(rootPath) //
              .body("endpointName", eq(
                  "SpringScheduled/io.holoinsight.server.demo.client.DemoClientTask.callDemoServer")) //
              .body("serviceCode", eq("demo-client")) //
              .body("tags.find{ it.key == 'resource.service.name' }.value", eq("demo-client")) //
              .extract().path(rootPath + ".spanId"); //

          // demo-client --OKHttp--> demo-server
          rootPath =
              "data.spans.find{ it.layer =='Http' && it.serviceCode == 'demo-client' && it.peer == 'demo-server:8080' }";
          String spanId1 = resp.rootPath(rootPath) //
              .body("tags.find{ it.key =='attributes.http.url' }.value",
                  eq("http://demo-server:8080/demo-server")) //
              .body("tags.find{ it.key =='attributes.sw8.component' }.value", eq("OKHttp")) //
              .body("parentSpanId", eq(spanIdRoot)) //
              .extract().path(rootPath + ".spanId"); //

          // OKHttp -> demo-server
          rootPath =
              "data.spans.find{ it.layer =='Http' && it.serviceCode == 'demo-server' && it.peer == '' && it.type == 'Entry' }";
          String demoServerSpanId = resp.rootPath(rootPath) //
              .body("parentSpanId", eq(spanId1)) //
              .body("tags.find{ it.key =='attributes.http.url' }.value",
                  eq("http://demo-server:8080/demo-server")) //
              .body("tags.find{ it.key =='attributes.sw8.component' }.value", eq("SpringMVC")) //
              .extract().path(rootPath + ".spanId");

          // demo-server --OKHttp --> https://www.httpbin.org
          resp.rootPath("data.spans.find{ it.serviceCode == '%s' && it.peer == '%s' }",
              withArgs("demo-server", "www.httpbin.org:443")) //
              .body("parentSpanId", eq(demoServerSpanId)) //
              .body("tags.find{ it.key == '%s' }.value", withArgs("attributes.http.url"),
                  eq("https://www.httpbin.org/get")); //

          resp.rootPath("data.spans.find{ it.serviceCode == '%s' && it.endpointName == '%s' }", //
              withArgs("demo-server", "HikariCP/Connection/getConnection")) //
              .body("parentSpanId", eq(demoServerSpanId)); //

          resp.rootPath("data.spans.find{ it.serviceCode == '%s' && it.endpointName == '%s' }", //
              withArgs("demo-server", "Mysql/JDBC/Statement/executeQuery")) //
              .body("parentSpanId", eq(demoServerSpanId)); //

          resp.rootPath("data.spans.find{ it.serviceCode == '%s' && it.endpointName == '%s' }", //
              withArgs("demo-server", "HikariCP/Connection/close")) //
              .body("parentSpanId", eq(demoServerSpanId)); //

          resp.rootPath("data.spans.find{ it.serviceCode == '%s' && it.endpointName == '%s' }", //
              withArgs("demo-server", "Jedis/incr")) //
              .body("parentSpanId", eq(demoServerSpanId)); //

        });
  }
}
