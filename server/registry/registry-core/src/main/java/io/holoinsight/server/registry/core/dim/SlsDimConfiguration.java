/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.dim;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * created at 2023/8/23
 *
 * @author xzchaoo
 */
@Configuration
public class SlsDimConfiguration {
  @Bean
  public SlsConfig slsConfig() {
    return new SlsConfig();
  }

  @Bean
  public SlsMetaSyncer slsMetaSyncer() {
    return new SlsMetaSyncer();
  }
}
