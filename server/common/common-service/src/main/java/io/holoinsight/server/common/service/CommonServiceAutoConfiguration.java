/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author jsy1001de
 * @version 1.0: ServiceAutoConfiguration.java, v 0.1 2023年02月27日 下午2:20 jinsong.yjs Exp $
 */
@Configuration
public class CommonServiceAutoConfiguration {
  @Bean
  @ConditionalOnMissingBean
  public MetaDictValueService metaDictValueService() {
    return new MetaDictValueService();
  }

  @Bean
  @ConditionalOnMissingBean
  public MetaDataDictValueService metaDataDictValueService() {
    return new MetaDataDictValueServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public SuperCacheService superCacheService() {
    return new SuperCacheService();
  }
}
