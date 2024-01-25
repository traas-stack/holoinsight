/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.extension.ceresdbx;

import io.holoinsight.server.common.dao.mapper.TenantOpsMapper;
import io.holoinsight.server.extension.MetricMeterService;
import io.holoinsight.server.extension.MetricStorage;
import io.holoinsight.server.extension.promql.PqlQueryService;
import io.holoinsight.server.extension.promql.RemotePqlConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author jiwliu
 * @date 2023/1/10
 */
@Configuration
@ConditionalOnProperty(value = "holoinsight.metric.storage.type", havingValue = "ceresdbx")
@Import(RemotePqlConfiguration.class)
public class HoloinsightCeresdbxConfiguration {

  @Bean
  public CeresdbxClientManager ceresdbxClientManager(TenantOpsMapper tenantOpsMapper) {
    return new CeresdbxClientManager(tenantOpsMapper);
  }

  @Bean
  public MetricStorage ceresdbxMetricStorage(CeresdbxClientManager ceresdbxClientManager,
      @Autowired(required = false) PqlQueryService pqlQueryService) {
    return new CeresdbxMetricStorage(ceresdbxClientManager, pqlQueryService);
  }

  @Bean
  public MetricMeterService metricMeterService() {
    return new MetricMeterServiceImpl();
  }
}
