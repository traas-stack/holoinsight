/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.calculate;

import io.holoinsight.server.home.alert.common.http.HttpProxy;
import io.holoinsight.server.home.alert.common.http.XHttpRequest;
import io.holoinsight.server.home.alert.common.http.XHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

/**
 * @author wangsiyuan
 * @date 2022/10/13 8:55 下午
 */
public class AlgorithmHttp {

  private static final Logger LOGGER = LoggerFactory.getLogger(AlgorithmHttp.class);

  public static String invokeAlgorithm(String algorithmUrl, String requestBody) {
    XHttpResponse respose = null;
    try {
      XHttpRequest req = XHttpRequest.post(algorithmUrl, Collections.emptyMap(), "utf-8", 5000,
          requestBody.getBytes(), "application/json; charset=utf-8");
      respose = HttpProxy.request(req);
      if (respose.code != 200) {
        LOGGER.error("[InvokeAlgorithmError],AlgorithmUrl={},requestBody={},response={},content={}",
            algorithmUrl, requestBody, respose.code, respose.getStringResponse());
        return null;
      } else {
        LOGGER.info("[InvokeAlgorithmError],AlgorithmUrl={},requestBody={},response={},content={}",
            algorithmUrl, requestBody, respose.code, respose.getStringResponse());
      }
    } catch (Exception e) {
      LOGGER.error("[InvokeAlgorithmException] Sync Exception", e);
    }
    return respose == null ? null : respose.getStringResponse();
  }
}
