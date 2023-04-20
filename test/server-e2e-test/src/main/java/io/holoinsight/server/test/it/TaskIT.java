/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import io.holoinsight.server.home.common.model.TaskEnum;
import org.junit.jupiter.api.Test;

import java.time.Duration;

/**
 * @author masaimu
 * @version 2023-04-14 16:09:00
 */
public class TaskIT extends BaseIT {

  @Test
  public void test_task_scheduler() {
    await("Test task scheduler") //
        .atMost(Duration.ofMinutes(2)) //
        .untilNoException(() -> {
          given() //
              .pathParam("taskId", TaskEnum.TASK_DEMO.getCode()) //
              .when() //
              .get("/webapi/task/cluster/{taskId}") //
              .then() //
              .body("success", IS_TRUE) //
              .body("data.size()", gt(0));
        });
  }
}
