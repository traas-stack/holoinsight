/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.postcal;

import io.holoinsight.server.apm.common.model.query.Duration;
import io.holoinsight.server.apm.common.model.query.MetricValues;
import io.holoinsight.server.apm.common.model.specification.OtlpMappings;
import io.holoinsight.server.apm.engine.storage.MetricStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * PostCalMetricStorage obtains indicator data through post-calculation (such as elasticsearch's
 * DSL)
 */
public abstract class PostCalMetricStorage implements MetricStorage {

  @Autowired
  private MetricsManager metricsManager;

  @Override
  public List<String> querySchema(String metric) {
    Assert.notNull(metric, "metric can not be null!");
    MetricDefine metricDefine = metricsManager.getMetric(metric);
    Assert.notNull(metricDefine, String.format("metric not found: %s", metric));
    return metricDefine.getGroups().stream()
        .map(group -> OtlpMappings.fromOtlp(metricDefine.getIndex(), group))
        .collect(Collectors.toList());
  }

  @Override
  public MetricValues queryMetric(String tenant, String metric, Duration duration,
      Map<String, Object> conditions) throws Exception {
    Assert.notNull(duration, "duration can not be null!");
    MetricDefine metricDefine = metricsManager.getMetric(metric);
    Assert.notNull(metricDefine, String.format("metric not found: %s", metric));
    Map<String, Object> mergedConditions = new HashMap<>();
    if (conditions != null) {
      conditions.forEach(
          (k, v) -> mergedConditions.put(OtlpMappings.toOtlp(metricDefine.getIndex(), k), v));
    }
    if (metricDefine.getConditions() != null) {
      mergedConditions.putAll(metricDefine.getConditions());
    }

    return query(metricDefine, tenant, duration, mergedConditions, metricDefine.getGroups());
  }

  protected abstract MetricValues query(MetricDefine metricDefine, String tenant, Duration duration,
      Map<String, Object> conditions, Collection<String> groups) throws Exception;

}
