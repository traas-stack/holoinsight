/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * http DataCollection proxy
 */
public class HttpProxy {

  private static final Logger logger = LoggerFactory.getLogger(HttpProxy.class);

  public static final String HEADER_SEND_TIME = "http_proxy_send_time";
  public static final String HEADER_TRACE = "http_proxy_trace";

  private static HttpAsync.HttpAsyncClient httpAsyncClient;

  private static synchronized void start() {
    if (httpAsyncClient == null) {
      try {
        httpAsyncClient = new HttpAsync.HttpConfig().build();
      } catch (Exception e) {
        logger.error("init http async client error:", e);
        throw new RuntimeException("init http async client error:", e);
      }
    }
  }

  public static void request(XHttpRequest request, HttpAsync.HttpAsyncHandler handler) {
    if (httpAsyncClient == null) {
      start();
    }
    // 确保header中有请求发送时间
    if (request.inHeaders == null) {
      request.inHeaders = new HashMap<>();
    }
    long start = System.currentTimeMillis();
    String traceId = UUID.randomUUID().toString();

    request.inHeaders.put(HEADER_SEND_TIME, String.format("%d", start));
    request.inHeaders.put(HEADER_TRACE, traceId);
    httpAsyncClient.request(request, new HttpAsync.HttpAsyncHandler() {

      @Override
      public void onFail(String msg, Exception e) {
        logger.error("http request[" + request + "] fail, error: " + msg, e);
        handler.onFail(msg, e);
      }

      @Override
      public void handle(XHttpResponse rep) {
        logger.info("http request[" + request + "], trace = [" + traceId + "], cost "
            + (System.currentTimeMillis() - start) + "ms, code[" + rep.code + "].");
        handler.handle(rep);
      }
    });
  }

  public static void requestWithRetry(XHttpRequest request, HttpAsync.HttpAsyncRetryHandler handler,
      int retryCnt, int maxRetryTimes) {
    if (httpAsyncClient == null) {
      start();
    }
    // 确保header中有请求发送时间
    if (request.inHeaders == null) {
      request.inHeaders = new HashMap<>();
    }
    long start = System.currentTimeMillis();
    String traceId = UUID.randomUUID().toString();

    request.inHeaders.put(HEADER_SEND_TIME, String.format("%d", start));
    request.inHeaders.put(HEADER_TRACE, traceId);
    httpAsyncClient.request(request, new HttpAsync.HttpAsyncRetryHandler() {

      @Override
      public void onFail(String msg, Exception e, int retryCnt, int maxRetryTimes) {
        retryCnt++;
        logger.error("http request[" + request + "] fail, error: " + msg, e);
        if (retryCnt <= maxRetryTimes) {
          requestWithRetry(request, handler, retryCnt, maxRetryTimes);
        }
        handler.onFail(msg, e, retryCnt, maxRetryTimes);

      }

      @Override
      public void handle(XHttpResponse rep) {
        logger.info("http request[" + request + "], trace = [" + traceId + "], cost "
            + (System.currentTimeMillis() - start) + "ms, code[" + rep.code + "].");
        handler.handle(rep);
      }
    }, retryCnt, maxRetryTimes);
  }

  private static class Optional {
    boolean sucess;
    XHttpResponse response;
    String error;
    Exception exception;
  }

  public static XHttpResponse requestWithRetry(XHttpRequest request, int retryCnt,
      int maxRetryTimes) throws Exception {
    XHttpResponse response;
    try {
      response = request(request);
      return response;
    } catch (Exception e) {
      retryCnt++;
      if (retryCnt >= maxRetryTimes) {
        throw new RuntimeException(
            String.format("request fail and reach the max retry times[%s], %s. ", maxRetryTimes,
                e.getMessage()),
            e);
      } else {
        return requestWithRetry(request, retryCnt, maxRetryTimes);
      }
    }
  }

  public static XHttpResponse request(XHttpRequest request) throws Exception {
    if (httpAsyncClient == null) {
      start();
    }
    long start = System.currentTimeMillis();
    CountDownLatch latch = new CountDownLatch(1);
    Optional optional = new Optional();
    httpAsyncClient.request(request, new HttpAsync.HttpAsyncHandler() {
      @Override
      public void onFail(String msg, Exception e) {
        optional.sucess = false;
        optional.error = msg;
        optional.exception = e;
        latch.countDown();
      }

      @Override
      public void handle(XHttpResponse rep) {
        optional.sucess = true;
        optional.response = rep;
        latch.countDown();
      }
    });
    latch.await(30, TimeUnit.SECONDS);
    if (optional.sucess) {
      logger.info("http request[" + request + "] cost " + (System.currentTimeMillis() - start)
          + "ms, code[" + optional.response.code + "].");
      return optional.response;
    } else {
      logger.error("http request[" + request + "] fail, error: " + optional.error,
          optional.exception);
      throw optional.exception;
    }
  }

  private static Map<String, Object> genParams() {
    Map<String, Object> vParams = new HashMap<>();
    vParams.put("username", "xflush");
    vParams.put("token", "fji73!sp");
    Map<String, Object> params = new HashMap<>();
    params.put("pageSize", 40000);
    params.put("currentPage", 1);

    vParams.put("params", params);
    return vParams;
  }
}
