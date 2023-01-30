/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.model.integration.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author zzhb101
 * @version : DesensitizeUtil.java, v 0.1 2022年07月26日 16:17 xiangwanpeng Exp $
 */
public class DesensitizeUtil {
  /**
   * 后面脱敏
   *
   * @param str
   * @param size
   * @return
   */
  public static String left(String str, int size) {
    if (StringUtils.isBlank(str)) {
      return str;
    }
    int length = StringUtils.length(str);
    if (length < size * 2) {
      return StringUtils.leftPad("", size, "*");
    }
    String name = StringUtils.left(str, size);
    return StringUtils.rightPad(name, length, "*");
  }

  /**
   * 中间脱敏
   */
  public static String around(String str, int left, int right) {
    if (StringUtils.isBlank(str)) {
      return str;
    }
    int length = StringUtils.length(str);
    if (length < (left + right) * 2) {
      return StringUtils.leftPad("", left + right, "*");
    }
    return StringUtils.left(str, left) + StringUtils.leftPad("", length - left - right, "*")
        + StringUtils.right(str, right);
  }

  /**
   * 前面脱敏
   */
  public static String right(String str, int size) {
    if (StringUtils.isBlank(str)) {
      return str;
    }
    int length = StringUtils.length(str);
    if (length < size * 2) {
      return StringUtils.rightPad("", size, "*");
    }
    String name = StringUtils.right(str, size);
    return StringUtils.leftPad(name, length, "*");
  }
}
