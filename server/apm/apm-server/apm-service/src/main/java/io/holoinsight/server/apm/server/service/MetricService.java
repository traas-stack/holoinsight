/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.server.service;

import io.holoinsight.server.apm.common.model.query.Duration;
import io.holoinsight.server.apm.common.model.query.MetricValues;
import io.holoinsight.server.apm.engine.postcal.MetricDefine;

import java.util.List;
import java.util.Map;

public interface MetricService {

  List<String> listMetrics();

  List<MetricDefine> listMetricDefines();

  MetricDefine getMetricDefine(String metric);

  MetricValues queryMetric(String tenant, String metric, Duration duration,
      Map<String, Object> conditions) throws Exception;

  List<String> querySchema(String metric);
}
