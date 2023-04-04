/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.extension.promql;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jiwliu
 * @date 2023/3/8
 */

@Configuration
@EnableConfigurationProperties(RemotePqlProperties.class)
@ConditionalOnProperty(value = "holoinsight.metric.pql.remote.enabled", havingValue = "true")
public class RemotePqlConfiguration {

  @Bean
  public PqlQueryService pqlQueryService(RemotePqlProperties remotePqlProperties) {
    return new HttpPqlQueryService(remotePqlProperties);
  }

}
