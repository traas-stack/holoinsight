/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.server.service.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.common.model.query.Duration;
import io.holoinsight.server.storage.common.model.query.MetricValues;
import io.holoinsight.server.storage.engine.elasticsearch.service.MetricEsService;
import io.holoinsight.server.storage.server.service.MetricService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author jiwliu
 * @version : TraceServiceImpl.java, v 0.1 2022年09月20日 16:48 xiangwanpeng Exp $
 */
@Service
@ConditionalOnFeature("trace")
public class MetricServiceImpl implements MetricService {

  @Autowired
  @Qualifier("spanMetricEsServiceImpl")
  private MetricEsService metricEsService;

  @Override
  public List<String> listMetrics() {
    return metricEsService.listMetrics();
  }

  @Override
  public MetricValues queryMetric(String tenant, String metric, Duration duration,
      Map<String, Object> conditions) throws IOException {
    return metricEsService.queryMetric(tenant, metric, duration, conditions);
  }

  @Override
  public List<String> querySchema(String metric) {
    return metricEsService.querySchema(metric);
  }
}
