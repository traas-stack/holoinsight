/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.calculate;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wangsiyuan
 * @date 2022/10/13 8:55 下午
 */
public class AlgorithmHttp {

  private static final Logger LOGGER = LoggerFactory.getLogger(AlgorithmHttp.class);

  private static final RequestConfig requestConfig;

  static {
    requestConfig = RequestConfig.custom().setConnectTimeout(1000 * 60)
        .setConnectionRequestTimeout(1000 * 6).setSocketTimeout(1000 * 60 * 3).build();
  }

  public static String invokeAlgorithm(String algorithmUrl, String requestBody) {
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");
    return post(algorithmUrl, requestBody, headers);
  }

  public static String post(String algorithmUrl, String requestBody, Map<String, String> headers) {
    CloseableHttpClient httpClient = HttpClients.createDefault();
    CloseableHttpResponse response = null;
    String resultString = null;
    try {
      long start = System.currentTimeMillis();
      HttpPost httpPost = new HttpPost(algorithmUrl);
      if (headers != null) {
        for (String key : headers.keySet()) {
          httpPost.addHeader(key, headers.get(key));
        }
      }
      httpPost.setConfig(requestConfig);
      if (requestBody != null) {
        StringEntity entity = new StringEntity(requestBody,
            ContentType.create(ContentType.APPLICATION_JSON.getMimeType(), "utf-8"));
        httpPost.setEntity(entity);
      }
      response = httpClient.execute(httpPost);
      long cost = System.currentTimeMillis() - start;
      if (response != null) {
        resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
        if (response.getStatusLine().getStatusCode() != 200) {
          LOGGER.error(
              "[InvokeAlgorithmError],AlgorithmUrl={},requestBody={},response={},content={},cost={}",
              algorithmUrl, requestBody, response.getStatusLine().getStatusCode(), resultString,
              cost);
          return null;
        } else {
          LOGGER.info(
              "[InvokeAlgorithmInfo],AlgorithmUrl={},requestBody={},response={},content={},cost={}",
              algorithmUrl, requestBody, response.getStatusLine().getStatusCode(), resultString,
              cost);
        }
      } else {
        LOGGER.error("[InvokeAlgorithmError],AlgorithmUrl={}, response is null,cost={}",
            algorithmUrl, cost);
      }

    } catch (Exception e) {
      LOGGER.error("[InvokeAlgorithmException] Sync Exception for {}", algorithmUrl, e);
    } finally {
      try {
        if (response != null) {
          response.close();
        }
      } catch (IOException e) {
        LOGGER.error("[InvokeAlgorithmException] Http close fail for {}", algorithmUrl, e);
      }
    }
    return resultString;
  }
}
