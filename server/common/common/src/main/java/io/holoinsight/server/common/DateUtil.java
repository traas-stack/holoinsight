/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * <p>
 * DateUtil class.
 * </p>
 *
 * @author xzchaoo
 * @version 1.0: DateUtil.java, v 0.1 2022年03月28日 6:03 下午 jinsong.yjs Exp $
 */
public class DateUtil {
  /** Constant <code>ONE_DAY_SECONDS=86400</code> */
  public final static long ONE_DAY_SECONDS = 86400;

  /** Constant <code>SECOND=1000L</code> */
  public static final long SECOND = 1000L;

  /** Constant <code>MINUTE=1000 * 60L</code> */
  public static final long MINUTE = 1000 * 60L;

  /** Constant <code>HOUR=1000 * 60 * 60L</code> */
  public static final long HOUR = 1000 * 60 * 60L;

  /** Constant <code>DAY=1000 * 60 * 60 * 24L</code> */
  public static final long DAY = 1000 * 60 * 60 * 24L;

  private static ThreadLocal<Map<String, DateFormat>> tsf =
      new ThreadLocal<Map<String, DateFormat>>();

  /**
   * <p>
   * addSeconds.
   * </p>
   */
  public static Date addSeconds(Date date1, long secs) {
    return new Date(date1.getTime() + (secs * 1000));
  }

  /**
   * <p>
   * addMinutes.
   * </p>
   */
  public static Date addMinutes(Date date1, long minute) {
    return new Date(date1.getTime() + (minute * 1000 * 60));
  }

  /**
   * <p>
   * addHours.
   * </p>
   */
  public static Date addHours(Date date1, long hour) {
    return new Date(date1.getTime() + (hour * 1000 * 3600));
  }

  /**
   * <p>
   * addDays.
   * </p>
   */
  public static Date addDays(Date date1, long days) {
    return addSeconds(date1, days * ONE_DAY_SECONDS);
  }

  /**
   * 对传入的日期进行格式化
   *
   * @param date
   */
  public static String getDateOf_YYMMDD(Date date) {
    return getDate(date, "yyyy-MM-dd");
  }

  /**
   * <p>
   * getDateOf_MMDD_HHMM.
   * </p>
   */
  public static String getDateOf_MMDD_HHMM(Date date) {
    return getDate(date, "MM-dd HH:mm");
  }

  /**
   * <p>
   * getDateOf_MMDD_HHMMSS.
   * </p>
   */
  public static String getDateOf_MMDD_HHMMSS(Date date) {
    return getDate(date, "MM-dd HH:mm:ss");
  }

  /**
   * <p>
   * getDateOf_MMDD.
   * </p>
   */
  public static String getDateOf_MMDD(Date date) {
    return getDate(date, "MM-dd");
  }

  /**
   * <p>
   * getDateOf_YYMMDD_HHMMSS.
   * </p>
   */
  public static String getDateOf_YYMMDD_HHMMSS(Date date) {
    return getDate(date, "yyyy-MM-dd HH:mm:ss");
  }

  /**
   * <p>
   * getDateOf_HHMM.
   * </p>
   */
  public static String getDateOf_HHMM(Date date) {
    return getDate(date, "HH:mm");
  }

  /**
   * <p>
   * getDateOf_HHMMSS.
   * </p>
   */
  public static String getDateOf_HHMMSS(Date date) {
    return getDate(date, "HH:mm:ss");
  }

  /**
   * <p>
   * getDateOf_YYMMDD_HHMM.
   * </p>
   */
  public static String getDateOf_YYMMDD_HHMM(Date date) {
    return getDate(date, "yyyy-MM-dd HH:mm");
  }

  /**
   * <p>
   * getDateOf_YYYYMMDDHHMM.
   * </p>
   */
  public static String getDateOf_YYYYMMDDHHMM(Date date) {
    return getDate(date, "yyyyMMddHHmm");
  }

  /**
   * 返回"2011-11-11 11:11:11,123"
   *
   * @param date
   */
  public static String getDateOf_YYMMDD_HHMMSSMS(Date date) {
    return getDate(date, "yyyy-MM-dd HH:mm:ss,SSS");
  }

  /**
   * <p>
   * getDateOptionalTime.
   * </p>
   */
  public static String getDateOptionalTime(Date date) {
    return getDate(date, "yyyy-MM-dd'T'HH:mm:ss,SSSZ");
  }

  /**
   * <p>
   * getDateOf_YYMMDD_HHMMSS_DOT_MS.
   * </p>
   */
  public static String getDateOf_YYMMDD_HHMMSS_DOT_MS(Date date) {
    return getDate(date, "yyyy-MM-dd HH:mm:ss.SSS");
  }

  /**
   * 对传入的格式化字符串反解出日期
   *
   * @param date "2011-11-11"
   * @throws ParseException
   */
  public static Date parseDateOf_YYMMDD(String date) throws ParseException {
    return parseDate(date, "yyyy-MM-dd");
  }

  /**
   * 对传入的格式化字符串反解出日期
   *
   * @param date "2011-11-11 11"
   * @throws ParseException
   */
  public static Date parseDateOf_YYMMDD_HH(String date) throws ParseException {
    return parseDate(date, "yyyy-MM-dd HH");
  }

  /**
   * 对传入的格式化字符串反解出日期
   *
   * @param date "2011-11-11 11:11"
   */
  public static Date parseDateOf_YYMMDD_HHMM(String date) throws ParseException {
    return parseDate(date, "yyyy-MM-dd HH:mm");
  }

  /**
   * 对传入的格式化字符串反解出日期
   *
   * @param date "2011-11-11 11:11:11"
   */
  public static Date parseDateOf_YYMMDD_HHMMSS(String date) throws ParseException {
    return parseDate(date, "yyyy-MM-dd HH:mm:ss");
  }

  /**
   * <p>
   * parseDateOf_YYMMDD_HHMMSSDOTSSS.
   * </p>
   */
  public static Date parseDateOf_YYMMDD_HHMMSSDOTSSS(String date) throws ParseException {
    return parseDate(date, "yyyy-MM-dd HH:mm:ss.SSS");
  }

  /**
   * <p>
   * getDate.
   * </p>
   */
  public static String getDate(Date date, String format) {
    DateFormat df = getFormatFromThreadLocal(format);
    return df.format(date);
  }

  /**
   * <p>
   * parseDate.
   * </p>
   */
  public static Date parseDate(String date, String format) throws ParseException {
    DateFormat df = getFormatFromThreadLocal(format);
    return df.parse(date);
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


  public static Date getFirstDayOfMonth() {
    // 获取当前时间的Calendar实例
    Calendar calendar = Calendar.getInstance();

    // 将时间设置为本月的第一天
    calendar.set(Calendar.DAY_OF_MONTH, 1);

    // 将时分秒和毫秒清零
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    // 获取本月第一天的时间戳（以毫秒为单位）
    long firstDayOfMonthTimestamp = calendar.getTimeInMillis();

    return new Date(firstDayOfMonthTimestamp);
  }
}
