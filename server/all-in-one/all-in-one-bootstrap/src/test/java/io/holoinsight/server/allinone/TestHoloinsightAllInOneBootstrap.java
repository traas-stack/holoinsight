/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.allinone;

import io.holoinsight.server.allinone.bootstrap.HoloinsightAllInOneBootstrap;
import io.holoinsight.server.common.ContextHolder;

import org.springframework.boot.SpringApplication;

/**
 * <p>
 * created at 2022/11/25
 *
 * @author xzchaoo
 */
public class TestHoloinsightAllInOneBootstrap {
  public static void main(String[] args) {
    ContextHolder.ctx = SpringApplication.run(HoloinsightAllInOneBootstrap.class);
  }
}
