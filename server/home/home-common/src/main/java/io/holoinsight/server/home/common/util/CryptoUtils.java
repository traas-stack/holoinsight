/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: CryptoUtils.java, v 0.1 2022年05月19日 5:58 下午 jinsong.yjs Exp $
 */
@Slf4j
public class CryptoUtils {
  public static Map<String, String> getCookies(HttpServletRequest request) {
    Map<String, String> ret = new HashMap<String, String>();
    Cookie[] cookies = request.getCookies();
    for (Cookie cookie : cookies) {
      String v = cookie.getValue();
      ret.put(cookie.getName(), v);
    }
    return ret;
  }

  public static String getCookie(HttpServletRequest request, String key) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null)
      return null;
    for (Cookie cookie : cookies) {
      String v = cookie.getValue();
      if (cookie.getName().equalsIgnoreCase(key)) {
        return v;
      }
    }
    return null;
  }

  public static String decrypt(String content) {
    if (StringUtils.isBlank(content)) {
      return content;
    }
    try {
      byte[] buffer = Hex.decodeHex(content.toCharArray());
      Cipher cipher = CipherUtils.getDec();
      byte[] result = cipher.doFinal(buffer);
      return new String(result, StandardCharsets.UTF_8);
    } catch (Exception e) {
      log.error("decrypt", e);
    }
    return null;
  }

  public static String encrypt(String content) {
    if (StringUtils.isBlank(content)) {
      return content;
    }
    try {
      Cipher cipher = CipherUtils.getEnc();
      byte[] byteContent = content.getBytes(StandardCharsets.UTF_8);
      byte[] result = cipher.doFinal(byteContent);
      String enc = String.valueOf(Hex.encodeHex(result));
      return enc.replaceAll("\\n", "");
    } catch (Exception e) {
      log.error("encrypt", e);
    }
    return null;
  }
}
