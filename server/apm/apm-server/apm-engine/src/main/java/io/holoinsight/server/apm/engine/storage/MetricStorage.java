/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.storage;

import io.holoinsight.server.apm.common.model.query.Duration;
import io.holoinsight.server.apm.common.model.query.MetricValues;

import java.util.List;
import java.util.Map;

public interface MetricStorage extends ReadableStorage {

  List<String> listMetrics();

  MetricValues queryMetric(String tenant, String metric, Duration duration,
      Map<String, Object> conditions) throws Exception;

  List<String> querySchema(String metric);

}
