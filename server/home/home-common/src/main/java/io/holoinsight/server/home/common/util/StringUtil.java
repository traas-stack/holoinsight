/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util;


/**
 * 有关字符串处理的工具类。
 */
public class StringUtil {

  public static boolean isBlank(String str) {
    int len;

    if ((str == null) || ((len = str.length()) == 0)) {
      return true;
    }

    for (int i = 0; i < len; i++) {
      if (!Character.isWhitespace(str.charAt(i))) {
        return false;
      }
    }

    return true;
  }

  public static boolean isNotBlank(String str) {
    int len;

    if ((str == null) || ((len = str.length()) == 0)) {
      return false;
    }

    for (int i = 0; i < len; i++) {
      if (!Character.isWhitespace(str.charAt(i))) {
        return true;
      }
    }

    return false;
  }


}
