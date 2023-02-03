/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.extension.ceresdbx;

import io.holoinsight.server.common.dao.mapper.TenantOpsMapper;
import io.holoinsight.server.extension.MetricStorage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jiwliu
 * @date 2023/1/10
 */
@Configuration
@ConditionalOnProperty(value = "holoinsight.metric.storage.type", havingValue = "ceresdbx")
public class HoloinsightCeresdbxConfiguration {

  @Bean
  public CeresdbxClientManager ceresdbxClientManager(TenantOpsMapper tenantOpsMapper) {
    return new CeresdbxClientManager(tenantOpsMapper);
  }

  @Bean
  public MetricStorage ceresdbxMetricStorage(CeresdbxClientManager ceresdbxClientManager) {
    return new CeresdbxMetricStorage(ceresdbxClientManager);
  }

}
