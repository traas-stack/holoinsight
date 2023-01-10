/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.bootstrap;

import io.holoinsight.server.common.ContextHolder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Hello world!
 */
@SpringBootApplication
public class HoloinsightMetaBootstrap {
  public static void main(String[] args) {
    try {
      SpringApplication app = new SpringApplicationBuilder(HoloinsightMetaBootstrap.class).build();
      ContextHolder.ctx = app.run(args);
    } catch (Throwable e) {
      e.printStackTrace();
      System.exit(1);
    }

  }
}
