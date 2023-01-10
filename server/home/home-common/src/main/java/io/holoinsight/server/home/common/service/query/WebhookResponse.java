/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.service.query;

import lombok.Data;

/**
 * @author wangsiyuan
 * @date 2022/6/22 3:42 下午
 */
@Data
public class WebhookResponse {

  /**
   * 返回报文
   */
  private String response;

  /**
   * 返回code
   */
  private int code;

  /**
   * 请求体
   */
  private String requestMsg;
}
