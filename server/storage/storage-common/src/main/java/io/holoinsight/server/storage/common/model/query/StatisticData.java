/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.common.model.query;

import lombok.Data;

@Data
public class StatisticData {
  private String appId;
  private String envId;
  private String tenant;
  private long serviceCount;
  private long traceCount;

}
