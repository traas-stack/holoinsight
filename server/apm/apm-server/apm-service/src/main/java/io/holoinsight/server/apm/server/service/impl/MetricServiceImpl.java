/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.server.service.impl;

import io.holoinsight.server.apm.common.model.query.Duration;
import io.holoinsight.server.apm.common.model.query.MetricValues;
import io.holoinsight.server.apm.engine.storage.MetricStorage;
import io.holoinsight.server.apm.server.service.MetricService;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author jiwliu
 * @version : TraceServiceImpl.java, v 0.1 2022年09月20日 16:48 xiangwanpeng Exp $
 */
public class MetricServiceImpl implements MetricService {

  @Autowired
  protected MetricStorage metricStorage;

  @Override
  public List<String> listMetrics() {
    return metricStorage.listMetrics();
  }

  @Override
  public MetricValues queryMetric(String tenant, String metric, Duration duration,
      Map<String, Object> conditions) throws IOException {
    return metricStorage.queryMetric(tenant, metric, duration, conditions);
  }

  @Override
  public List<String> querySchema(String metric) {
    return metricStorage.querySchema(metric);
  }
}
