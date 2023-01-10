/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.log;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.methods.HttpRequestBase;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.util.Base64;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author zanghaibo
 * @time 2022-08-05 11:43 上午
 */

public class HawkSigner {

  private String keyId;

  private String key;

  public HawkSigner(String keyId, String key) {
    this.keyId = keyId;
    this.key = key;
  }

  public void signature(HttpRequestBase request) {
    request.addHeader("authorization",
        getAuthorizationHeader(request.getMethod(), request.getURI()));
  }

  public Map<String, String> getHawkSignHeader(String method, URI uri) {
    Map<String, String> header = new HashMap<>();
    header.put("authorization", getAuthorizationHeader(method, uri));
    return header;
  }

  public String getAuthorizationHeader(String method, URI uri) {
    String nonce = RandomStringUtils.random(6, true, true);
    long ts = System.currentTimeMillis() / 1000L;

    final StringBuilder sb = new StringBuilder(1024);
    sb.append("Hawk id=\"");
    sb.append(keyId);
    sb.append("\", ts=\"");
    sb.append(ts);
    sb.append("\", nonce=\"");
    sb.append(nonce);
    sb.append("\", mac=\"");
    sb.append(getMac(method, uri, ts, nonce));
    sb.append('"');
    return sb.toString();
  }

  private String getMac(String method, URI uri, long ts, String nonce) {
    StringBuilder sb = new StringBuilder(1024);
    sb.append("hawk.1.header");
    sb.append('\n');
    sb.append(ts);
    sb.append('\n');
    sb.append(nonce);
    sb.append('\n');
    sb.append(method.toUpperCase(Locale.ENGLISH));
    sb.append('\n');
    sb.append(uri.getRawPath());
    if (uri.getQuery() != null) {
      sb.append('?');
      sb.append(uri.getRawQuery());
    }
    sb.append('\n');
    sb.append(uri.getHost().toLowerCase(Locale.ENGLISH));
    sb.append('\n');
    sb.append(uri.getPort() > 0 ? uri.getPort() : ("https".equals(uri.getScheme()) ? 443 : 80));
    sb.append("\n\n\n");

    return encoding(sb.toString());
  }

  private String encoding(String payload) {
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256"));
      return Base64.getEncoder().encodeToString(mac.doFinal(payload.getBytes("UTF-8")));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
