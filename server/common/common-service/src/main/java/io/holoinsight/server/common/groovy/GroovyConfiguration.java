/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.groovy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * created at 2022/11/25
 *
 * @author xzchaoo
 */
@Configuration
public class GroovyConfiguration {
  @Bean
  public GroovyConfig groovyConfig() {
    return new GroovyConfig();
  }

  @Bean
  public GroovyWebController groovyWebController() {
    return new GroovyWebController();
  }
}
