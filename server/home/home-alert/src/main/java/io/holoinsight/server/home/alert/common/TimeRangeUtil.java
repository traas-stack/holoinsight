/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.common;

import io.holoinsight.server.home.facade.emuns.PeriodType;
import io.holoinsight.server.home.facade.trigger.DataSource;
import io.holoinsight.server.home.facade.trigger.Trigger;
import org.apache.commons.lang3.StringUtils;

/**
 * @author masaimu
 * @version 2023-04-25 14:34:00
 */
public class TimeRangeUtil {

  public static long getStartTimestamp(long timestamp, DataSource dataSource, Trigger trigger) {
    long time = 1L;
    // Compatible format: 1m or 1m-avg
    String downsample = dataSource.getDownsample();
    if (StringUtils.isNotBlank(downsample)) {
      time = parseTime(downsample);
    }
    return timestamp - (trigger.getStepNum() - 1) * time * PeriodType.MINUTE.intervalMillis()
        - trigger.getDownsample() * PeriodType.MINUTE.intervalMillis();
  }

  public static long getEndTimestamp(long timestamp, DataSource dataSource) {
    long time = 1L;
    // Compatible format: 1m or 1m-avg
    String downsample = dataSource.getDownsample();
    if (StringUtils.isNotBlank(downsample)) {
      time = parseTime(downsample);
    }
    return timestamp + time * PeriodType.MINUTE.intervalMillis();
  }

  public static long parseTime(String downsample) {
    String timeStr = downsample;
    if (timeStr.contains("-")) {
      timeStr = timeStr.split("-", 2)[0];
    }
    return Long.parseLong(timeStr.substring(0, timeStr.length() - 1));
  }
}
