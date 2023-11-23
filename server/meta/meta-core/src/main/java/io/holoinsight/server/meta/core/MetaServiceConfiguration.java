/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.core;

import com.mongodb.client.MongoDatabase;
import io.holoinsight.server.common.service.SuperCacheService;
import io.holoinsight.server.meta.core.service.DBCoreService;
import io.holoinsight.server.meta.core.service.MongoDataCoreService;
import io.holoinsight.server.meta.core.service.SqlDataCoreService;
import io.holoinsight.server.meta.core.service.bitmap.BitmapDataCoreService;
import io.holoinsight.server.meta.dal.service.mapper.MetaDataMapper;
import org.mybatis.spring.annotation.MapperScan;
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

  @Bean("mongoDataCoreService")
  @ConditionalOnProperty(value = "holoinsight.meta.db_data_mode", havingValue = "mongodb",
      matchIfMissing = true)
  public DBCoreService MongoDataCoreService(MongoDatabase mongoDatabase) {
    return new MongoDataCoreService(mongoDatabase);
  }

  @Bean("sqlDataCoreService")
  @ConditionalOnProperty(value = "holoinsight.meta.db_data_mode", havingValue = "mysql")
  public DBCoreService SqlDataCoreService(MetaDataMapper metaDataMapper,
      SuperCacheService superCacheService) {
    return new BitmapDataCoreService(metaDataMapper, superCacheService);
  }

  @Bean
  @ConditionalOnMissingBean
  public SuperCacheService superCacheService() {
    return new SuperCacheService();
  }
}
