/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;

@Slf4j
public class Fmt {

  public static final String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
  public static final String yyyy_MM_dd_HH_mm = "yyyy-MM-dd HH:mm";
  public static final String yyyy_MM_dd = "yyyy-MM-dd";
  public static final String yyyyMMdd = "yyyyMMdd";
  public static final String HH_mm = "HH:mm";
  public static final String yyyyMMddHHmmss = "yyyyMMddHHmmss";


  public static String toFixed(double value, int digits) {
    BigDecimal bg = new BigDecimal(value);
    String plainText = bg.toString();
    if (plainText.indexOf('.') > 0) {
      bg = bg.setScale(digits, BigDecimal.ROUND_HALF_UP);
      return bg.toString();
    }
    return plainText;
  }


  public static String toPercent(double value, int digits) {
    NumberFormat nt = NumberFormat.getPercentInstance();
    nt.setMinimumFractionDigits(digits);
    return nt.format(value);
  }

  public static String toNumberString(double value, int digits) {
    NumberFormat nt = NumberFormat.getInstance();
    nt.setGroupingUsed(false);
    nt.setMaximumFractionDigits(digits);
    return nt.format(value);
  }

  public static String toPercent(double value) {
    return toPercent(value, 0);
  }

  public static Date string2Date(String str) {
    return string2Date(str, yyyy_MM_dd_HH_mm_ss);
  }

  public static Date string2Date(String str, String... patterns) {
    try {
      return DateUtils.parseDate(str, patterns);
    } catch (ParseException e) {
      log.error("parseDate fail", e);
    }
    return null;
  }

  public static String date2String(Date date) {
    return date2String(date, Fmt.yyyy_MM_dd_HH_mm_ss);
  }

  public static String date2String(Date date, String pattern) {
    if (date == null) {
      return null;
    }
    FastDateFormat dateFormat = FastDateFormat.getInstance(pattern);
    return dateFormat.format(date);
  }

  public static void main(String[] args) {
    // System.out.println(Fmt.toFixed(0.562, 2));
    // System.out.println(Fmt.toPercent(0.562, 0));
  }

}
