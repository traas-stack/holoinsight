/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor;

import io.holoinsight.server.agg.v1.executor.executor.RegistryHttpCompletenessService;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

/**
 * <p>
 * created at 2023/9/12
 *
 * @author xzchaoo
 */
@SpringBootApplication
public class ExecutorMain {
  @Bean
  public CompletenessService completenessService() {
    return new RegistryHttpCompletenessService();
  }

  public static void main(String[] args) {
    new SpringApplicationBuilder(ExecutorMain.class) //
        .web(WebApplicationType.NONE) //
        .build() //
        .run(args); //
  }
}
