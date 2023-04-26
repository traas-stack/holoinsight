/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.compute.algorithm.anomaly;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wangsiyuan
 * @date 2023/4/24 2:11 PM
 */
@Data
public class AlgorithmConfig implements Serializable {
  /**
   * algorithmType
   */
  private String algorithmType;
  /**
   * sensitivity
   */
  private String sensitivity;
}
