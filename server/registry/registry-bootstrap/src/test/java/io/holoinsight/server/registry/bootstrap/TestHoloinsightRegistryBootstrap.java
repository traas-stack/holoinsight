/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.bootstrap;

import io.holoinsight.server.common.ContextHolder;

import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * 本机开发的启动入口, 此时 profile=dev, 并且log4j会使用 log4j2-test.xml
 * <p>
 * created at 2022/2/25
 *
 * @author zzhb101
 */
public class TestHoloinsightRegistryBootstrap {
  public static void main(String[] args) throws InterruptedException {
    // System.setProperty("debug", "true");
    ContextHolder.ctx = new SpringApplicationBuilder(HoloinsightRegistryBootstrap.class) //
        .profiles("test") //
        .build() //
        .run(args); //
  }
}
