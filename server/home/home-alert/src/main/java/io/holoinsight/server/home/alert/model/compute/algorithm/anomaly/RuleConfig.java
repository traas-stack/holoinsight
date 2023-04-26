/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.compute.algorithm.anomaly;

import lombok.Data;

/**
 * @author wangsiyuan
 * @date 2023/4/24 11:56 AM
 */
@Data
public class RuleConfig {
  /**
   * defaultDuration
   */
  private Integer defaultDuration;
  /**
   * customChangeRate
   */
  private Double customChangeRate;
}
