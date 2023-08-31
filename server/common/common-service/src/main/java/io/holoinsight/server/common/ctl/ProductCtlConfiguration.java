/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.ctl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductCtlConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public ProductCtlService productCtlService() {
    return new ProductCtlServiceImpl();
  }

}
