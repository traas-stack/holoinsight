/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.event;

import lombok.Data;

/**
 * @author wangsiyuan
 * @date 2022/6/16 8:49 下午
 */
@Data
public class WebhookInfo implements Cloneable {

  /**
   * 请求方式
   */
  private String requestType;

  /**
   * 请求地址
   */
  private String requestUrl;

  /**
   * 租户id
   */
  private String tenant;

  /**
   * 请求头
   */
  private String requestHeaders;

  /**
   * 请求体
   */
  private String requestBody;

  /**
   * 额外信息
   */
  private String extra;

  /**
   * 拼装报文
   */
  private String webhookMsg;

  public WebhookInfo clone() {
    try {
      return (WebhookInfo) super.clone();
    } catch (CloneNotSupportedException e) {
      return null;
    }
  }
}
