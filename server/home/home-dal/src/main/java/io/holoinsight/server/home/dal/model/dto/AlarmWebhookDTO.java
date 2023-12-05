/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author wangsiyuan
 * @date 2022/6/15 4:51 下午
 */
@Data
public class AlarmWebhookDTO {
  /**
   * id
   */
  private Long id;

  /**
   * 创建时间
   */
  private Date gmtCreate;

  /**
   * 修改时间
   */
  private Date gmtModified;

  /**
   * 创建者
   */
  private String creator;

  /**
   * 修改者
   */
  private String modifier;

  /**
   * 回调名称
   */
  private String webhookName;

  /**
   * 回调状态
   */
  private Byte status;

  /**
   * 请求方式
   */
  private String requestType;

  /**
   * 请求地址
   */
  private String requestUrl;

  /**
   * 回调类型
   */
  private Byte type;

  /**
   * 租户id
   */
  private String tenant;


  private String role;

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
   * 调试数据
   */
  private String webhookTest;
}
