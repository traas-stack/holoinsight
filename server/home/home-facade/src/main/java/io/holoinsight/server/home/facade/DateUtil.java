/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author wangsiyuan
 * @date 2022/3/21 7:46 下午
 */
public class DateUtil {
  public final static long ONE_DAY_SECONDS = 86400;

  private static ThreadLocal<Map<String, DateFormat>> tsf =
      new ThreadLocal<Map<String, DateFormat>>();

  public static Date addSeconds(Date date1, long secs) {
    return new Date(date1.getTime() + (secs * 1000));
  }

  public static Date addMinutes(Date date1, long minute) {
    return new Date(date1.getTime() + (minute * 1000 * 60));
  }

  public static Date addHours(Date date1, long hour) {
    return new Date(date1.getTime() + (hour * 1000 * 3600));
  }

  public static Date addDays(Date date1, long days) {
    return addSeconds(date1, days * ONE_DAY_SECONDS);
  }

  /**
   * 获取开始时间和结束时间之间的日期区间数组，包括头尾
   *
   * @param start
   * @param end
   * @return
   */
  public static List<Date> getDatesBetween(Date start, Date end) {
    List<Date> ret = new ArrayList<Date>();
    start = setHoursToEmpty(start);
    end = setHoursToEmpty(end);
    while (start.getTime() <= end.getTime()) {
      ret.add(start);
      start = addDays(start, 1);
    }
    return ret;
  }

  /**
   * 获取开始与结束时间之间的秒数
   *
   * @param start
   * @param end
   * @return
   */
  public static int getSecondsBetween(Date start, Date end) {
    int seconds = -1;
    if (start != null && end != null) {
      long startTime = start.getTime();
      long endTime = end.getTime();
      if (startTime <= endTime) {
        return (int) ((endTime - startTime) / 1000);
      }
    }
    return seconds;
  }

  /**
   * 以<strong>分钟</strong>为间隔 获取开始时间和结束时间之间的日期区间数组，包括头尾，
   *
   * @param start
   * @param end
   * @return
   */
  public static List<Date> getMinutesBetween(Date start, Date end) {
    List<Date> ret = new ArrayList<Date>();
    start = setSecondsToEmpty(start);
    end = setSecondsToEmpty(end);
    while (start.getTime() <= end.getTime()) {
      ret.add(start);
      start = addSeconds(start, 60);
    }
    return ret;
  }

  /**
   * 以<strong>秒</strong>为间隔 获取开始时间和结束时间之间的日期区间数组，包括头尾，
   *
   * @param start
   * @param end
   * @return
   */
  public static List<Date> getSecondsListBetween(Date start, Date end) {
    List<Date> ret = new ArrayList<Date>();
    start = setMillSecondsToEmpty(start);
    end = setMillSecondsToEmpty(end);
    while (start.getTime() <= end.getTime()) {
      ret.add(start);
      start = addSeconds(start, 1);
    }
    return ret;
  }

  /**
   * 定位到一天的最后一毫秒
   */
  public static Date getEndOfThisDay(Date date) {
    long time = setHoursToEmpty(date).getTime();
    return new Date(time + 24 * 3600 * 1000 - 1);
  }

  public static long setSecondsToEmpty(long timestamp) {
    return timestamp / 60000 * 60000;
  }

  public static Date setSecondsToEmpty(Date date) {
    return new Date(date.getTime() / (60000) * (60000));
  }

  public static Date setMillSecondsToEmpty(Date date) {
    return new Date(date.getTime() / (1000) * (1000));
  }

  public static Date setSecondsToEnd(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);

    calendar.set(Calendar.SECOND, 59);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }

  public static Date setMinutesToEmpty(Date date) {
    return new Date(date.getTime() / (3600000) * (3600000));
  }

  // 这个方法实际上是得到一天的开始时间
  public static Date setHoursToEmpty(Date date) {
    Calendar calendar = Calendar.getInstance();
    int timeTypes[] =
        {Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND};
    calendar.setTime(date);
    for (int i = 0; i < timeTypes.length; i++) {
      calendar.set(timeTypes[i], 0);
    }
    return calendar.getTime();
  }

  /**
   * 计算start到end之间的天 包括start和end
   *
   * @param start
   * @param end
   * @return
   */
  public static List<Date> getDays(Date start, Date end) {
    List<Date> dates = new ArrayList<Date>();
    Calendar calendar = Calendar.getInstance();
    int timeTypes[] =
        {Calendar.MILLISECOND, Calendar.SECOND, Calendar.MINUTE, Calendar.HOUR_OF_DAY};
    calendar.setTime(start);
    for (int i = 0; i < timeTypes.length; i++) {
      calendar.set(timeTypes[i], 0);
    }
    start = calendar.getTime();
    while (start.getTime() <= end.getTime()) {
      dates.add(start);
      start = DateUtil.addDays(start, 1);
    }
    return dates;
  }

  /**
   * 计算start到end之间的小时 包括start和end
   *
   * @param start
   * @param end
   * @return
   */
  public static List<Date> getHours(Date start, Date end) {
    List<Date> dates = new ArrayList<Date>();
    start = setMinutesToEmpty(start);
    while (start.getTime() <= end.getTime()) {
      dates.add(start);
      start = addHours(start, 1);
    }
    return dates;
  }

  /**
   * 计算start到end之间分钟，包括端点
   *
   * @param start
   * @param end
   * @return
   */
  public static List<Date> getMinutes(Date start, Date end) {
    List<Date> dates = new ArrayList<Date>();
    start = setSecondsToEmpty(start);
    while (start.getTime() <= end.getTime()) {
      dates.add(start);
      start = DateUtil.addMinutes(start, 1);
    }
    return dates;
  }

  /**
   * 获取本线程下对应的format zone使用默认配置
   */
  public static DateFormat getFormatFromThreadLocal(String format) {
    return getFormatFromThreadLocal(format, null);
  }

  /**
   * 获取本线程下对应的format 使用指定的timeZone，如中国地区的"GMT+8:00"
   */
  public static DateFormat getFormatFromThreadLocal(String format, String zoneId) {
    Map<String, DateFormat> lf = tsf.get();
    if (lf == null) {
      lf = new HashMap<String, DateFormat>();
      tsf.set(lf);
    }
    DateFormat df = lf.get(format + zoneId);
    if (df == null) {
      df = new SimpleDateFormat(format, Locale.US);
      if (zoneId != null) {
        df.setTimeZone(TimeZone.getTimeZone(zoneId));
      }
      lf.put(format + zoneId, df);
    }
    return df;
  }

  public static String getDate(Date date, String format) {
    DateFormat df = getFormatFromThreadLocal(format);
    return df.format(date);
  }

  /**
   * 对传入的日期进行格式化
   *
   * @param date
   * @return
   */
  public static String getDateOf_YYMMDD(Date date) {
    return getDate(date, "yyyy-MM-dd");
  }

  public static String getDateOf_HH(Date date) {
    return getDate(date, "HH");
  }

  public static String getDateOf_MMDD_HHMM(Date date) {
    return getDate(date, "MM-dd HH:mm");
  }

  public static String getDateOf_MMDD(Date date) {
    return getDate(date, "MM-dd");
  }

  public static String getDateOf_YYMMDD_HHMMSS(Date date) {
    return getDate(date, "yyyy-MM-dd HH:mm:ss");
  }

  public static String getDateOf_YYMMDD_Plus_HHMMSS(Date date) {
    return getDate(date, "yyyy-MM-dd+HH:mm:ss");
  }

  public static String getDateOf_HHMM(Date date) {
    return getDate(date, "HH:mm");
  }

  public static String getDateOf_HHMMSS(Date date) {
    return getDate(date, "HH:mm:ss");
  }

  public static String getDateOf_MMSS(Date date) {
    return getDate(date, "mm:ss");
  }

  public static String getDateOf_YYMMDD_HHMM(Date date) {
    return getDate(date, "yyyy-MM-dd HH:mm");
  }

  public static String getDateOf_YYYYMMDDHHMM(Date date) {
    return getDate(date, "yyyyMMddHHmm");
  }

  /**
   * 返回"2011-11-11 11:11:11,123"
   *
   * @param date
   * @return
   */
  public static String getDateOf_YYMMDD_HHMMSSMS(Date date) {
    return getDate(date, "yyyy-MM-dd HH:mm:ss,SSS");
  }

  /**
   * 获得当前的分钟数
   *
   * @param date
   * @return
   */
  public static int getMinute(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar.get(Calendar.MINUTE);
  }

  public static int getHour(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar.get(Calendar.HOUR_OF_DAY);
  }

  public static int getDayOfMonth(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar.get(Calendar.DAY_OF_MONTH);
  }

  public static int getDayOfWeek(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar.get(Calendar.DAY_OF_WEEK);
  }

  public static int getMonth(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar.get(Calendar.MONTH);
  }

  public static int getYear(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar.get(Calendar.YEAR);
  }

  public static Date getYesterday() {
    return getDateByGivenHour(new Date(), -24);
    // return addSeconds(new Date(), -1);
  }

  /**
   * 返回从给定日期相差小时的date
   *
   * @param givenDate 给定的日期
   * @param numOfHour 与给定日期相差的小时数，可以是负数
   * @return 日期
   */
  public static Date getDateByGivenHour(Date givenDate, int numOfHour) {
    long givenDateStamp = givenDate.getTime();
    long resultDateStamp = givenDateStamp + numOfHour * 3600000;
    return new Date(resultDateStamp);
  }

  public static Date setMinute(Date date, int minute) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.MINUTE, minute);
    return calendar.getTime();
  }

  /**
   * 获得当前的分钟数
   *
   * @param date
   * @return
   */
  public static int getSecond(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar.get(Calendar.SECOND);
  }

  public static Date parseDate(String date, String format) throws ParseException {
    DateFormat df = getFormatFromThreadLocal(format);
    return df.parse(date);
  }

  public static Date parseDate(String date, String format, String zoneId) throws ParseException {
    DateFormat df = getFormatFromThreadLocal(format, zoneId);
    return df.parse(date);
  }

  /**
   * 对传入的格式化字符串反解出日期
   *
   * @param date "2011-11-11"
   * @return
   * @throws ParseException
   */
  public static Date parseDateOf_YYMMDD(String date) throws ParseException {
    return parseDate(date, "yyyy-MM-dd");
  }

  /**
   * 对传入的格式化字符串反解出日期
   *
   * @param date "2011-11-11 11"
   * @return
   * @throws ParseException
   */
  public static Date parseDateOf_YYMMDD_HH(String date) throws ParseException {
    return parseDate(date, "yyyy-MM-dd HH");
  }

  /**
   * 对传入的格式化字符串反解出日期
   *
   * @param date "2011-11-11 11:11"
   * @return
   */
  public static Date parseDateOf_YYMMDD_HHMM(String date) throws ParseException {
    return parseDate(date, "yyyy-MM-dd HH:mm");
  }

  /**
   * 对传入的格式化字符串反解出日期
   *
   * @param date "2011-11-11 11:11:11"
   * @return
   */
  public static Date parseDateOf_YYMMDD_HHMMSS(String date) throws ParseException {
    return parseDate(date, "yyyy-MM-dd HH:mm:ss");
  }

  /**
   * 返回long型时间的字符串
   *
   * @param date
   * @return
   */
  public static String getDateTime(Date date) {
    return date.getTime() + "";
  }

  /**
   * 对传入的日期字符串翻译为时间
   *
   * @param date
   * @return
   * @throws ParseException
   * @throws ParseException
   */
  public static Date parseDateOf_YYMMDD_HHMMORTime(String date) throws ParseException {
    if (date.indexOf("-") < 0) {
      // 认为是time数字
      long time = Long.parseLong(date);
      return new Date(time);
    }
    return parseDateOf_YYMMDD_HHMM(date);
  }

  /**
   * 获得当前是今天的第几分钟
   *
   * @param date
   * @return
   */
  public static int getMinutesPresentation(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
  }
}
