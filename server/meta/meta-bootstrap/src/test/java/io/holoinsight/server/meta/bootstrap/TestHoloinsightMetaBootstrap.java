/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.bootstrap;

import io.holoinsight.server.common.ContextHolder;

import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * <p>
 * created at 2022/11/26
 *
 * @author jsy1001de
 */
public class TestHoloinsightMetaBootstrap {
  public static void main(String[] args) {
    ContextHolder.ctx = new SpringApplicationBuilder(HoloinsightMetaBootstrap.class)
        .profiles("test").build().run(args);
  }
}
