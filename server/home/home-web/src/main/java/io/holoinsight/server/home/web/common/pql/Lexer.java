/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.common.pql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zzhb101
 * @time 2023-01-02 8:18 下午
 */

@Data
@AllArgsConstructor
@Slf4j
public class Lexer {

  private String token;

  private List<String> prevTokens;

  private List<String> nextTokens;

  private String sOrig;

  private String sTail;

  public void Prev() {
    nextTokens.add(token);
    token = prevTokens.get(prevTokens.size() - 1);
    prevTokens = prevTokens.subList(0, prevTokens.size() - 1);
  }


  public void Next() throws PqlException {
    prevTokens.add(token);
    if (nextTokens.size() > 0) {
      token = nextTokens.get(nextTokens.size() - 1);
      nextTokens = nextTokens.subList(0, nextTokens.size() - 1);
      return;
    }
    String nextToken = next(this);
    token = nextToken;
  }

  public String next(Lexer lexer) throws PqlException {
    String s = lexer.sTail;
    int i = 0;
    while (i < s.length() && isSpaceChar(s.charAt(i))) {
      i++;
    }
    s = s.substring(i, s.length());
    lexer.sTail = s;

    if (StringUtils.isEmpty(s)) {
      return "";
    }

    String token;

    switch (s.charAt(0)) {
      case '#':
        s = s.substring(1, s.length() - 1);
        int n = s.indexOf('\n');
        if (n < 0) {
          return "";
        }
        lexer.sTail = s.substring(n + 1, s.length());
        next(lexer);
      case '{':
      case '}':
      case '[':
      case ']':
      case '(':
      case ')':
      case ',':
      case '@':
        token = String.valueOf(s.charAt(0));
        lexer.sTail = s.substring(token.length(), s.length());
        return token;
    }

    if (isIdentPrefix(s)) {
      token = scanIdent(s);
      lexer.sTail = s.substring(token.length(), s.length());
      return token;
    }

    if (isStringPrefix(s)) {
      token = scanString(s);
      lexer.sTail = s.substring(token.length(), s.length());
      return token;
    }

    int n1 = scanBinaryOpPrefix(s);
    if (n1 > 0) {
      token = s.substring(0, n1);
      lexer.sTail = s.substring(token.length(), s.length());
      return token;
    }

    int n2 = scanTagFilterOpPrefix(s);
    if (n2 > 0) {
      token = s.substring(0, n2);
      lexer.sTail = s.substring(token.length(), s.length());
      return token;
    }

    int n3 = scanDuration(s);
    if (n3 > 0) {
      token = s.substring(0, n3);
      lexer.sTail = s.substring(token.length(), s.length());
      return token;
    }

    if (isPositiveNumberPrefix(s)) {
      token = scanPositiveNumber(s);
      lexer.sTail = s.substring(token.length(), s.length());
      return token;
    }

    throw new PqlException("cannot recognize " + s);
  }

  public Boolean isPositiveNumberPrefix(String s) {
    if (s.length() == 0) {
      return false;
    }
    if (isDecimalChar(s.charAt(0))) {
      return true;
    }

    if (s.charAt(0) != '.' || s.length() < 2) {
      return false;
    }
    return isDecimalChar(s.charAt(1));
  }

  public String scanPositiveNumber(String s) {
    int i = 0;
    ScanSpecialIntegerResult result = scanSpecialIntegerPrefix(s);
    i += result.getSkipChars();
    if (result.getIsHex()) {
      while (i < s.length() && isHexChar(s.charAt(i))) {
        i++;
      }
      return s.substring(0, i);
    }
    while (i < s.length() && isDecimalChar(s.charAt(i))) {
      i++;
    }

    if (i == s.length()) {
      if (i == 0) {
        return "";
      }
      return s;
    }
    int sLen = scanNumMultiplier(s.substring(i, s.length()));
    if (sLen > 0) {
      i += sLen;
      return s.substring(0, i);
    }
    if (s.charAt(i) != '.' && s.charAt(i) != 'e' && s.charAt(i) != 'E') {
      if (i == 0) {
        return "";
      }
      return s.substring(0, i);
    }
    if (s.charAt(i) == '.') {
      i++;
      int j = i;
      while (j < s.length() && isDecimalChar(s.charAt(j))) {
        j++;
      }
      i = j;
      if (i == s.length()) {
        return s;
      }
    }

    sLen = scanNumMultiplier(s.substring(i, s.length()));
    if (sLen > 0) {
      i += sLen;
      return s.substring(0, i);
    }

    if (s.charAt(i) != 'e' && s.charAt(i) != 'E') {
      return s.substring(0, i);
    }
    i++;


    // Scan exponent part.
    if (i == s.length()) {
      return "";
    }
    if (s.charAt(i) == '-' || s.charAt(i) == '+') {
      i++;
    }
    int j = i;
    while (j < s.length() && isDecimalChar(s.charAt(j))) {
      j++;
    }
    if (j == i) {
      return "";
    }

    return s.substring(0, j);
  }

  public int scanNumMultiplier(String s) {
    s = s.toLowerCase();
    if (true == s.startsWith("kib") || true == s.startsWith("mib") || true == s.startsWith("gib")
        || true == s.startsWith("tib")) {
      return 3;
    } else if (true == s.startsWith("ki") || true == s.startsWith("kb")
        || true == s.startsWith("mi") || true == s.startsWith("mb") || true == s.startsWith("gi")
        || true == s.startsWith("gb") || true == s.startsWith("ti") || true == s.startsWith("tb")) {
      return 2;
    } else if (true == s.startsWith("k") || true == s.startsWith("m") || true == s.startsWith("g")
        || true == s.startsWith("t")) {
      return 1;
    }
    return 0;
  }

  public Boolean isHexChar(char ch) {
    return isDecimalChar(ch) || ch >= 'a' && ch <= 'f' || ch >= 'A' && ch <= 'F';
  }

  public ScanSpecialIntegerResult scanSpecialIntegerPrefix(String s) {
    if (s.length() < 1 || s.charAt(0) != '0') {
      return new ScanSpecialIntegerResult(0, false);
    }
    s = s.substring(1, s.length() - 1).toLowerCase();
    if (s.length() == 0) {
      return new ScanSpecialIntegerResult(0, false);
    }
    if (isDecimalChar(s.charAt(0))) {
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
  public class ScanSpecialIntegerResult {
    private int skipChars;
    private Boolean isHex;
  }

  // scanDuration scans duration, which must start with positive num.
  //
  // I.e. 123h, 3h5m or 3.4d-35.66s
  public int scanDuration(String s) {
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

  public int scanSingleDuration(String s, Boolean canBeNegative) {
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

  public int scanTagFilterOpPrefix(String s) {
    if (s.length() >= 2) {
      switch (s.substring(0, 2)) {
        case "=~":
        case "!~":
        case "!=":
          return 2;
      }
    }
    if (s.length() >= 1) {
      if (s.charAt(0) == '=') {
        return 1;
      }
    }
    return -1;
  }

  public static Map<String, Boolean> binaryOps = new HashMap<>();
  static {
    binaryOps.put("+", true);
    binaryOps.put("-", true);
    binaryOps.put("*", true);
    binaryOps.put("/", true);
    binaryOps.put("%", true);
    binaryOps.put("^", true);

    // See https://github.com/prometheus/prometheus/pull/9248
    binaryOps.put("atan2", true);

    // cmp ops
    binaryOps.put("==", true);
    binaryOps.put("!=", true);
    binaryOps.put(">", true);
    binaryOps.put("<", true);
    binaryOps.put(">=", true);
    binaryOps.put("<=", true);

    // logical set ops
    binaryOps.put("and", true);
    binaryOps.put("or", true);
    binaryOps.put("unless", true);

    // New ops for MetricsQL
    binaryOps.put("if", true);
    binaryOps.put("ifnot", true);
    binaryOps.put("default", true);
  }

  public int scanBinaryOpPrefix(String s) {
    int n = 0;
    for (Map.Entry<String, Boolean> entry : binaryOps.entrySet()) {
      String op = entry.getKey();
      if (s.length() < op.length()) {
        continue;
      }
      String ss = s.substring(0, entry.getKey().length()).toLowerCase();
      if (ss.equals(op) && op.length() > n) {
        n = op.length();
      }
    }
    return n;
  }

  public Boolean isStringPrefix(String s) {
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

  public String scanString(String s) {
    if (s.length() < 2) {
      log.error("cannot find end of string in {}", s);
      return "";
    }
    char quote = s.charAt(0);

    for (int i = 1; i < s.length() - 1; i++) {
      int n = s.substring(i, s.length() - 1).indexOf(quote);
      if (n < 0) {
        log.error("cannot find closing quote {} for the string {}", quote, s);
        return "";
      }
      i += n;
      int bs = 0;
      while (bs < i && s.charAt(i - bs - 1) == '\\') {
        bs++;
      }
      if (bs % 2 == 0) {
        token = s.substring(0, i + 1);
        return token;
      }
    }
    return "";
  }

  public Boolean isIdentPrefix(String s) {
    if (StringUtils.isEmpty(s)) {
      return false;
    }
    if (s.charAt(0) == '\\') {
      return true;
    }
    return isFirstIdentChar(s.charAt(0));
  }

  public Boolean isFirstIdentChar(char ch) {
    if (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z') {
      return true;
    }
    return ch == '_' || ch == ':';
  }

  public Boolean isSpaceChar(char ch) {
    switch (ch) {
      // case ' ', '\t', '\n', '\v', '\f', '\r':
      case ' ':
      case '\t':
      case '\n':
      case '\f':
      case '\r':
        return true;
      default:
        return false;
    }
  }

  public String scanIdent(String s) {
    int i = 0;
    while (i < s.length()) {
      if (isIdentChar(s.charAt(i))) {
        i++;
        continue;
      }
      if (s.charAt(i) != '\\') {
        break;
      }
      i++;

      // 判断字符长度
      // i = i + size;
    }
    if (i == 0) {
      throw new IllegalArgumentException("bug");
    }
    return s.substring(0, i);
  }

  public Boolean isIdentChar(char ch) {
    if (isFirstIdentChar(ch)) {
      return true;
    }
    return isDecimalChar(ch) || ch == '.';
  }

  public Boolean isDecimalChar(char ch) {
    return ch >= '0' && ch <= '9';
  }

}
