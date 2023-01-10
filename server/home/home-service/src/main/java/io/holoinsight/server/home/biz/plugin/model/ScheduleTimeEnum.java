/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin.model;

/**
 * @author masaimu
 * @version 2022-10-31 13:58:00
 */
public enum ScheduleTimeEnum {
  WAIT_5_SEC(5_000L), WAIT_15_SEC(10_000L), WAIT_30_SEC(30_000L), WAIT_45_SEC(45_000L), WAIT_1_MIN(
      60_000L), WAIT_5_MIN(300_000L), WAIT_10_MIN(600_000L);

  long timeRange;

  ScheduleTimeEnum(long timeRange) {
    this.timeRange = timeRange;
  }

  public long getTimeRange() {
    return timeRange;
  }
}
