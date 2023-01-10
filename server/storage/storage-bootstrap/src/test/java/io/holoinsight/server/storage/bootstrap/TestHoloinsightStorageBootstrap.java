/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.bootstrap;

import io.holoinsight.server.common.ContextHolder;

import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * <p>
 * created at 2022/11/28
 *
 * @author jiwliu
 */
public class TestHoloinsightStorageBootstrap {
  public static void main(String[] args) {
    ContextHolder.ctx = new SpringApplicationBuilder(HoloinsightStorageBootstrap.class)
        .profiles("dev").build().run(args);
  }
}
