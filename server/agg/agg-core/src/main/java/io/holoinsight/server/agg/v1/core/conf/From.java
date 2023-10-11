/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core.conf;

import javax.annotation.Nonnull;

import lombok.Data;

/**
 * <p>
 * created at 2023/9/22
 *
 * @author xzchaoo
 */
@Data
public class From {
  /**
   * supported:
   * <ul>
   * <li>metrics</li>
   * </ul>
   */
  @Nonnull
  private String type;
  private FromConfigs configs;
  private FromMetrics metrics;

  private CompletenessConfig completeness = new CompletenessConfig();

  public From() {}
}
