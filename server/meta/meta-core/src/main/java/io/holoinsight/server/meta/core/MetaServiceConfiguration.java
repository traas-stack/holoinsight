/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.core;

import io.holoinsight.server.common.service.SuperCacheService;
import io.holoinsight.server.meta.core.service.bitmap.BitmapDataCoreService;
import io.holoinsight.server.meta.core.service.hashmap.HashMapDataCoreService;
import io.holoinsight.server.meta.dal.service.mapper.MetaDataMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jinyan.ljw
 * @Description MetaServiceConfiguration
 * @date 2023/4/27
 */
@Configuration
@MapperScan(basePackages = {"io.holoinsight.server.meta.dal.service.mapper"})
public class MetaServiceConfiguration {

  @Autowired
  private SuperCacheService superCacheService;

  @Bean("hashMapDataCoreService")
  @ConditionalOnProperty(value = "holoinsight.meta.db_data_mode", havingValue = "mysql")
  public HashMapDataCoreService hashMapDataCoreService(MetaDataMapper metaDataMapper,
      SuperCacheService superCacheService) {
    return new HashMapDataCoreService(metaDataMapper, superCacheService);
  }

  @Bean("bitmapDataCoreService")
  @ConditionalOnProperty(value = "holoinsight.meta.db_data_mode", havingValue = "mysql")
  public BitmapDataCoreService bitmapDataCoreService(MetaDataMapper metaDataMapper,
      SuperCacheService superCacheService) {
    return new BitmapDataCoreService(metaDataMapper, superCacheService);
  }

  @Bean
  @ConditionalOnMissingBean
  public SuperCacheService superCacheService() {
    return new SuperCacheService();
  }
}
