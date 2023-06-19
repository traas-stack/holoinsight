/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade;

import io.holoinsight.server.home.facade.emuns.TimeFilterEnum;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * @author wangsiyuan
 * @date 2022/3/21 7:46 下午
 */
@Data
public class TimeFilter implements Serializable {

  private static final long serialVersionUID = 4167984649363013688L;

  public static final String ALARM_LOCALHOST = "localhost";

  private String from;

  private String to;

  private List<Integer> weeks;
  // @Version(1)
  private List<Integer> months;
  // @Version(1)
  private String model;

  /**
   * 根据timezone来决定配置的时间相对值
   */
  private static String parseWithTzByFmt(long time, String timeZone, String format) {
    // 如果是本地时区，直接搞不用转换了
    if (StringUtils.isEmpty(timeZone) || StringUtils.equals(ALARM_LOCALHOST, timeZone.trim())) {
      return DateUtil.getDate(new Date(time), format);
    }
    DateFormat fmt = new SimpleDateFormat(format);
    fmt.setTimeZone(TimeZone.getTimeZone(timeZone));
    String res = fmt.format(time);
    return res;
  }

  public boolean timeIsInMe(long term, String timeZone) {
    if (TimeFilterEnum.DAY.getDesc().equalsIgnoreCase(this.model)) {
      return timeInMe(term, timeZone);
    } else {
      boolean time = timeInMe(term, timeZone);
      boolean weekin = weekInMe(term, timeZone);
      return time && weekin;
    }
  }

  private boolean weekInMe(long term, String timeZone) {
    if (CollectionUtils.isEmpty(this.getWeeks())) {
      return true;
    }
    Calendar instance = Calendar.getInstance();
    instance.setTimeZone(TimeZone.getTimeZone(timeZone));
    instance.setTimeInMillis(term);
    int d = instance.get(Calendar.DAY_OF_WEEK) - 1;
    return this.getWeeks().contains(d);
  }

  private boolean timeInMe(long term, String timeZone) {
    String start = this.getFrom();
    String end = this.getTo();
    if (StringUtils.isBlank(start)) {
      start = "00:00";
    }

    if (StringUtils.isBlank(end)) {
      end = "24:00";
    }

    /**
     * 防止有格式不规范的时间
     */
    start = formatTime(start);
    end = formatTime(end);

    // 将long根据时区信息映射到HH:mm, 否则用的是机器的本地时区时区
    String termStr = parseWithTzByFmt(term, timeZone, "HH:mm");
    if (start.compareTo(end) > 0) {
      String[][] ranges = new String[2][2];
      String[] range = new String[2];
      range[0] = start;
      range[1] = "24:00";
      ranges[0] = range;
      range = new String[2];
      range[0] = "00:00";
      range[1] = end;
      ranges[1] = range;
      return timeHits(termStr, ranges);
    } else {
      String[] range = new String[2];
      range[0] = start;
      range[1] = end;
      return timeHits(termStr, range);
    }

  }

  private String formatTime(String time) {
    String[] hm = time.split(":|：");
    String h = hm[0].trim();
    if (h.length() == 0) {
      h = "00";
    }
    if (h.length() == 1) {
      h = "0" + h;
    }

    String m = hm[1].trim();
    if (m.length() == 0) {
      m = "00";
    }
    if (m.length() == 1) {
      m = "0" + m;
    }
    return h + ":" + m;
  }

  private final boolean timeHits(String now, String[]... ranges) {
    for (String[] range : ranges) {
      if (now.compareTo(range[0]) >= 0 && now.compareTo(range[1]) <= 0) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return "from:" + (from == null ? "" : from) + ",to:" + (to == null ? "" : to) + ",week:"
        + (weeks == null ? "[]" : weeks.toString());
  }
}
