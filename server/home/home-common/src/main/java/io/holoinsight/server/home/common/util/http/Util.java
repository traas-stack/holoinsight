/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util.http;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Util {

  public static void checkUrl(String domain, String _url) throws HttpException {
    if (StringUtils.isBlank(domain)) {
      throw new HttpException("empty domain");
    } else if (!domain.startsWith("http://") && !domain.startsWith("https://")) {
      throw new HttpException("inlegal domain: " + domain);
    }
  }

  public static String getUrl(String domain, String _url, Map<String, Object> params) {
    StringBuilder args = new StringBuilder();
    if (params != null && params.size() != 0) {
      for (Entry<String, Object> entry : params.entrySet()) {
        if (args.length() != 0) {
          args.append("&");
        }
        args.append(entry.getKey()).append("=").append(entry.getValue());
      }
    }

    StringBuilder urlBuilder = new StringBuilder(domain);
    if (_url != null)
      urlBuilder.append(_url);
    if (args.length() > 0) {
      if (_url != null && _url.contains("?")) {
        urlBuilder.append("&").append(args.toString());
      } else {
        urlBuilder.append("?").append(args.toString());
      }
    }
    return urlBuilder.toString();
  }

  public static Header[] buildHeaders(Map<String, String> inHeaders) {
    List<Header> headerList = new ArrayList<Header>();
    for (Entry<String, String> inHeaderEntry : inHeaders.entrySet()) {
      final Entry<String, String> tmpE = inHeaderEntry;
      headerList.add(new Header() {

        @Override
        public String getValue() {
          return tmpE.getValue();
        }

        @Override
        public String getName() {
          return tmpE.getKey();
        }

        @Override
        public HeaderElement[] getElements() throws ParseException {
          return null;
        }
      });
    }
    return headerList.toArray(new Header[0]);
  }

  public static String readData(InputStream in, String charsetName) throws IOException {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();

    byte[] buffer = new byte[1024];
    int len = 0;
    while (true) {
      len = in.read(buffer, 0, 1024);
      if (len < 0) {
        break;
      }
      bout.write(buffer, 0, len);
    }

    return StringUtils.isBlank(charsetName) ? bout.toString() : bout.toString(charsetName);
  }

  /**
   * 获取response编码
   */
  public static String getCharsetName(HttpResponse response) {
    Header header = response.getEntity().getContentType();
    if (header == null) {
      return null;
    }
    HeaderElement[] eles = header.getElements();
    for (HeaderElement ele : eles) {
      if (ele.getName().equals("charset")) {
        return ele.getValue();
      }
    }
    return null;
  }

  public static List<NameValuePair> buildFormPostBody(Map<String, String> postForm) {

    List<NameValuePair> pairs = new ArrayList<>();
    for (Entry<String, String> entry : postForm.entrySet()) {
      pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
    }
    return pairs;
  }
}
