/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.compute.algorithm;

import lombok.Data;

/**
 * @author wangsiyuan
 * @date 2022/10/13 9:14 下午
 */
@Data
public class AbnormalFeatures {
  private String abnormalCategory;
  private Double anomalyDuration;
  private Double baseLine;
  private Double changeRate;
  private Double currentValue;
}
