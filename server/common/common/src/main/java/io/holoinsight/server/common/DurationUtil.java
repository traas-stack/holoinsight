/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common;

import org.apache.commons.lang3.StringUtils;

import java.time.Duration;

/**
 * A tool for converting strings to durations with precision down to seconds
 */
public class DurationUtil {

  /**
   * Examples:
   * 
   * <pre>
   *    "5s"       -- parses as "5 seconds"
   *    "1m"       -- parses as "1 minute" (where a minute is 60 seconds)
   *    "1h1m1s"   -- parses as "1 hour, 1 minute and 1 second"
   *    "2w"       -- parses as "2 weeks"
   *    "1M"       -- parses as "1 month" (the uppercase `M` stands for months and the lowercase `m` stands for minutes)
   *    "1y"       -- parses as "1 year" (year is the maximum conversion precision)
   *    "1"        -- java.lang.IllegalArgumentException: invalid duration: 1
   * </pre>
   */
  public static Duration parse(String input) {
    long secs = 0;
    StringBuilder number = new StringBuilder();
    StringBuilder letters = new StringBuilder();
    for (int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);
      if (Character.isDigit(c)) {
        number.append(c);
      } else if (Character.isLetter(c) && (number.length() > 0)) {
        letters.append(c);
        while (i + 1 < input.length() && Character.isLetter(input.charAt(i + 1))) {
          letters.append(input.charAt(i + 1));
          i++;
        }
        secs += toSecs(Integer.parseInt(number.toString()), letters.toString());
        number = new StringBuilder();
        letters = new StringBuilder();
      }
    }
    if (secs <= 0 || number.length() > 0 || letters.length() > 0) {
      throw new IllegalArgumentException("invalid duration: " + input);
    }
    return Duration.ofSeconds(secs);
  }

  private static long toSecs(int num, String unit) {

    if (StringUtils.equalsIgnoreCase(unit, "y") || StringUtils.equalsIgnoreCase(unit, "year")) {
      num *= 60 * 60 * 24 * 365;
    } else if (StringUtils.equals(unit, "M") || StringUtils.equalsIgnoreCase(unit, "month")) {
      num *= 60 * 60 * 24 * 30;
    } else if (StringUtils.equalsIgnoreCase(unit, "w")
        || StringUtils.equalsIgnoreCase(unit, "week")) {
      num *= 60 * 60 * 24 * 7;
    } else if (StringUtils.equalsIgnoreCase(unit, "d")
        || StringUtils.equalsIgnoreCase(unit, "day")) {
      num *= 60 * 60 * 24;
    } else if (StringUtils.equalsIgnoreCase(unit, "h")
        || StringUtils.equalsIgnoreCase(unit, "hour")) {
      num *= 60 * 60;
    } else if (StringUtils.equals(unit, "m") || StringUtils.equalsIgnoreCase(unit, "min")
        || StringUtils.equalsIgnoreCase(unit, "minute")) {
      num *= 60;
    } else if (StringUtils.equalsIgnoreCase(unit, "s") || StringUtils.equalsIgnoreCase(unit, "sec")
        || StringUtils.equalsIgnoreCase(unit, "second")) {
      num *= 1;
    } else {
      num *= 0;
    }
    return num;
  }
}
