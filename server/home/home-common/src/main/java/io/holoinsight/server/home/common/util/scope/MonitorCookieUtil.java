/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util.scope;

import io.holoinsight.server.home.common.util.CookieUtils;
import io.holoinsight.server.home.common.util.CryptoUtils;
import io.holoinsight.server.home.common.util.StringUtil;
import io.holoinsight.server.common.J;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 *
 * @author jsy1001de
 * @version 1.0: MonitorCookieUtil.java, v 0.1 2022年03月15日 1:45 下午 jinsong.yjs Exp $
 */
@Slf4j
public class MonitorCookieUtil {
  // ULA相关
  static String APPNAME = "MONITOR";
  static String USER = APPNAME + "_USER_COOKIE";
  static String AUTH = APPNAME + "_AUTH_COOKIE";
  static String TENANT = "loginTenant";

  /**
   * 用户身份cookie
   */
  public static void addUserCookie(MonitorUser user, HttpServletResponse resp) {

    user.setExprie(System.currentTimeMillis() + CookieUtils.MAXAGE * 1000);
    String userJson = J.toJson(user);
    String userStr = CryptoUtils.encrypt(userJson);
    CookieUtils.addCookie(resp, USER, userStr);
    resp.setHeader("P3P",
        "CP=\"CURa ADMa DEVa PSAo PSDo OUR BUS UNI PUR INT DEM STA PRE COM NAV OTC NOI DSP COR\"");
  }

  /**
   *
   */
  public static void addTenantCookie(String tenant, HttpServletResponse resp) {
    if (StringUtil.isNotBlank(tenant)) {
      CookieUtils.addCookie(resp, TENANT, tenant);
    }
  }

  public static void removeUserCookie(HttpServletResponse resp) {
    CookieUtils.removeCookie(resp, USER);
    CookieUtils.removeCookie(resp, AUTH);
    CookieUtils.removeCookie(resp, TENANT);
    resp.setHeader("P3P",
        "CP=\"CURa ADMa DEVa PSAo PSDo OUR BUS UNI PUR INT DEM STA PRE COM NAV OTC NOI DSP COR\"");
  }

  public static MonitorUser getUserCookie(HttpServletRequest req) {
    Cookie userCookie = CookieUtils.getCookieModel(req, USER);
    if (null != userCookie) {
      try {
        String content = CryptoUtils.decrypt(userCookie.getValue());
        MonitorUser monitorUser = J.fromJson(content, MonitorUser.class);

        if (null != monitorUser && null != monitorUser.getExprie()
            && (System.currentTimeMillis() > monitorUser.getExprie())) {
          return null;
        }

        return monitorUser;
      } catch (Throwable e) {
        log.error("UserCookie decode error? must be changed by someone", e);
        return null;
      }
    }
    return null;
  }

  public static String getTenantCookie(HttpServletRequest req) {
    return CryptoUtils.getCookie(req, TENANT);
  }

  public static MonitorScope getScope(HttpServletRequest req, MonitorUser mu) {
    List<NameValuePair> params;
    if (req.getQueryString() == null) {
      params = new ArrayList<>();
    } else {
      params = URLEncodedUtils.parse(req.getQueryString(), StandardCharsets.UTF_8);
    }
    String tenant = null;
    for (NameValuePair param : params) {
      if ("tenant".equals(param.getName())) {
        tenant = param.getValue();
      }
    }

    MonitorScope ms = new MonitorScope();
    ms.tenant = tenant;

    String loginTenant = CookieUtils.getCookie(req, "loginTenant");
    String loginWorkspace = CookieUtils.getCookie(req, "loginWorkspace");
    if (StringUtil.isNotBlank(loginTenant)) {
      ms.tenant = loginTenant;
      mu.setLoginTenant(loginTenant);
    } else if (StringUtil.isNotBlank(mu.getLoginTenant())) {
      ms.tenant = mu.getLoginTenant();
    }

    if (StringUtil.isNotBlank(loginWorkspace)) {
      ms.workspace = loginWorkspace;
    }
    return ms;
  }

  public static String getTenantOrException() {
    MonitorScope ms = RequestContext.getContext().ms;
    return ms.getTenantIdOrException();
  }

  /**
   * 用户权限cookie to json -> zip -> 加密
   */
  public static void addMonitorAuthCookie(MonitorAuth ma, HttpServletResponse resp) {

    MonitorAuthPure monitorAuthPure = ma.toPure();
    monitorAuthPure.exprie = System.currentTimeMillis() + CookieUtils.MAXAGE * 1000;

    String authStr = zipString(J.toJson(monitorAuthPure));
    authStr = CryptoUtils.encrypt(authStr);
    CookieUtils.addCookie(resp, AUTH, authStr);
    resp.setHeader("P3P",
        "CP=\"CURa ADMa DEVa PSAo PSDo OUR BUS UNI PUR INT DEM STA PRE COM NAV OTC NOI DSP COR\"");
  }

  // 解密 -> unzip -> parse object
  public static MonitorAuth getMonitorAuthCookie(HttpServletRequest req) {
    Cookie authCookie = CookieUtils.getCookieModel(req, AUTH);
    if (null != authCookie) {
      try {
        String content = CryptoUtils.decrypt(authCookie.getValue());
        content = unzipString(content);
        // 解压
        MonitorAuthPure map = J.fromJson(content, MonitorAuthPure.class);
        if (null != map && null != map.exprie && (System.currentTimeMillis() > map.exprie)) {
          return null;
        }
        return map.toAdv();
      } catch (Throwable e) {
        log.error("Monitor Auth decode error? must be changed by someone", e);
        return null;
      }
    }
    return null;
  }

  public static void removeMonitorAuthCookie(HttpServletResponse resp) {
    CookieUtils.removeCookie(resp, AUTH);
  }

  // 压缩
  public static String zipString(String unzip) {
    Deflater deflater = new Deflater(9); // 0 ~ 9 压缩等级 低到高
    deflater.setInput(unzip.getBytes());
    deflater.finish();

    final byte[] bytes = new byte[256];
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(256);

    while (!deflater.finished()) {
      int length = deflater.deflate(bytes);
      outputStream.write(bytes, 0, length);
    }

    deflater.end();
    return new sun.misc.BASE64Encoder().encodeBuffer(outputStream.toByteArray());
  }

  // 解压缩
  public static String unzipString(String zip) throws Exception {
    byte[] decode = new sun.misc.BASE64Decoder().decodeBuffer(zip);

    Inflater inflater = new Inflater();
    inflater.setInput(decode);

    final byte[] bytes = new byte[256];
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(256);

    try {
      while (!inflater.finished()) {
        int length = inflater.inflate(bytes);
        outputStream.write(bytes, 0, length);
      }
    } catch (DataFormatException e) {
      throw e;
    } finally {
      inflater.end();
    }
    return outputStream.toString();
  }
}
