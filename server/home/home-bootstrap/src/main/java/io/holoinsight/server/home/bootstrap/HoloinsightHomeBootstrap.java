/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.bootstrap;

import io.holoinsight.server.common.ContextHolder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author masaimu
 * @version 2022-12-08 10:26:00
 */
@SpringBootApplication
public class HoloinsightHomeBootstrap {
  public static void main(String[] args) {
    try {
      ContextHolder.ctx = SpringApplication.run(HoloinsightHomeBootstrap.class, args);
    } catch (Throwable e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
