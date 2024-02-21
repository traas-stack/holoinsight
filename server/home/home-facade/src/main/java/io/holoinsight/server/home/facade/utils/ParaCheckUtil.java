/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade.utils;

import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.ResultCodeEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parameter checker
 * 
 * @author jsy1001de
 * @version 1.0: ParaCheckUtil.java, v 0.1 2022-03-15 13:14
 */
public class ParaCheckUtil {

  private static Pattern PATTERN_SQL =
      Pattern.compile("^[\\u00b7A-Za-z0-9\\u4e00-\\u9fa5\\-_ ,\\.]*$");
  private static Pattern PATTERN_STRICT_SQL =
      Pattern.compile("^[\\u00b7A-Za-z0-9\\u4e00-\\u9fa5\\-_, |:\\.]*$");

  private static Pattern uniCodePattern = Pattern.compile("\\\\u[0-9a-f]{4}");

  /**
   * Check whether the boolean result is true.
   */
  public static void checkParaBoolean(Boolean result, String errorMsg) {
    if (!result) {
      throw new MonitorException(ResultCodeEnum.PARAMETER_ILLEGAL, errorMsg);
    }
  }

  public static boolean sqlNameCheck(String param) {
    Matcher commonAllowed = PATTERN_SQL.matcher(param);
    if (commonAllowed.find()) {
      if (!unicodeCheck(param)) {
        return false;
      }
      return true;
    }
    return false;
  }

  public static boolean sqlFieldCheck(String param) {
    Matcher commonAllowed = PATTERN_STRICT_SQL.matcher(param);
    if (commonAllowed.find()) {
      if (!unicodeCheck(param)) {
        return false;
      }
      return true;
    }
    return false;
  }

  private static boolean unicodeCheck(String param) {
    // unicode encoding check
    int start = 0;
    Matcher uniCodeMatcher = uniCodePattern.matcher(param);
    while (uniCodeMatcher.find(start)) {
      start = uniCodeMatcher.end();
      String keyword = uniCodeMatcher.group(0);
      if (StringUtils.isNotBlank(keyword) && keyword.startsWith("\\u")) {
        String hexString = StringUtils.substring(keyword, 2);
        int valueInteger = Integer.parseInt(hexString, 16);
        if (valueInteger < 19968 || valueInteger > 40869) {
          return false;
        }
      }
    }
    return true;
  }

  public static void checkTimeRange(Long start, Long end, String errorMsg) {
    if (start > end) {
      throw new MonitorException(ResultCodeEnum.PARAMETER_ILLEGAL, errorMsg);
    }
  }
}
