/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.common.webhook;


import io.holoinsight.server.home.alert.common.G;
import io.holoinsight.server.home.alert.common.http.HttpProxy;
import io.holoinsight.server.home.alert.common.http.XHttpRequest;
import io.holoinsight.server.home.alert.common.http.XHttpResponse;
import io.holoinsight.server.home.alert.model.event.WebhookInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author wangsiyuan
 * @date 2022/6/16 9:15 下午
 */
public class WebhookUtil {

  private static Logger LOGGER = LoggerFactory.getLogger(WebhookUtil.class);

  public static final String CONTENT_TYPE_UTF_8 = "application/json; charset=utf-8";

  /**
   * 发送webhook消息
   * 
   * @param webhookInfo
   * @return
   */
  public static XHttpResponse sendWebhook(WebhookInfo webhookInfo) {
    try {
      XHttpRequest xHttpRequest;
      switch (webhookInfo.getRequestType().toUpperCase()) {
        case "POST":
          xHttpRequest = XHttpRequest.post(webhookInfo.getRequestUrl(),
              G.get().fromJson(webhookInfo.getRequestHeaders(), Map.class), UTF_8.name(), 60000,
              webhookInfo.getWebhookMsg().getBytes(UTF_8), CONTENT_TYPE_UTF_8);
          break;
        case "GET":
          xHttpRequest = XHttpRequest.get(webhookInfo.getRequestUrl(), null,
              G.get().fromJson(webhookInfo.getRequestHeaders(), Map.class), UTF_8.name(), 60000);
          break;
        case "PUT":
          xHttpRequest = XHttpRequest.put(webhookInfo.getRequestUrl(),
              G.get().fromJson(webhookInfo.getRequestHeaders(), Map.class), UTF_8.name(), null,
              CONTENT_TYPE_UTF_8, 60000);
          break;
        case "DELETE":
          xHttpRequest = XHttpRequest.delete(webhookInfo.getRequestUrl(),
              G.get().fromJson(webhookInfo.getRequestHeaders(), Map.class), UTF_8.name(), 60000);
          break;
        default:
          xHttpRequest = XHttpRequest.get(webhookInfo.getRequestUrl(), null,
              G.get().fromJson(webhookInfo.getRequestHeaders(), Map.class), UTF_8.name(), 60000);
      }
      return HttpProxy.request(xHttpRequest);
    } catch (Exception e) {
      LOGGER.error("SendWebhook Exception WebhookInfo:{}", G.get().toJson(webhookInfo), e);
      return null;
    }
  }

  public static XHttpResponse sendWebhookWithException(WebhookInfo webhookInfo) throws Exception {
    XHttpRequest xHttpRequest;
    switch (webhookInfo.getRequestType().toUpperCase()) {
      case "POST":
        xHttpRequest = XHttpRequest.post(webhookInfo.getRequestUrl(),
            G.get().fromJson(webhookInfo.getRequestHeaders(), Map.class), UTF_8.name(), 60000,
            webhookInfo.getWebhookMsg().getBytes(UTF_8), CONTENT_TYPE_UTF_8);
        break;
      case "GET":
        xHttpRequest = XHttpRequest.get(webhookInfo.getRequestUrl(), null,
            G.get().fromJson(webhookInfo.getRequestHeaders(), Map.class), UTF_8.name(), 60000);
        break;
      case "PUT":
        xHttpRequest = XHttpRequest.put(webhookInfo.getRequestUrl(),
            G.get().fromJson(webhookInfo.getRequestHeaders(), Map.class), UTF_8.name(), null,
            CONTENT_TYPE_UTF_8, 60000);
        break;
      case "DELETE":
        xHttpRequest = XHttpRequest.delete(webhookInfo.getRequestUrl(),
            G.get().fromJson(webhookInfo.getRequestHeaders(), Map.class), UTF_8.name(), 60000);
        break;
      default:
        xHttpRequest = XHttpRequest.get(webhookInfo.getRequestUrl(), null,
            G.get().fromJson(webhookInfo.getRequestHeaders(), Map.class), UTF_8.name(), 60000);
    }
    return HttpProxy.request(xHttpRequest);
  }
}
