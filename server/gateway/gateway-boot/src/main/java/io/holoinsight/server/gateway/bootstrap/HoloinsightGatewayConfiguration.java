/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.bootstrap;

import io.holoinsight.server.common.auth.ApiKeyAutoConfiguration;
import io.holoinsight.server.common.config.ConfigConfiguration;
import io.holoinsight.server.common.dao.CommonDaoConfiguration;
import io.holoinsight.server.common.groovy.GroovyConfiguration;
import io.holoinsight.server.common.security.InternalWebApiSecurityConfiguration;
import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.common.springboot.ConditionalOnRole;
import io.holoinsight.server.common.springboot.HoloinsightProperties;
import io.holoinsight.server.common.threadpool.ThreadPoolConfiguration;
import io.holoinsight.server.extension.MetricStorage;
import io.holoinsight.server.extension.NoopMetricStorage;
import io.holoinsight.server.extension.ceresdbx.HoloinsightCeresdbxConfiguration;
import io.holoinsight.server.gateway.core.grpc.GatewayProperties;
import io.holoinsight.server.gateway.core.trace.controller.TraceAgentConfigurationController;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * <p>
 * created at 2022/11/25
 *
 * @author sw1136562366
 */
@Configuration
@ConditionalOnRole("gateway")
@ComponentScan({"io.holoinsight.server.gateway",})
@EnableConfigurationProperties({HoloinsightProperties.class, GatewayProperties.class})
@Import({ConfigConfiguration.class, GroovyConfiguration.class, ThreadPoolConfiguration.class,
    InternalWebApiSecurityConfiguration.class, ApiKeyAutoConfiguration.class,
    CommonDaoConfiguration.class, HoloinsightCeresdbxConfiguration.class})
public class HoloinsightGatewayConfiguration {
  @Bean
  @ConditionalOnMissingBean
  public MetricStorage metricStorage() {
    return new NoopMetricStorage();
  }

  /**
   * <p>
   * agentConfigurationController.
   * </p>
   */
  @Bean
  @ConditionalOnFeature("trace")
  public TraceAgentConfigurationController agentConfigurationController() {
    return new TraceAgentConfigurationController();
  }

}
