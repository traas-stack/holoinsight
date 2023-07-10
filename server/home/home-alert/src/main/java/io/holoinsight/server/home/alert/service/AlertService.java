/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service;

import static io.holoinsight.server.home.alert.service.event.alertManagerEvent.AlertManagerBuildMsgHandler.buildMsgWithMap;

import java.util.Collections;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.alert.common.http.XHttpResponse;
import io.holoinsight.server.home.alert.common.webhook.WebhookUtil;
import io.holoinsight.server.home.alert.model.event.WebhookInfo;
import io.holoinsight.server.home.common.service.query.WebhookResponse;
import io.holoinsight.server.home.dal.model.dto.AlarmWebhookTestDTO;
import lombok.Data;

@Service
public class AlertService {

  public WebhookResponse webhookTest(AlarmWebhookTestDTO alarmWebhookDTO) {
    WebhookTestRequest request = new WebhookTestRequest();
    request.setRequestType(alarmWebhookDTO.getRequestType());
    request.setRequestUrl(alarmWebhookDTO.getRequestUrl());
    request.setTestBody(alarmWebhookDTO.getTestBody());
    request.setTenant(alarmWebhookDTO.getTenant());
    request.setExtra(alarmWebhookDTO.getExtra());
    if (alarmWebhookDTO.getRequestHeaders() != null) {
      request.setRequestHeaders(alarmWebhookDTO.getRequestHeaders());
    }
    if (alarmWebhookDTO.getRequestHeaders() != null) {
      request.setRequestBody(alarmWebhookDTO.getRequestBody());
    }

    return webhookTest(request);
  }

  public WebhookResponse webhookTest(WebhookTestRequest request) {
    // 转换报文
    Map<String, String> valuesMap = J.fromJson(request.getTestBody(), Map.class);
    String webhookTemplate = request.getRequestBody();
    String webhookMsg = buildMsgWithMap(webhookTemplate, valuesMap, Collections.emptyList());
    // 发送webhook
    WebhookInfo webhookInfo = getWebhookInfo(request, webhookMsg);
    XHttpResponse xHttpResponse = WebhookUtil.sendWebhook(webhookInfo);
    WebhookResponse webhookResponse = new WebhookResponse();
    if (xHttpResponse != null) {
      webhookResponse.setResponse(xHttpResponse.getStringResponse());
      webhookResponse.setCode(xHttpResponse.code);
      webhookResponse.setRequestMsg(webhookMsg);
    } else {
      webhookResponse.setRequestMsg(webhookMsg);
    }
    return webhookResponse;
  }

  private WebhookInfo getWebhookInfo(WebhookTestRequest request, String webhookMsg) {
    WebhookInfo webhookInfo = new WebhookInfo();
    webhookInfo.setWebhookMsg(webhookMsg);
    webhookInfo.setRequestType(request.getRequestType());
    webhookInfo.setRequestBody(request.getRequestBody());
    webhookInfo.setRequestHeaders(request.getRequestHeaders());
    webhookInfo.setRequestUrl(request.getRequestUrl());
    webhookInfo.setTenant(request.getTenant());
    webhookInfo.setExtra(request.getExtra());
    return webhookInfo;
  }

  @Data
  public static class WebhookTestRequest {
    private String tenant;
    private String requestType;
    private String requestUrl;
    private String requestHeaders;
    private String requestBody;
    private String testBody;
    private String extra;
  }

}
