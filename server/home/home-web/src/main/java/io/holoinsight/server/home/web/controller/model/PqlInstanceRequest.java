/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller.model;

import lombok.Data;

/**
 * @author zanghaibo
 * @time 2022-09-02 5:56 下午
 */

@Data
public class PqlInstanceRequest {

  /**
   * 租户
   */
  private String tenant;

  /**
   * pql
   */
  private String query;

  /**
   * 评估时间:ms
   */
  private Long time;

  /**
   * 超时
   */
  private String timeout = "30s";

  /**
   * 间隔
   */
  private String delta = "5m";
}
