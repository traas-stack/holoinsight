/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.otlp.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.holoinsight.server.otlp.core.ConsoleOTLPMetricsHandler;
import io.holoinsight.server.otlp.core.OTLPMetricsHandler;

/**
 * <p>
 * created at 2023/11/2
 *
 * @author xzchaoo
 */
@Configuration
public class HoloInsightOTLPAutoConfiguration {
  @Bean
  public OTLPWebController otlpWebController() {
    return new OTLPWebController();
  }

  @Bean
  public OTLPGrpcServer otlpGrpcServer() {
    return new OTLPGrpcServer();
  }

  @Bean
  public OTLPMetricsHandler otlpMetricsHandler() {
    return new ConsoleOTLPMetricsHandler();
  }
}
