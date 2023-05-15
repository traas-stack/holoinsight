/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.query;

import lombok.Data;

@Data
public class StatisticData {
  private String tenant;
  private long traceCount;
  private long spanCount;
  private long serviceCount;
  private long serviceInstanceCount;
  private long endpointCount;
  private double successRate;
  private double avgLatency;
}
