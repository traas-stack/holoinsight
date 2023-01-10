/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller.model;

import lombok.Data;

/**
 * @author zanghaibo
 * @time 2022-09-02 5:57 下午
 */

@Data
public class PqlRangeQueryRequest {

  /**
   * 租户
   */
  private String tenant;

  /**
   * pql
   */
  private String query;

  /**
   * 开始时间:ms
   */
  private Long start;

  /**
   * 结束时间:ms
   */
  private Long end;

  /**
   * 步长
   */
  private Long step = 5L;

  /**
   * 超市
   */
  private String timeout = "30s";

  /**
   * 偏差
   */
  private String delta = "5m";

  /**
   * 补0
   */
  private Boolean fillZero = false;
}
