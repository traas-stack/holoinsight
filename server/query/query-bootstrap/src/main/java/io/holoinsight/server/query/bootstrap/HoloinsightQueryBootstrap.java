/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.query.bootstrap;

import io.holoinsight.server.common.ContextHolder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <p>
 * created at 2022/11/26
 *
 * @author xiangwanpeng
 */
@SpringBootApplication
public class HoloinsightQueryBootstrap {
  public static void main(String[] args) {
    try {
      ContextHolder.ctx = SpringApplication.run(HoloinsightQueryBootstrap.class, args);
    } catch (Throwable e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
