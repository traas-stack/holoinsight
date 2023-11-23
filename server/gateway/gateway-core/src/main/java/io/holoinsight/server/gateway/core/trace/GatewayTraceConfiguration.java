/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.trace;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.common.springboot.HoloinsightProperties;
import io.holoinsight.server.common.trace.TraceAgentConfigurationService;
import io.holoinsight.server.gateway.core.trace.controller.TraceAgentConfigurationController;
import io.holoinsight.server.gateway.core.trace.exporter.LocalTraceExporter;
import io.holoinsight.server.gateway.core.trace.exporter.RelayTraceExporter;
import io.holoinsight.server.gateway.core.trace.exporter.TraceExporter;

/**
 * <p>
 * created at 2022/11/2
 *
 * @author sw1136562366
 */
@Configuration
@ConditionalOnFeature("trace")
public class GatewayTraceConfiguration {

  /**
   * <p>
   * traceExporter.
   * </p>
   */
  @Bean
  public TraceExporter traceExporter(HoloinsightProperties hp) {
    if (hp.getRoles().getActive().contains("apm")) {
      return new LocalTraceExporter();
    }
    return new RelayTraceExporter();
  }

  @Bean
  public TraceAgentConfigurationController agentConfigurationController() {
    return new TraceAgentConfigurationController();
  }

  /**
   * <p>
   * traceAgentConfigurationService.
   * </p>
   */
  @Bean
  public TraceAgentConfigurationService traceAgentConfigurationService() {
    return new TraceAgentConfigurationService();
  }

  @Bean
  public GatewayOTLPTraceHandler gatewayOTLPTraceHandler() {
    return new GatewayOTLPTraceHandler();
  }
}
