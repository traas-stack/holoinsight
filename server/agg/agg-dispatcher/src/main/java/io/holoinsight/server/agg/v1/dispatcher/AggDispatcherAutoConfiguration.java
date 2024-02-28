/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.dispatcher;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;

import io.holoinsight.server.agg.v1.core.AggProperties;
import io.holoinsight.server.agg.v1.dispatcher.mock.AggDispatcherMockDataGenerator;
import io.holoinsight.server.common.dao.CommonDaoConfiguration;
import io.holoinsight.server.common.springboot.ConditionalOnRole;

/**
 * <p>
 * created at 2023/9/18
 *
 * @author xzchaoo
 */
@EnableConfigurationProperties(AggProperties.class)
@ConditionalOnRole("agg-dispatcher")
@Import(CommonDaoConfiguration.class)
public class AggDispatcherAutoConfiguration {
  @Bean
  public AggTaskV1StorageForDispatcher aggTaskV1StorageForDispatcher() {
    return new AggTaskV1StorageForDispatcher();
  }

  @Bean
  public AggTaskV1Syncer2 aggTaskV1Syncer2() {
    return new AggTaskV1Syncer2();
  }

  @Bean
  public AggGatewayHook aggGatewayHook() {
    return new AggGatewayHook();
  }

  @Bean
  public AggAgentEventHandler aggAgentEventHandler() {
    return new AggAgentEventHandler();
  }

  @DependsOn("aggTaskV1Syncer2")
  @Bean
  public AggDispatcher aggDispatcher() {
    return new AggDispatcher();
  }

  @Bean
  public AggConfig aggConfig() {
    return new AggConfig();
  }

  @ConditionalOnProperty(value = "holoinsight.agg.dispatcher.mock.enabled", havingValue = "true")
  @Bean
  public AggDispatcherMockDataGenerator aggDispatcherMockDataGenerator() {
    return new AggDispatcherMockDataGenerator();
  }

  @ConditionalOnMissingBean
  @ConditionalOnRole("apm")
  @Bean
  public AggSpanStorageHook aggSpanStorageHook() {
    return new AggSpanStorageHook();
  }
}
