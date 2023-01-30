/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.allinone;

import io.holoinsight.server.allinone.bootstrap.HoloinsightAllInOneBootstrap;
import io.holoinsight.server.common.ContextHolder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * <p>
 * created at 2022/11/25
 *
 * @author xzchaoo
 */
public class TestHoloinsightAllInOneBootstrap {
  public static void main(String[] args) {
    ContextHolder.ctx = new SpringApplicationBuilder(HoloinsightAllInOneBootstrap.class)
        .profiles("test").build().run(args);
  }
}
