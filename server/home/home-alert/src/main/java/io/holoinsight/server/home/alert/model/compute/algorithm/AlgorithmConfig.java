/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.compute.algorithm;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wangsiyuan
 * @date 2022/10/11 9:42 下午
 */
@Data
public class AlgorithmConfig implements Serializable {
  /**
   * 算法类型
   */
  private String algorithmType;
  /**
   * 检测类型
   */
  private String detectType;
  /**
   * 严格政策
   */
  private String strictPolicy;
  /**
   * 默认时间
   */
  private Integer defaultDuration;
}
