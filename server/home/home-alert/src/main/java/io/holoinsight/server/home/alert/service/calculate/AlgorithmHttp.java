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
import java.util.UUID;

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

  public static String invokeAlgorithm(String algorithmUrl, String requestBody, String traceId) {
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");
    return post(algorithmUrl, requestBody, headers, traceId);
  }

  public static String post(String algorithmUrl, String requestBody, Map<String, String> headers,
      String traceId) {
    XHttpResponse response = null;
    String resultString = null;
    String detailTraceId = UUID.randomUUID().toString();
    long start = System.currentTimeMillis();
    try {
      XHttpRequest req = XHttpRequest.post(algorithmUrl, Collections.emptyMap(), "utf-8", 5000,
          requestBody.getBytes(), "application/json; charset=utf-8");
      response = HttpProxy.request(req);

      if (response != null) {
        resultString = response.getStringResponse();
        if (response.code != 200) {
          LOGGER.error(
              "{} {} [InvokeAlgorithmError],AlgorithmUrl={},requestBody={},response={},content={}",
              traceId, detailTraceId, algorithmUrl, requestBody, response.code, resultString);
          return null;
        } else {
          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                "{} [InvokeAlgorithmDetail],AlgorithmUrl={}, requestBody={},response={},content={}",
                detailTraceId, algorithmUrl, requestBody, response.code, resultString);
          }
        }
      } else {
        LOGGER.error("{} {} [InvokeAlgorithmError],AlgorithmUrl={}, response is null", traceId,
            detailTraceId, algorithmUrl);
      }

    } catch (Exception e) {
      LOGGER.error("{} {} [InvokeAlgorithmException] Sync Exception for {}", traceId, detailTraceId,
          algorithmUrl, e);
    } finally {
      long cost = System.currentTimeMillis() - start;
      int code = response == null ? 500 : response.code;
      LOGGER.info("{} {} [InvokeAlgorithmInfo],AlgorithmUrl={},responseCode={},cost={}", traceId,
          detailTraceId, algorithmUrl, code, cost);
    }
    return resultString;
  }
}
