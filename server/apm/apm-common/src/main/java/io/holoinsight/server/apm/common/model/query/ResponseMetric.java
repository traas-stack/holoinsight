/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.query;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
public class ResponseMetric implements Serializable {
  private double avgLatency;
  private double p95Latency;
  private double p99Latency;
  private int totalCount;
  private int errorCount;
  private double successRate;

  public ResponseMetric() {}

  public ResponseMetric(double avgLatency, double p95Latency, double p99Latency, int totalCount,
      int errorCount) {
    this.avgLatency = avgLatency;
    this.p95Latency = p95Latency;
    this.p99Latency = p99Latency;
    this.totalCount = totalCount;
    this.errorCount = errorCount;
    this.successRate = ((double) (totalCount - errorCount) / totalCount) * 100;
  }

}
