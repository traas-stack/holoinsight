/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.common.pql;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zzhb101
 * @time 2023-01-03 5:06 下午
 */
public class ParseStringUtil {

  public static Map<String, Boolean> aggrFuncs = new HashMap<>();

  static {
    aggrFuncs.put("any", true);
    aggrFuncs.put("avg", true);
    aggrFuncs.put("bottomk", true);
    aggrFuncs.put("bottomk_avg", true);
    aggrFuncs.put("bottomk_max", true);
    aggrFuncs.put("bottomk_median", true);
    aggrFuncs.put("bottomk_last", true);
    aggrFuncs.put("bottomk_min", true);
    aggrFuncs.put("count", true);
    aggrFuncs.put("count_values", true);
    aggrFuncs.put("distinct", true);
    aggrFuncs.put("geomean", true);
    aggrFuncs.put("group", true);
    aggrFuncs.put("histogram", true);
    aggrFuncs.put("limitk", true);
    aggrFuncs.put("mad", true);
    aggrFuncs.put("max", true);
    aggrFuncs.put("median", true);
    aggrFuncs.put("min", true);
    aggrFuncs.put("mode", true);
    aggrFuncs.put("mode", true);
    aggrFuncs.put("outliers_mad", true);
    aggrFuncs.put("outliersk", true);
    aggrFuncs.put("quantile", true);
    aggrFuncs.put("quantiles", true);
    aggrFuncs.put("stddev", true);
    aggrFuncs.put("stdvar", true);
    aggrFuncs.put("sum", true);
    aggrFuncs.put("sum2", true);
    aggrFuncs.put("topk", true);
    aggrFuncs.put("topk_avg", true);
    aggrFuncs.put("topk_max", true);
    aggrFuncs.put("topk_median", true);
    aggrFuncs.put("topk_last", true);
    aggrFuncs.put("topk_min", true);
    aggrFuncs.put("zscore", true);
  }


  public static Map<String, Boolean> binaryOps = new HashMap<>();
  static {
    binaryOps.put("+", true);
    binaryOps.put("-", true);
    binaryOps.put("*", true);
    binaryOps.put("/", true);
    binaryOps.put("%", true);
    binaryOps.put("^", true);

    binaryOps.put("atan2", true);

    binaryOps.put("==", true);
    binaryOps.put("!=", true);
    binaryOps.put(">", true);
    binaryOps.put("<", true);
    binaryOps.put(">=", true);
    binaryOps.put("<=", true);

    binaryOps.put("and", true);
    binaryOps.put("or", true);
    binaryOps.put("unless", true);

    binaryOps.put("if", true);
    binaryOps.put("ifnot", true);
    binaryOps.put("default", true);
  }

  public static Map<String, Integer> binaryOpPriorities = new HashMap<>();
  static {
    binaryOpPriorities.put("default", -1);
    binaryOpPriorities.put("if", 0);
    binaryOpPriorities.put("ifnot", 0);
    binaryOpPriorities.put("or", 1);
    binaryOpPriorities.put("and", 2);
    binaryOpPriorities.put("unless", 2);
    binaryOpPriorities.put("==", 3);
    binaryOpPriorities.put("!=", 3);
    binaryOpPriorities.put("<", 3);
    binaryOpPriorities.put(">", 3);
    binaryOpPriorities.put("<=", 3);
    binaryOpPriorities.put(">=", 3);
    binaryOpPriorities.put("+", 4);
    binaryOpPriorities.put("-", 4);
    binaryOpPriorities.put("*", 5);
    binaryOpPriorities.put("/", 5);
    binaryOpPriorities.put("%", 5);
    binaryOpPriorities.put("atan2", 5);
    binaryOpPriorities.put("^", 6);
  }

  public static Boolean isIdentPrefix(String s) {
    if (StringUtils.isEmpty(s)) {
      return false;
    }
    if (s.charAt(0) == '\\') {
      return true;
    }
    return isFirstIdentChar(s.charAt(0));
  }

  public static Boolean isFirstIdentChar(char ch) {
    if (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z') {
      return true;
    }
    return ch == '_' || ch == ':';
  }

  public static String unescapeIdent(String s) {
    int n = s.indexOf('\\');
    if (n < 0) {
      return s;
    }
    String dst;

    while (true) {
      dst = s.substring(0, n);
      s = s.substring(n + 1, s.length() - 1);
      if (s.length() == 0) {
        return dst;
      }
      if (s.charAt(0) == 'x' && s.length() >= 3) {
        int h1 = fromHex(s.charAt(1));
        int h2 = fromHex(s.charAt(2));
        if (h1 >= 0 && h2 >= 0) {
          dst = dst + ((h1 << 4) | h2);
          s = s.substring(3, s.length() - 1);
        } else {
          dst = dst + s.charAt(0);
          s = s.substring(1, s.length() - 1);
        }
      } else {
        dst = dst + s.charAt(0);
        s = s.substring(1, s.length() - 1);
      }
      n = s.indexOf("\\");
      if (n < 0) {
        dst = dst + s;
        return dst;
      }
    }
  }

  public static int fromHex(char ch) {
    if (ch >= '0' && ch <= '9') {
      return ch - '0';
    }
    if (ch >= 'a' && ch <= 'f') {
      return (ch - 'a') + 10;
    }
    if (ch >= 'A' && ch <= 'F') {
      return (ch - 'A') + 10;
    }
    return -1;
  }

  public static Boolean isPositiveDuration(String s) {
    int n = scanDuration(s);
    return n == s.length();
  }

  /**
   * Scan duration I.e. 123h, 3h5m or 3.4d-35.66s
   * 
   * @param s
   * @return
   */
  public static int scanDuration(String s) {
    int n = scanSingleDuration(s, false);
    if (n <= 0) {
      return -1;
    }
    s = s.substring(n, s.length());
    int i = n;
    while (true) {
      n = scanSingleDuration(s, true);
      if (n <= 0) {
        return i;
      }
      s = s.substring(n, s.length());
      i += n;
    }
  }

  public static int scanSingleDuration(String s, Boolean canBeNegative) {
    if (s.length() == 0) {
      return -1;
    }
    int i = 0;
    if (s.charAt(0) == '-' && canBeNegative) {
      i++;
    }
    while (i < s.length() && isDecimalChar(s.charAt(i))) {
      i++;
    }
    if (i == 0 || i == s.length()) {
      return -1;
    }
    if (s.charAt(i) == '.') {
      int j = i;
      i++;
      while (i < s.length() && isDecimalChar(s.charAt(i))) {
        i++;
      }
      if (i == j || i == s.length()) {
        return -1;
      }
    }
    switch (s.charAt(i)) {
      case 'm':
        if (i + 1 < s.length() && s.charAt(i + 1) == 's') {
          return i + 2;
        }
        return i + 1;
      case 's':
      case 'h':
      case 'd':
      case 'w':
      case 'y':
      case 'i':
        return i + 1;
      default:
        return -1;
    }
  }

  public static Boolean isStringPrefix(String s) {
    if (StringUtils.isEmpty(s)) {
      return false;
    }
    switch (s.charAt(0)) {
      case '"':
      case '\'':
      case '`':
        return true;
      default:
        return false;
    }
  }

  public static Boolean isPositiveNumberPrefix(String s) {
    if (s.length() == 0) {
      return false;
    }
    if (isDecimalChar(s.charAt(0))) {
      return true;
    }

    // Check for .234 numbers
    if (s.charAt(0) != '.' || s.length() < 2) {
      return false;
    }
    return isDecimalChar(s.charAt(1));
  }

  public static Boolean isInfOrNaN(String s) {
    if (s.length() != 3) {
      return false;
    }
    s = s.toLowerCase();
    return s.equals("inf") || s.equals("nan");
  }

  public static Boolean isDecimalChar(char ch) {
    return ch >= '0' && ch <= '9';
  }

  public static Boolean isSpecialIntegerPrefix(String s) {
    ScanSpecialIntegerResult result = scanSpecialIntegerPrefix(s);
    return result.getSkipChars() > 0;
  }

  public static ScanSpecialIntegerResult scanSpecialIntegerPrefix(String s) {
    if (s.length() < 1 || s.charAt(0) != '0') {
      return new ScanSpecialIntegerResult(0, false);
    }
    s = s.substring(1, s.length() - 1).toLowerCase();
    if (s.length() == 0) {
      return new ScanSpecialIntegerResult(0, false);
    }
    if (ParseStringUtil.isDecimalChar(s.charAt(0))) {
      return new ScanSpecialIntegerResult(1, false);
    }
    if (s.charAt(0) == 'x') {
      return new ScanSpecialIntegerResult(2, true);
    }
    if (s.charAt(0) == 'o' || s.charAt(0) == 'b') {
      return new ScanSpecialIntegerResult(2, false);
    }
    return new ScanSpecialIntegerResult(0, false);
  }


  @Data
  @AllArgsConstructor
  public static class ScanSpecialIntegerResult {
    private int skipChars;
    private Boolean isHex;
  }

  public static Boolean isEOF(String s) {
    return s.length() == 0;
  }

  public static Boolean isOffset(String s) {
    s = s.toLowerCase();
    return s.equals("offset");
  }

  public static Boolean isKeepMetricNames(String s) {
    return s.toLowerCase().equals("keep_metric_names");
  }

  public static Boolean isAggrFunc(String s) {
    return aggrFuncs.get(s.toLowerCase()) != null;
  }

  public static Boolean isBinaryOp(String op) {
    return binaryOps.get(op.toLowerCase()) != null;
  }

  public static Boolean isAggrFuncModifier(String s) {
    switch (s.toLowerCase()) {
      case "by":
      case "without":
        return true;
      default:
        return false;
    }
  }

  public static Boolean isBinaryOpJoinModifier(String s) {
    switch (s.toLowerCase()) {
      case "group_left":
      case "group_right":
        return true;
      default:
        return false;
    }
  }

  public static Boolean isBinaryOpBoolModifier(String s) {
    return s.toLowerCase().equals("bool");
  }

  public static Boolean IsBinaryOpCmp(String op) {
    switch (op) {
      case "==":
      case "!=":
      case ">":
      case "<":
      case ">=":
      case "<=":
        return true;
      default:
        return false;
    }
  }

  public static Boolean isBinaryOpGroupModifier(String s) {
    switch (s.toLowerCase()) {
      case "on":
      case "ignoring":
        return true;
      default:
        return false;
    }
  }

  public static Boolean isBinaryOpLogicalSet(String op) {
    switch (op.toLowerCase()) {
      case "and":
      case "or":
      case "unless":
        return true;
      default:
        return false;
    }
  }

  public static int binaryOpPriority(String op) {
    return binaryOpPriorities.get(op.toLowerCase());
  }

  public static Boolean isRightAssociativeBinaryOp(String op) {
    return op.toLowerCase().equals("^");
  }

  public static Boolean isIdentChar(char ch) {
    if (isFirstIdentChar(ch)) {
      return true;
    }
    return isDecimalChar(ch) || ch == '.';
  }

}
