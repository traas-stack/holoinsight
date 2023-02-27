/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.common;

import io.holoinsight.server.home.facade.emuns.PeriodType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/3/1 2:32 下午
 */
public class PeriodUtil implements Serializable {

  public static List<Range> toRanges(PeriodType periodType, List<Long> periods) {
    if (periods.size() == 0) {
      return Collections.emptyList();
    }
    // 排序
    List<Range> list = new ArrayList<>();
    Collections.sort(periods);

    Range item = new Range(periods.get(0));
    list.add(item);

    long last = item.start;
    for (int i = 1; i < periods.size(); i++) {
      long period = periods.get(i);
      if (last + periodType.intervalMillis() == period) {
        item.end = period;
      } else {
        item = new Range(period);
        list.add(item);
      }
      last = period;
    }
    return list;
  }

  public static List<Range> toRanges(PeriodType periodType, List<Long> periods, int tagsSize) {
    if (periods.size() == 0) {
      return Collections.emptyList();
    }
    if (periods.size() * tagsSize < 86400) {
      return toRanges(periodType, periods);
    }
    // 避免pontus查询超限的限制
    int maxCount = 86400 / tagsSize;
    // 排序
    Collections.sort(periods);
    List<Range> list = new ArrayList<>();
    Range item = new Range(periods.get(0));
    list.add(item);

    long last = item.start;
    int count = 0;
    for (int i = 1; i < periods.size(); i++) {
      long period = periods.get(i);
      if (last + periodType.intervalMillis() == period) {
        count++;
        if (count >= maxCount) {
          count = 0;
          item = new Range(period);
          list.add(item);
        } else {
          item.end = period;
        }
      } else {
        item = new Range(period);
        list.add(item);
      }
      last = period;
    }
    return list;
  }

  public static long toPeriods(PeriodType periodType, long start, long end) {
    if (start >= end) {
      return 1;
    }
    return (end - start) / periodType.intervalMillis() + 1;
  }

  @Getter
  @Setter
  @EqualsAndHashCode
  public static class Range implements Serializable {
    private long start;
    private long end;

    public Range(long start) {
      this.start = start;
      this.end = start;
    }

    public Range(long start, long end) {
      this.start = start;
      this.end = end;
    }

    @Override
    public String toString() {
      final StringBuilder sb = new StringBuilder("range {");
      sb.append("start=").append(start);
      sb.append(", end=").append(end);
      sb.append('}');
      return sb.toString();
    }
  }

}
