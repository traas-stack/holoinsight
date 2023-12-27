/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util.http;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;

public class XHttpRequest implements Serializable {
  private static final long serialVersionUID = 5363323934924301982L;
  public String url;
  public Map<String, Object> params;
  public String charset;
  public String method;
  public Map<String, String> inHeaders;
  public byte[] postBody;
  public String file;
  public String raw;
  public Map<String, String> postForm;
  public String contentType;
  public int timeoutMillisecond;

  public static XHttpRequest get(String url, Map<String, Object> params,
      Map<String, String> inHeaders, String charset, int timeoutMillisecond) {
    XHttpRequest ret = new XHttpRequest();
    ret.method = "GET";
    ret.url = url;
    ret.params = params;
    ret.inHeaders = inHeaders;
    ret.charset = charset;
    ret.timeoutMillisecond = timeoutMillisecond;
    return ret;
  }

  public static XHttpRequest get(String url) {
    return get(url, 5000);
  }

  public static XHttpRequest get(String url, int timeout) {
    return get(url, null, null, "utf-8", timeout);
  }

  public static XHttpRequest post(String url, Map<String, String> inHeaders, String charset,
      int timeoutMillisecond, byte[] postString) {
    return post(url, inHeaders, charset, timeoutMillisecond, postString, null);
  }

  public static XHttpRequest post(String url, Map<String, String> inHeaders, String charset,
      int timeoutMillisecond, byte[] postString, String contentType) {
    XHttpRequest ret = new XHttpRequest();
    ret.method = "POST";
    ret.url = url;
    ret.inHeaders = inHeaders;
    ret.charset = charset;
    ret.timeoutMillisecond = timeoutMillisecond;
    ret.postBody = postString;
    ret.contentType = contentType;
    return ret;
  }

  public static XHttpRequest put(String url, Map<String, String> inHeaders, String charset,
      String raw, String contentType, int timeoutMillisecond) {
    XHttpRequest ret = new XHttpRequest();
    ret.method = "put";
    ret.url = url;
    ret.inHeaders = inHeaders;
    ret.charset = charset;
    ret.timeoutMillisecond = timeoutMillisecond;
    ret.raw = raw;
    ret.contentType = contentType;
    return ret;
  }

  public static XHttpRequest delete(String url, Map<String, String> inHeaders, String charset,
      int timeoutMillisecond) {
    XHttpRequest ret = new XHttpRequest();
    ret.method = "delete";
    ret.url = url;
    ret.inHeaders = inHeaders;
    ret.charset = charset;
    ret.timeoutMillisecond = timeoutMillisecond;
    return ret;
  }

  public static XHttpRequest putJson(String url, Map<String, String> inHeaders, String charset,
      String raw, int timeoutMillisecond) {
    XHttpRequest ret = new XHttpRequest();
    ret.method = "put";
    ret.url = url;
    ret.inHeaders = inHeaders;
    ret.charset = charset;
    ret.timeoutMillisecond = timeoutMillisecond;
    ret.raw = raw;
    ret.contentType = "application/json";
    return ret;
  }

  public static XHttpRequest putFile(String url, String file, Map<String, String> inHeaders,
      int timeoutMillisecond) {
    XHttpRequest ret = new XHttpRequest();
    ret.method = "put";
    ret.url = url;
    ret.inHeaders = inHeaders;
    ret.timeoutMillisecond = timeoutMillisecond;
    ret.file = file;
    return ret;
  }

  public static XHttpRequest post(String url, Map<String, String> inHeaders, String charset,
      int timeoutMillisecond, Map<String, String> postForm) {
    return post(url, inHeaders, charset, timeoutMillisecond, postForm, null);
  }

  public static XHttpRequest postJson(String url, Map<String, String> inHeaders, String charset,
      String raw, int timeoutMillisecond) {
    XHttpRequest ret = new XHttpRequest();
    ret.method = "post";
    ret.url = url;
    ret.inHeaders = inHeaders;
    ret.charset = charset;
    ret.timeoutMillisecond = timeoutMillisecond;
    ret.raw = raw;
    ret.contentType = "application/json; charset=utf-8";
    return ret;
  }

  public static XHttpRequest post(String url, Map<String, String> inHeaders, String charset,
      int timeoutMillisecond, Map<String, String> postForm, String contentType) {
    XHttpRequest ret = new XHttpRequest();
    ret.method = "POST";
    ret.url = url;
    ret.inHeaders = inHeaders;
    ret.charset = charset;
    ret.timeoutMillisecond = timeoutMillisecond;
    ret.postForm = postForm;
    ret.contentType = contentType;
    return ret;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("XHttpRequest [url:").append(url).append(";params:")
        .append(mapToString(params)).append(";charset=").append(charset).append(", method=")
        .append(method).append(", inHeaders=").append(mapToString(inHeaders))
        .append(", postString=").append(postBody).append(", postForm=")
        .append(mapToString(postForm)).append(", contentType=").append(contentType)
        .append(", timeoutMillisecond=").append(timeoutMillisecond).append("]");
    return stringBuilder.toString();
  }

  private String mapToString(Map<?, ?> map) {
    StringBuilder stringBuilder = new StringBuilder();
    if (map == null) {
      return null;
    }
    for (Entry<?, ?> entry : map.entrySet()) {
      if (stringBuilder.length() != 0) {
        stringBuilder.append("&");
      }
      stringBuilder.append(entry.getKey().toString()).append("=")
          .append(entry.getValue().toString());
    }
    return stringBuilder.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((charset == null) ? 0 : charset.hashCode());
    result = prime * result + ((contentType == null) ? 0 : contentType.hashCode());
    result = prime * result + ((params == null) ? 0 : params.hashCode());
    result = prime * result + ((inHeaders == null) ? 0 : inHeaders.hashCode());
    result = prime * result + ((method == null) ? 0 : method.hashCode());
    result = prime * result + ((postForm == null) ? 0 : postForm.hashCode());
    result = prime * result + ((postBody == null) ? 0 : postBody.hashCode());
    result = prime * result + timeoutMillisecond;
    result = prime * result + ((url == null) ? 0 : url.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    XHttpRequest other = (XHttpRequest) obj;
    if (charset == null) {
      if (other.charset != null)
        return false;
    } else if (!charset.equals(other.charset))
      return false;
    if (contentType == null) {
      if (other.contentType != null)
        return false;
    } else if (!contentType.equals(other.contentType))
      return false;
    if (params == null) {
      if (other.params != null)
        return false;
    } else if (other.params == null || !mapToString(this.params).equals(mapToString(other.params)))
      return false;
    if (inHeaders == null) {
      if (other.inHeaders != null)
        return false;
    } else if (!inHeaders.equals(other.inHeaders))
      return false;
    if (method == null) {
      if (other.method != null)
        return false;
    } else if (!method.equals(other.method))
      return false;
    if (postForm == null) {
      if (other.postForm != null)
        return false;
    } else if (!postForm.equals(other.postForm))
      return false;
    if (postBody == null) {
      if (other.postBody != null)
        return false;
    } else if (!postBody.equals(other.postBody))
      return false;
    if (timeoutMillisecond != other.timeoutMillisecond)
      return false;
    if (url == null) {
      if (other.url != null)
        return false;
    } else if (!url.equals(other.url))
      return false;
    return true;
  }
}
