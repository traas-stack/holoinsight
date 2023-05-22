/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.server.service.impl;

import io.holoinsight.server.apm.common.model.query.Duration;
import io.holoinsight.server.apm.common.model.query.MetricValues;
import io.holoinsight.server.apm.common.model.query.StatisticData;
import io.holoinsight.server.apm.common.model.query.StatisticDataList;
import io.holoinsight.server.apm.common.model.specification.sw.Tag;
import io.holoinsight.server.apm.common.model.specification.sw.TraceState;
import io.holoinsight.server.apm.engine.postcal.MetricDefine;
import io.holoinsight.server.apm.engine.postcal.MetricsManager;
import io.holoinsight.server.apm.engine.storage.MetricStorage;
import io.holoinsight.server.apm.server.service.MetricService;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class MetricServiceImpl implements MetricService {

  @Autowired
  protected MetricStorage metricStorage;

  @Autowired
  private MetricsManager metricsManager;

  @Override
  public List<String> listMetrics() {
    return metricsManager.listMetrics();
  }

  @Override
  public List<MetricDefine> listMetricDefines() {
    return metricsManager.listMetricDefines();
  }

  @Override
  public MetricDefine getMetricDefine(String metric) {
    return metricsManager.getMetric(metric);
  }

  @Override
  public MetricValues queryMetric(String tenant, String metric, Duration duration,
      Map<String, Object> conditions) throws Exception {
    return metricStorage.queryMetric(tenant, metric, duration, conditions);
  }

  @Override
  public List<String> querySchema(String metric) {
    return metricStorage.querySchema(metric);
  }

  @Override
  public StatisticData billing(String tenant, String serviceName, String serviceInstanceName,
      String endpointName, List<String> traceIds, int minTraceDuration, int maxTraceDuration,
      TraceState traceState, long start, long end, List<Tag> tags) throws Exception {
    return metricStorage.billing(tenant, serviceName, serviceInstanceName, endpointName, traceIds,
        minTraceDuration, maxTraceDuration, traceState, start, end, tags);
  }

  @Override
  public StatisticDataList statistic(long startTime, long endTime, List<String> groups,
      List<AggregationBuilder> aggregations) throws Exception {
    return metricStorage.statistic(startTime, endTime, groups, aggregations);
  }
}
