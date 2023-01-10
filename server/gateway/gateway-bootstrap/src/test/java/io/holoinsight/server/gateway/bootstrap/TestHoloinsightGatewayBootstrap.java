/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.bootstrap;

import io.holoinsight.server.common.ContextHolder;

import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * <p>
 * created at 2022/11/25
 *
 * @author sw1136562366
 */
public class TestHoloinsightGatewayBootstrap {
  public static void main(String[] args) {
    ContextHolder.ctx = new SpringApplicationBuilder(HoloinsightGatewayBootstrap.class) //
        .profiles("dev") //
        .build() //
        .run(args); //
  }
}
