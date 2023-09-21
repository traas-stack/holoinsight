/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import io.restassured.response.ValidatableResponse;


/**
 * <p>
 * Agent VM mode integration test.
 * <p>
 * created at 2023/3/12
 *
 * @author xzchaoo
 */
@Timeout(value = 2, unit = TimeUnit.MINUTES)
public class AgentVMIT extends BaseIT {
  String app = "holoinsight-server-example";
  String ip;

  Function<ValidatableResponse, ValidatableResponse> listFilesRespAssert = spec -> spec.isSuccess() //
      .body("data.dirTrees.size()", gt(0)) //
      .root("data.dirTrees[0]").body("name", eq("home")) //
      .body("fullPath", eq("/home")) //
      .root("data.dirTrees[0].subs[0]").body("name", eq("admin")) //
      .body("fullPath", eq("/home/admin")) //
      .root("data.dirTrees[0].subs[0].subs[0]").body("name", eq("logs")) //
      .body("fullPath", eq("/home/admin/logs")) //
      .root("data.dirTrees[0].subs[0].subs[0].subs.find { it.name == 'holoinsight-server' }") //
      .body("name", eq("holoinsight-server")) //
      .body("fullPath", eq("/home/admin/logs/holoinsight-server")) //
      .root(
          "data.dirTrees[0].subs[0].subs[0].subs.find { it.name == 'holoinsight-server' }.subs.find { it.name == 'agent.log' }") //
      .body("name", eq("agent.log")) //
      .body("fullPath", eq("/home/admin/logs/holoinsight-server/agent.log")); //

  @Order(1)
  @Test
  public void test_agent_listFiles_with_app() {
    await().atMost(Duration.ofSeconds(10)).untilNoException(() -> {
      ip = given() //
          .body(json() //
              .put("app", app) //
              .put("logpath", "/home/admin/logs")) //
          .post("/webapi/agent/listFiles") //
          .then() //
          .visit(listFilesRespAssert) //
          .extract() //
          .path("data.ip"); //
    });
  }

  @Order(2)
  @Test
  public void test_agent_listFiles_with_ip() {
    await().atMost(Duration.ofSeconds(10)).untilNoException(() -> {
      given() //
          .body(json() //
              .put("ip", ip) //
              .put("logpath", "/home/admin/logs")) //
          .when() //
          .post("/webapi/agent/listFiles") //
          .then() //
          .visit(listFilesRespAssert); //
    });
  }

  @Order(3)
  @Test
  public void test_agent_preview_with_app() {
    await().atMost(Duration.ofSeconds(10)).untilNoException(() -> {
      given() //
          .body(json() //
              .put("app", app) //
              .put("logpath", "/home/admin/logs/holoinsight-server/agent.log")) //
          .when() //
          .post("/webapi/agent/previewFile") //
          .then() //
          .isSuccess() //
          .body("data.lines.size()", gt(0)) //
      ; //
    });
  }

  @Order(4)
  @Test
  public void test_agent_preview_with_ip() {
    await().atMost(Duration.ofSeconds(10)).untilNoException(() -> {
      given() //
          .body(json() //
              .put("ip", ip) //
              .put("logpath", "/home/admin/logs/holoinsight-server/agent.log")) //
          .when() //
          .post("/webapi/agent/previewFile") //
          .then() //
          .isSuccess() //
          .body("data.lines.size()", gt(0)) //
      ;
    });
  }
}
