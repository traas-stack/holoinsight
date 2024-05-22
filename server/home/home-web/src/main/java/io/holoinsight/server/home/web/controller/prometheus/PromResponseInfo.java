/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.web.controller.prometheus;

import lombok.Data;

import java.util.List;

/**
 * @author jsy1001de
 * @version 1.0: PromResponseInfo.java, Date: 2024-05-20 Time: 17:50
 */
@Data
public class PromResponseInfo<T> {
  /**
   * 状态 成功-- success
   */
  private String status;
  /**
   * prometheus指标属性和值
   */
  private T data;

  /**
   * 错误码
   */
  private String errorType;
  /**
   * 错误信息
   */
  private String error;

  /**
   * warnings 信息
   */
  private List<String> warnings;
}
