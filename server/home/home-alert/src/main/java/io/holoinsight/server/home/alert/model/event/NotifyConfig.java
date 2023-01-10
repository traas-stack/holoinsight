/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.event;

import lombok.Data;

import java.util.Map;

/**
 * @author wangsiyuan
 * @date 2022/8/4 8:37 下午
 */
@Data
public class NotifyConfig {

  /**
   * 产品码
   */
  String productCode;

  /**
   * 短信模板码
   */
  String smsTemplateCode;

  /**
   * 短信模板内容
   */
  Map<String, String> param;

  /**
   * 语音模板码
   */
  String phoneTemplateCode;

  /**
   * 邮件主题
   */
  String subject;

  /**
   * 邮件内容
   */
  String content;

  /**
   * 邮件额外内容
   */
  String extraContent;
}
