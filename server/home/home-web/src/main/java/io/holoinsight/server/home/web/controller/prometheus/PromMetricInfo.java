/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.web.controller.prometheus;

import lombok.Data;

/**
 * @author jsy1001de
 * @version 1.0: PromMetricInfo.java, Date: 2024-05-20 Time: 17:51
 */
@Data
public class PromMetricInfo {
  /**
   * prometheus指标名称
   */
  private String __name__;
  /**
   * prometheus实例名称
   */
  private String instance;
  /**
   * prometheus任务名称
   */
  private String job;
}
