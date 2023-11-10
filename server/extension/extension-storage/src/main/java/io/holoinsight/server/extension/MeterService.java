/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.extension;

import io.holoinsight.server.extension.model.WriteMetricsParam;

/**
 * @author zanghaibo
 * @time 2023-05-23 10:39 上午
 */
public interface MeterService {
  /**
   * meter metric
   *
   * @param writeMetricsParam
   */
  void meter(WriteMetricsParam writeMetricsParam);
}
