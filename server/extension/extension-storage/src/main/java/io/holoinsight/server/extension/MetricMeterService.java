/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.extension;

import io.holoinsight.server.extension.model.Table;
import io.holoinsight.server.extension.model.WriteMetricsParam;

import java.util.Map;

/**
 * @author zanghaibo
 * @time 2023-05-23 10:39 上午
 */
public interface MetricMeterService {
  /**
   * meter metric
   *
   * @param writeMetricsParam
   */
  void meter(WriteMetricsParam writeMetricsParam);

  void meter(String tenant, Table table);

  /**
   * meter one single point
   * 
   * @param tags
   */
  void meter(Map<String, String> tags, String tenant, String metricName);

  Map<String, String> keyGen(String tenant, String name, Map<String, String> tags);
}
