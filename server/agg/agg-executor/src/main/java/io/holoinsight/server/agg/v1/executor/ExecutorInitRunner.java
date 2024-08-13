/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;

import io.holoinsight.server.agg.v1.executor.executor.ExecutorManager;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * created at 2023/9/26
 *
 * @author xzchaoo
 */
@Slf4j
@Order(200)
public class ExecutorInitRunner implements ApplicationRunner {
  @Autowired
  private ExecutorManager manager;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    log.info("ExecutorInitRunner");
    new Thread(manager::run, "agg-executor-manager").start();
  }
}
