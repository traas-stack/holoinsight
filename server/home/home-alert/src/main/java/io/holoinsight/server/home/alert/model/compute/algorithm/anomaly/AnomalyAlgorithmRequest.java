/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.compute.algorithm.anomaly;

import lombok.Data;

import java.util.Map;

/**
 * AnomalyAlgorithmRequest
 *
 * @author wangsiyuan
 * @date 2023/4/24 11:54 AM
 */
@Data
public class AnomalyAlgorithmRequest {
  /**
   * inputTimeSeries
   */
  private Map<String, Double> inputTimeSeries;
  /**
   * intervalTime
   */
  private Integer intervalTime;
  /**
   * detectTime
   */
  private Long detectTime;
  /**
   * algorithmConfig
   */
  private AlgorithmConfig algorithmConfig;
  /**
   * ruleConfig
   */
  private RuleConfig ruleConfig;
}
