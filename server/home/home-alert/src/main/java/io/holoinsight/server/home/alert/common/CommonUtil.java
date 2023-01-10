/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.common;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CommonUtil {

  /**
   * 补全两位数的字符串
   *
   * @param num
   * @return
   */
  public static String completeNumber2(int num) {
    String ret = String.valueOf(num);
    if (num < 10) {
      ret = "0" + ret;
    }
    return ret;
  }

  /**
   * 拿到变化的百分比
   *
   * @param from
   * @param to
   * @return
   */
  public static double getChangePercent(double from, double to) {
    if (to == 0 && from == 0) {
      return 0;
    }
    if (to == 0 && from != 0) {
      return (float) (from) / 1;
    }
    return (from - to) / to;
  }

  /**
   * 拿到直接占用的百分比
   *
   * @param from
   * @param to
   * @return
   */
  public static double getDirectPercent(double from, double to) {
    if (to == 0 && from == 0) {
      return 0;
    }
    if (to == 0 && from != 0) {
      return (from) / 1;
    }
    return from / to;
  }

  /**
   * 保留两位小数
   *
   * @param src
   * @return
   */
  public static double reserve2Position(double src) {
    return src * 100 > Integer.MAX_VALUE ? src : ((double) (int) (src * 100 + 0.005d)) / 100;
  }

  public static String convertToPercentageStr(double src) {
    return NumberFormat.getPercentInstance().format(src);
  }

  /**
   * double转化为字符串的优雅展示 数值很大的时候不显示小数部分 数值很小(<100)且小数部分确实存在的时候，显示两位小数
   */
  public static String doubleToString(double src) {
    String ret = null;
    if (src > Integer.MAX_VALUE) {
      DecimalFormat format = new DecimalFormat("###");
      format.setMinimumFractionDigits(0);
      ret = format.format(src);
    } else if (src < 100 && src - (int) (src) > 0.009) {
      ret = String.valueOf(((double) (int) (src * 100 + 0.005f)) / 100);
    } else if (Double.isInfinite(src) || Double.isNaN(src)) {
      ret = "-";
    } else {
      ret = String.valueOf((int) (src + 0.005f));
    }
    return ret;
  }

  /**
   * 将数组从头开始收缩到某个长度
   *
   * @param src
   * @param length
   * @return
   */
  public static String[] collapseFromHead(String[] src, int length) {
    if (src == null) {
      return null;
    }
    String[] ret = new String[length];
    for (int k = 0; k < length; k++) {
      ret[k] = src[k];
    }
    return ret;
  }

  /**
   * 将数组从尾开始收缩到某个长度
   *
   * @param src
   * @param length
   * @return
   */
  public static String[] collapseFromTail(String[] src, int length) {
    if (src == null) {
      return null;
    }
    String[] ret = new String[length];
    for (int k = 0; k < length; k++) {
      ret[k] = src[k + (src.length - length)];
    }
    return ret;
  }

  /**
   * 是否存在于数组
   *
   * @param genDeeps
   * @param deep
   * @return
   */
  public static boolean existInArray(int[] genDeeps, int deep) {
    if (genDeeps != null) {
      for (int k : genDeeps) {
        if (k == deep) {
          return true;
        }
      }
    }
    return false;

  }

  /**
   * 是否存在于数组
   *
   * @param srcs
   * @param str
   * @return
   */
  public static boolean existInArray(String[] srcs, String str) {
    if (srcs != null) {
      for (String k : srcs) {
        if (k.equals(str)) {
          return true;
        }
      }
    }
    return false;

  }

  /**
   * 时间转分钟
   *
   * @param str
   * @return
   */
  public static int minuteStrToInt(String str) {
    String[] arr = str.split(":");
    if (arr.length != 2) {
      return -1;
    }
    return Integer.parseInt(arr[0]) * 60 + Integer.parseInt(arr[1]);
  }

  public static boolean strFastMatch(String s1, String s2) {
    if (s1.length() == s2.length()) {
      return s1.equals(s2);
    }
    return false;
  }

  /**
   * 获取十进制下这个整数占了多少位，如1000占4位，999占3位
   *
   * @param integer
   * @return
   */
  public static int getDecimalBits(int integer) {
    int ret = 0;
    while (integer > 0) {
      integer /= 10;
      ret++;
    }
    return ret;
  }

  private static Random seed = new Random();

  /**
   * 获取0到max-1的随机数
   *
   * @param max
   */
  public static int getRandom(int max) {
    return Math.abs(seed.nextInt() % max);
  }

  static Map<String, Character> lineBreakers = new HashMap<String, Character>();

  public static long getCurrentMin() {
    long cur = System.currentTimeMillis();
    cur -= cur % 60000; // 分钟对齐
    return cur;
  }

  public static long getLastMin() {
    long cur = System.currentTimeMillis();
    cur -= cur % 60000; // 分钟对齐
    return cur - 60000;
  }

  public static boolean safe_equals(Object a, Object b) {
    if (a == null && b == null) {
      return true;
    }
    if (a == null || b == null) {
      return false;
    }
    return a.equals(b);
  }
}
