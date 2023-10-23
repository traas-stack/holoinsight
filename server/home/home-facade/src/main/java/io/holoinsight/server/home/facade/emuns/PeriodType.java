/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade.emuns;

import java.util.Calendar;

public enum PeriodType {

  SECOND(1000, "1s"), //
  FIVE_SECOND(5000, "5s"), //
  TEN_SECOND(10000, "10s"), //
  MINUTE(60000, "1min"), //
  FIVE_MINUTE(5 * 60000, "5min"), //
  QUARTER_HOUR(15 * 60000, "15min"), //
  HALF_HOUR(30 * 60000, "30min"), //
  HOUR(60 * 60 * 1000, "1hour"), //
  DAY(24 * 60 * 60 * 1000, "1day"), //
  WEEK(7 * 24 * 60 * 60 * 1000, "1week");

  private final int intervalMillis;
  private final String desc;

  PeriodType(int intervalMillis, String desc) {
    this.intervalMillis = intervalMillis;
    this.desc = desc;
  }

  public int intervalMillis() {
    return intervalMillis;
  }

  private long roundingDay(long period) {
    if (period % PeriodType.DAY.intervalMillis >= PeriodType.HOUR.intervalMillis * 16) {
      return period / intervalMillis * intervalMillis + 16 * PeriodType.HOUR.intervalMillis;
    } else {
      return period / intervalMillis * intervalMillis - 8 * PeriodType.HOUR.intervalMillis;
    }
  }

  private long roundingWeek(long period) {
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(period);
    cal.setFirstDayOfWeek(Calendar.MONDAY);
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    return cal.getTimeInMillis();
  }

  public long rounding(long period) {
    switch (this) {
      case DAY: {
        return roundingDay(period);
      }
      case WEEK: {
        return roundingWeek(period);
      }
      default: {
        return period / intervalMillis() * intervalMillis();
      }
    }
  }

  public String desc() {
    return desc;
  }

  public PeriodType less() {
    switch (this) {
      case WEEK:
        return PeriodType.DAY;
      case DAY:
        return PeriodType.HOUR;
      case HOUR:
        return PeriodType.MINUTE;
      case MINUTE:
        return PeriodType.FIVE_SECOND;
      case FIVE_SECOND:
        return PeriodType.SECOND;
      default:
        throw new IllegalArgumentException("No less period type for [" + this.name() + "].");
    }
  }

  public static PeriodType great(PeriodType p1, PeriodType p2) {
    if (compare(p1, p2) > 0) {
      return p1;
    } else {
      return p2;
    }
  }

  public static PeriodType less(PeriodType p1, PeriodType p2) {
    if (compare(p1, p2) > 0) {
      return p2;
    } else {
      return p1;
    }
  }

  public static PeriodType getByInterval(int interval) {
    for (PeriodType periodType : values()) {
      if (periodType.intervalMillis == interval) {
        return periodType;
      }
    }
    return null;
  }

  private static int compare(PeriodType p1, PeriodType p2) {
    if (p1.intervalMillis > p2.intervalMillis) {
      return 1;
    } else if (p1.intervalMillis == p2.intervalMillis) {
      return 0;
    } else {
      return -1;
    }
  }

  public static PeriodType[] supportPeriodTypes =
      new PeriodType[] {PeriodType.SECOND, PeriodType.FIVE_SECOND, PeriodType.MINUTE};
}
