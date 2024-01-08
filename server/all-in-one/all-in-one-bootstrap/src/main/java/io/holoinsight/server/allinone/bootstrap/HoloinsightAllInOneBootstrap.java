/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.allinone.bootstrap;

import io.holoinsight.server.common.ContextHolder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * 'all-in-one' module uses Spring Boot AutoConfiguration to discover sub-modules, such as
 * 'HoloinsightRegistryConfiguration'. And use property 'holoinsight.roles.active' to activate
 * specified sub-modules.
 * <p>
 * created at 2022/11/14
 *
 * @author xzchaoo
 */
@SpringBootApplication(
    exclude = {ElasticsearchRestClientAutoConfiguration.class, FlywayAutoConfiguration.class})
public class HoloinsightAllInOneBootstrap {
  public static void main(String[] args) {
    try {
      SpringApplication app =
          new SpringApplicationBuilder(HoloinsightAllInOneBootstrap.class).build();
      ContextHolder.ctx = app.run(args);
    } catch (Throwable e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
