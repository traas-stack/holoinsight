/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.utils;

public enum DownSampling {
  /**
   * None downsampling is for un-time-series data.
   */
  None(0, ""),
  /**
   * Second downsampling is not for metrics, but for record, profile and top n. Those are details
   * but don't do aggregation, and still merge into day level in the persistence.
   */
  Second(1, "second"), Minute(2, "minute"), Hour(3, "hour"), Day(4, "day");

  private final int value;
  private final String name;

  DownSampling(int value, String name) {
    this.value = value;
    this.name = name;
  }

  public int getValue() {
    return value;
  }

  public String getName() {
    return name;
  }
}
