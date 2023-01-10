/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.auth;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * created at 2022/12/1
 *
 * @author xzchaoo
 */
@Configuration
public class ApiKeyAutoConfiguration {
  @Bean
  @ConditionalOnMissingBean
  public ApikeyService apikeyService() {
    return new ApikeyService();
  }

  @Bean
  @ConditionalOnMissingBean
  public ApikeyAuthService apikeyAuthService() {
    return new DefaultApikeyAuthService();
  }

}
