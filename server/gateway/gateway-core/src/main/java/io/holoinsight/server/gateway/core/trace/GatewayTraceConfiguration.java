/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.trace;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.common.springboot.HoloinsightProperties;
import io.holoinsight.server.gateway.core.trace.config.TraceAgentConfigurationService;
import io.holoinsight.server.gateway.core.trace.exporter.LocalTraceExporter;
import io.holoinsight.server.gateway.core.trace.exporter.RelayTraceExporter;
import io.holoinsight.server.gateway.core.trace.exporter.TraceExporter;
import io.holoinsight.server.gateway.core.trace.receiver.opentelemetry.TraceServiceImpl;
import io.holoinsight.server.gateway.core.trace.scheduler.TraceAgentConfigurationScheduler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

  /**
   * <p>
   * gatewayTraceGrpcServer.
   * </p>
   */
  @Bean
  public GatewayTraceGrpcServer gatewayTraceGrpcServer() {
    return new GatewayTraceGrpcServer();
  }

  /**
   * <p>
   * gateway_traceServiceImpl.
   * </p>
   */
  @Bean
  @Qualifier("gateway_traceServiceImpl")
  public TraceServiceImpl gateway_traceServiceImpl() {
    return new TraceServiceImpl();
  }

  /**
   * <p>
   * agentConfigurationScheduler.
   * </p>
   */
  @Bean
  public TraceAgentConfigurationScheduler agentConfigurationScheduler() {
    return new TraceAgentConfigurationScheduler();
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
}
