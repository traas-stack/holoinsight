/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.bootstrap;

import io.holoinsight.server.common.ContextHolder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <p>
 * created at 2022/11/25
 *
 * @author sw1136562366
 */
@SpringBootApplication
public class HoloinsightGatewayBootstrap {
  public static void main(String[] args) {
    try {
      ContextHolder.ctx = SpringApplication.run(HoloinsightGatewayBootstrap.class, args);
    } catch (Throwable e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
