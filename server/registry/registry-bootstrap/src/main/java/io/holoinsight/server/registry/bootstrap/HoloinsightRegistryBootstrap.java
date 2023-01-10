/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.bootstrap;

import io.holoinsight.server.common.ContextHolder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <p>
 * created at 2022/11/14
 *
 * @author zzhb101
 */
@SpringBootApplication
public class HoloinsightRegistryBootstrap {
  public static void main(String[] args) throws Exception {
    try {
      ContextHolder.ctx = SpringApplication.run(HoloinsightRegistryBootstrap.class, args);
    } catch (Throwable e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
