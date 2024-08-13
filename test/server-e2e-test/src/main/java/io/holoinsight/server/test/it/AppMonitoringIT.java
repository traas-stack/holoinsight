/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

/**
 * <p>
 * App Monitoring integration test.
 * <p>
 * created at 2023/3/11
 *
 * @author xzchaoo
 */
public class AppMonitoringIT extends BaseIT {
  String tenant = "default";
  String app = "holoinsight-server-example";

  @Test
  void test_has_VM_meta() {
    await("Has VM Meta") //
        .untilNoException(() -> {
          given() //
              .pathParam("tenant", tenant) //
              .when() //
              .get("/webapi/meta/{tenant}_app/queryAll") //
              .then() //
              .body("success", IS_TRUE) //
              .rootPath("data.find { it.app == '%s' }", withArgs(app))
              .body("_label", hasEntry("machineType", "VM")) //
          ;
        }); //
  }

  @Test
  public void test_has_correct_displayMenu() {
    await() //
        .atMost(Duration.ofMinutes(10)) //
        .untilNoException(() -> { //
          given() //
              .pathParam("app", "holoinsight-server-example") //
              .when() //
              .get("/webapi/displaymenu/query/apm/{app}") //
              .then() //
              .isSuccess() //
              .rootPath("data.find{ it.name =='系统监控' }") //
              .body(NOT_NULL) //
              .body("children.find { it.name == '单机系统' }", NOT_NULL) //
          ;;
        });
  }

  @Test
  void test_has_system_metrics() {
    // After docker-docker bootstrapped, it takes about 2~3 minutes to generate first CPU data.
    await("Has System Metrics") //
        .atMost(5, TimeUnit.MINUTES) //
        .untilNoException(() -> {
          for (String metric : new String[] {"system_cpu_util", //
              "system_cpu_user", //
              "system_cpu_sys", //
              "system_mem_util", //
              "system_mem_total", //
              "system_mem_used", //
              "system_mem_cach", //
              "system_mem_buff", //
              "system_load_load1", //
              "system_load_load5", //
              "system_load_load15", //
              "system_disk_used", //
              "system_disk_total", //
              "system_disk_util", //
              "system_tcp_active", //
              "system_tcp_pasive", //
              "system_traffic_bytin", //
              "system_traffic_bytout", //
              "system_traffic_pktin", //
              "system_traffic_pktout", //
              "system_process_threads", //
              "system_process_pids", //
          }) {
            JSONObject body = json().put("tenant", tenant);
            JSONArray datasources = jarray();
            body.put("datasources", datasources);
            long now = System.currentTimeMillis();
            datasources.put(json() //
                .put("metric", metric) //
                .put("start", now - 5 * 60 * 1000L) //
                .put("end", now) //
                .put("groupBy", newArrayList("app"))); //
            given() //
                .body(body) //
                .when() //
                .post("/webapi/v1/query") //
                .then() //
                .body("success", IS_TRUE) //
                .rootPath("data.results.find { it.tags.app == '%s' }", withArgs(app)) //
                .body("metric", eq(metric)) //
                .body("values.size()", gt(0)) //
            ;
          }
        });
  }
}
