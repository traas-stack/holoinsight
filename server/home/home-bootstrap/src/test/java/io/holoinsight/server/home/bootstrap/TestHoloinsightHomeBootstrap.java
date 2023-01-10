/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.bootstrap;

import io.holoinsight.server.common.ContextHolder;

import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author masaimu
 * @version 2022-12-08 10:26:00
 */
public class TestHoloinsightHomeBootstrap {
  public static void main(String[] args) {
    // System.setProperty("debug", "true");
    ContextHolder.ctx = new SpringApplicationBuilder(HoloinsightHomeBootstrap.class)
        .profiles("test").build().run(args);
  }
}
