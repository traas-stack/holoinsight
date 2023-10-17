/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.holoinsight.server.agg.v1.executor.CompletenessService;
import io.holoinsight.server.agg.v1.executor.output.XOutput;
import io.holoinsight.server.common.springboot.ConditionalOnRole;
import io.holoinsight.server.extension.MetricStorage;
import io.holoinsight.server.registry.core.collecttarget.CollectTargetStorage;

/**
 * <p>
 * created at 2023/10/16
 *
 * @author xzchaoo
 */
@Configuration
@ConditionalOnRole("agg-executor")
@ConditionalOnBean(CollectTargetStorage.class)
public class AggExecutorRegistryAutoConfiguration {

  @Bean
  @ConditionalOnBean(MetricStorage.class)
  public XOutput metricStorageOutput() {
    return new MetricStorageOutput();
  }

  @Bean
  public CompletenessService completenessService() {
    return new RegistryInternalCompletenessService();
  }
}
