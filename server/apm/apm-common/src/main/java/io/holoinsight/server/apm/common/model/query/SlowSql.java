/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.query;

import lombok.Data;

@Data
public class SlowSql {
  private String serviceName;
  private String address;
  private String statement;
  private String traceId;
  private int latency;
  private long startTime;
}
