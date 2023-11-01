/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core.conf;

import java.util.concurrent.TimeUnit;

import lombok.Data;

/**
 * Agg task fill-zero config
 * <p>
 * created at 2023/11/1
 *
 * @author xzchaoo
 */
@Data
public class FillZero {
  /**
   * Whether to enable fill-zero logic
   */
  private boolean enabled = false;

  /**
   * Limit the maximum number of historyTags
   */
  private int keyLimit = 1000;

  /**
   * <p>
   * This field represents the survival time of each history tag since its last occurrence.
   * <p>
   * Defaults to 1day.
   */
  private long expireTime = TimeUnit.DAYS.toMillis(1);
}
