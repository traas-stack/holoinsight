/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;

import lombok.Data;

/**
 * @author wangsiyuan
 * @date 2022/6/20 7:54 下午
 */
@Data
public class AlarmWebhookTestDTO {

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
   * 测试体
   */
  private String testBody;

  /**
   * 额外信息
   */
  private String extra;
}
