/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.calculate;

import io.holoinsight.server.home.alert.common.http.HttpProxy;
import io.holoinsight.server.home.alert.common.http.XHttpRequest;
import io.holoinsight.server.home.alert.common.http.XHttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
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
    XHttpResponse response = null;
    String resultString = null;
    try {
      long start = System.currentTimeMillis();
      XHttpRequest req = XHttpRequest.post(algorithmUrl, Collections.emptyMap(), "utf-8", 5000,
          requestBody.getBytes(), "application/json; charset=utf-8");
      response = HttpProxy.request(req);
      long cost = System.currentTimeMillis() - start;
      if (response != null) {
        resultString = response.getStringResponse();
        if (response.code != 200) {
          LOGGER.error(
              "[InvokeAlgorithmError],AlgorithmUrl={},requestBody={},response={},content={},cost={}",
              algorithmUrl, requestBody, response.code, resultString, cost);
          return null;
        } else {
          LOGGER.info(
              "[InvokeAlgorithmInfo],AlgorithmUrl={},requestBody={},response={},content={},cost={}",
              algorithmUrl, requestBody, response.code, resultString, cost);
        }
      } else {
        LOGGER.error("[InvokeAlgorithmError],AlgorithmUrl={}, response is null,cost={}",
            algorithmUrl, cost);
      }

    } catch (Exception e) {
      LOGGER.error("[InvokeAlgorithmException] Sync Exception for {}", algorithmUrl, e);
    }
    return resultString;
  }
}
