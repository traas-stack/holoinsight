/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util.http;

import java.io.Serializable;

public class XHttpResponse implements Serializable {
  private static final long serialVersionUID = 7444452124053478904L;
  public int code;
  private String response;
  public byte[] byteResponse;
  public String headerLastModify; // Last-Modified
  public String headerEtag; // ETag

  public XHttpResponse(int code, String response, String headerEtag, String headerLastModify) {
    super();
    this.code = code;
    this.response = response;
    this.headerEtag = headerEtag;
    this.headerLastModify = headerLastModify;
  }

  @Override
  public String toString() {
    return "HttpResponse [code=" + code + ", response=" + response + "]";
  }

  public String getStringResponse() {
    if (response != null) {
      return response;
    }
    if (byteResponse != null) {
      return new String(byteResponse);
    }
    return null;
  }

  public String getStringResponse(String charset) throws Exception {
    if (response != null)
      return response;
    if (byteResponse != null)
      return new String(byteResponse, charset);
    return null;
  }
}
