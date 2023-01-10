/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 *
 * @author jsy1001de
 * @version 1.0: CommonCookieUtils.java, v 0.1 2022年05月19日 6:05 下午 jinsong.yjs Exp $
 */
public class CommonCookieUtils {
  private static final String PATH = "/";

  /**
   * US locale - all HTTP dates are in english
   */
  public final static Locale LOCALE_US = Locale.US;

  /**
   * Pattern used for old cookies
   */
  public final static String OLD_COOKIE_PATTERN = "EEE, dd-MMM-yyyy HH:mm:ss z";

  //
  // from RFC 2068, token special case characters
  //
  private static final String tspecials = "()<>@,;:\\\"/[]?={} \t";
  private static boolean checkFlag[] = new boolean[127];
  static {
    for (int i = 0; i < tspecials.length(); i++) {
      checkFlag[tspecials.charAt(i)] = true;
    }
  }

  public static String getCookieValue(String key, HttpServletRequest request) {
    Cookie cookie = getCookie(key, request);
    if (cookie == null)
      return null;
    return cookie.getValue();
  }

  public static Cookie getCookie(String key, HttpServletRequest request) {
    if (request == null)
      return null;
    Cookie[] cookies = request.getCookies();
    if (cookies == null)
      return null;
    Cookie value = null;
    for (Cookie c : cookies) {
      if (key.equals(c.getName())) {
        value = c;
        break;
      }
    }
    return value;
  }

  public static void addCookie(String key, String value, HttpServletResponse response) {
    setCookie(key, value, -1, null, null, response);
  }

  public static void addCookie(String key, String value, final boolean httpOnly,
      HttpServletResponse response) {
    setCookie(key, value, -1, null, null, httpOnly, response);
  }

  public static void addCookie(String key, String value, final boolean httpOnly,
      final boolean secure, HttpServletResponse response) {
    setCookie(key, value, -1, null, null, httpOnly, secure, response);
  }

  public static void addCookie(String key, String value, int maxAge, HttpServletResponse response) {
    setCookie(key, value, maxAge, null, null, response);
  }

  public static void addCookie(String key, String value, int maxAge, final boolean httpOnly,
      HttpServletResponse response) {
    setCookie(key, value, maxAge, null, null, httpOnly, response);
  }

  public static void addCookie(String key, String value, int maxAge, final boolean httpOnly,
      final boolean secure, HttpServletResponse response) {
    setCookie(key, value, maxAge, null, null, httpOnly, secure, response);
  }

  public static void addCookie(String key, String value, int maxAge, String path, String domainName,
      HttpServletResponse response) {
    setCookie(key, value, maxAge, path, domainName, response);
  }

  public static void addCookie(String key, String value, int maxAge, String path, String domainName,
      final boolean httpOnly, HttpServletResponse response) {
    setCookie(key, value, maxAge, path, domainName, httpOnly, response);
  }

  public static void addCookie(String key, String value, int maxAge, String path, String domainName,
      final boolean httpOnly, final boolean secure, HttpServletResponse response) {
    setCookie(key, value, maxAge, path, domainName, httpOnly, secure, response);
  }

  public static void removeCookie(String key, HttpServletResponse response) {
    removeCookie(key, null, null, response);
  }

  public static void removeCookie(String key, String path, String domainName,
      HttpServletResponse response) {
    setCookie(key, StringUtils.EMPTY, 0, path, domainName, false, response);
  }

  private static void setCookie(String key, String value, int maxAge, String path,
      String domainName, HttpServletResponse response) {
    setCookie(key, value, maxAge, path, domainName, false, false, response);
  }

  public static void setCookie(String key, String value, int maxAge, String path, String domainName,
      final boolean httpOnly, HttpServletResponse response) {
    setCookie(key, value, maxAge, path, domainName, httpOnly, false, response);
  }

  private static void setCookie(String key, String value, int maxAge, String path,
      String domainName, final boolean httpOnly, final boolean secure,
      HttpServletResponse response) {
    if (response != null) {
      Cookie cookie = new Cookie(key, value);
      cookie.setMaxAge(maxAge);
      if (StringUtils.isNotBlank(path))
        cookie.setPath(path);
      else
        cookie.setPath(PATH);
      if (StringUtils.isNotBlank(domainName))
        cookie.setDomain(domainName);
      cookie.setVersion(0);
      cookie.setSecure(secure);
      if (httpOnly) {
        final StringBuffer buf = new StringBuffer();
        getCookieHeaderValue(cookie, buf, httpOnly);
        response.addHeader(getCookieHeaderName(cookie), buf.toString());
      } else
        response.addCookie(cookie);
    }
  }

  private static String getCookieHeaderName(final Cookie cookie) {
    final int version = cookie.getVersion();
    if (version == 1) {
      return "Set-Cookie2";
    } else {
      return "Set-Cookie";
    }
  }

  private static void getCookieHeaderValue(final Cookie cookie, final StringBuffer buf,
      final boolean httpOnly) {
    final int version = cookie.getVersion();

    // this part is the same for all cookies

    String name = cookie.getName(); // Avoid NPE on malformed cookies
    if (name == null) {
      name = "";
    }
    String value = cookie.getValue();
    if (value == null) {
      value = "";
    }

    buf.append(name);
    buf.append("=");

    maybeQuote(version, buf, value);

    // add version 1 specific information
    if (version == 1) {
      // Version=1 ... required
      buf.append("; Version=1");

      // Comment=comment
      if (cookie.getComment() != null) {
        buf.append("; Comment=");
        maybeQuote(version, buf, cookie.getComment());
      }
    }

    // add domain information, if present

    if (cookie.getDomain() != null) {
      buf.append("; Domain=");
      maybeQuote(version, buf, cookie.getDomain());
    }

    // Max-Age=secs/Discard ... or use old "Expires" format
    if (cookie.getMaxAge() >= 0) {
      if (version == 0) {
        buf.append("; Expires=");
        SimpleDateFormat dateFormat = new SimpleDateFormat(OLD_COOKIE_PATTERN, LOCALE_US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        if (cookie.getMaxAge() == 0) {
          dateFormat.format(new Date(10000), buf, new FieldPosition(0));
        } else {
          dateFormat.format(new Date(System.currentTimeMillis() + cookie.getMaxAge() * 1000L), buf,
              new FieldPosition(0));
        }
      } else {
        buf.append("; Max-Age=");
        buf.append(cookie.getMaxAge());
      }
    } else if (version == 1) {
      buf.append("; Discard");
    }

    // Path=path
    if (cookie.getPath() != null) {
      buf.append("; Path=");
      maybeQuote(version, buf, cookie.getPath());
    }

    // Secure
    if (cookie.getSecure()) {
      buf.append("; Secure");
    }

    // HttpOnly
    if (httpOnly) {
      buf.append("; HttpOnly");
    }
  }

  private static void maybeQuote(final int version, final StringBuffer buf, final String value) {
    if (version == 0 || isToken(value)) {
      buf.append(value);
    } else {
      buf.append('"');
      buf.append(value);
      buf.append('"');
    }
  }

  /*
   * Return true iff the string counts as an HTTP/1.1 "token".
   */
  private static boolean isToken(final String value) {
    final int len = value.length();
    char c;
    final char[] charArray = value.toCharArray();
    for (int i = 0; i < len; i++) {
      c = charArray[i];
      if (c < 0x20 || c >= 0x7f) {
        return false;
      } else {
        if (checkFlag[c]) {
          return false;
        }
      }
    }
    return true;
  }
}
