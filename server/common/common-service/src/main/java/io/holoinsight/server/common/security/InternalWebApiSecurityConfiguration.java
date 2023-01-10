/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.security;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * created at 2022/11/18
 *
 * @author xzchaoo
 */
@Configuration
@EnableConfigurationProperties(HoloinsightSecurityProperties.class)
public class InternalWebApiSecurityConfiguration {
  @Bean
  public HoloinsightSecurityWebMvcConfigurer gatewayWebMvcConfigurer() {
    return new HoloinsightSecurityWebMvcConfigurer();
  }

  @Bean
  public InternalWebApiFilter internalWebApiFilter() {
    return new InternalWebApiFilter();
  }

  @Bean
  public InternalWebApiInterceptor internalWebApiInterceptor() {
    return new InternalWebApiInterceptor();
  }
}
