/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author jsy1001de
 * @version 1.0: CookieUtils.java, v 0.1 2022年05月19日 5:59 下午 jinsong.yjs Exp $
 */
public class CookieUtils {

  public static int MAXAGE = 20 * 60;

  static final String PATH = "/";

  public static void addCookie(HttpServletResponse resp, String name, Object value) {
    CommonCookieUtils.addCookie(name, value.toString(), MAXAGE, PATH, null, true, resp);
  }

  public static void addCookie(HttpServletResponse resp, String name, String path, Object value) {
    CommonCookieUtils.addCookie(name, value.toString(), MAXAGE, path, null, true, true, resp);
  }

  public static void addCookie(HttpServletResponse resp, String name, String path, Integer age,
      Object value) {
    CommonCookieUtils.addCookie(name, value.toString(), age, path, null, true, true, resp);
  }

  public static String getCookie(HttpServletRequest req, String name) {
    return CommonCookieUtils.getCookieValue(name, req);
  }


  public static Cookie getCookieModel(HttpServletRequest req, String name) {
    return CommonCookieUtils.getCookie(name, req);
  }

  public static void removeCookie(HttpServletResponse resp, String name) {
    CommonCookieUtils.setCookie(name, StringUtils.EMPTY, 0, PATH, null, true, resp);
  }
}
