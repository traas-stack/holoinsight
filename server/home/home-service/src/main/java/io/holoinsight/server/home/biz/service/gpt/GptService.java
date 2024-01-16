/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.gpt;

import com.alibaba.fastjson.JSON;
import io.holoinsight.server.common.config.EnvironmentProperties;
import io.holoinsight.server.home.common.util.http.HttpProxy;
import io.holoinsight.server.home.common.util.http.XHttpRequest;
import io.holoinsight.server.home.common.util.http.XHttpResponse;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author wangsiyuan
 * @date 2023/12/21 7:54 PM
 */
@Service
public class GptService {

  private static final Logger LOGGER = LoggerFactory.getLogger(GptService.class);

  @Autowired
  private EnvironmentProperties environmentProperties;


  public String submit(Map<String, Object> request, String traceId) {
    String submitUrl = environmentProperties.getGptUrl() + "/ai/task/submit";
    if (request != null && !request.isEmpty()) {
      LOGGER.info("{} [submit],url={},requestBody={}", traceId, submitUrl,
          JSON.toJSONString(request));
      return post(submitUrl, JSON.toJSONString(request), traceId);
    }
    return "FALSE";
  }

  public String save(List<Map<String, Object>> request, String traceId) {
    String saveUrl = environmentProperties.getGptUrl() + "/qa/save";
    if (CollectionUtils.isNotEmpty(request)) {
      request.forEach(req -> {
        LOGGER.info("{} [save],url={},requestBody={}", traceId, saveUrl, JSON.toJSONString(req));
        post(saveUrl, JSON.toJSONString(req), traceId);
      });
      return "SUCCESS";
    } else {
      return "FALSE";
    }

  }

  public String post(String url, String requestBody, String traceId) {

    XHttpResponse response = null;
    String resultString = null;
    String detailTraceId = UUID.randomUUID().toString();
    long start = System.currentTimeMillis();
    try {
      XHttpRequest req = XHttpRequest.post(url, Collections.emptyMap(), "utf-8", 20000,
          requestBody.getBytes(), "application/json; charset=utf-8");
      response = HttpProxy.request(req);

      if (response != null) {
        resultString = response.getStringResponse();
        if (response.code != 200) {
          LOGGER.error("{} {} [InvokeError],url={},requestBody={},response={},content={}", traceId,
              detailTraceId, url, requestBody, response.code, resultString);
          return null;
        } else {
          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("{} [InvokeDetail],url={}, requestBody={},response={},content={}",
                detailTraceId, url, requestBody, response.code, resultString);
          }
        }
      } else {
        LOGGER.error("{} {} [InvokeError],url={}, response is null", traceId, detailTraceId, url);
      }

    } catch (Exception e) {
      LOGGER.error("{} {} [InvokeException] Sync Exception for {}", traceId, detailTraceId, url, e);
    } finally {
      long cost = System.currentTimeMillis() - start;
      int code = response == null ? 500 : response.code;
      LOGGER.info("{} {} [InvokeInfo],url={},responseCode={},cost={}", traceId, detailTraceId, url,
          code, cost);
    }
    return resultString;
  }
}
