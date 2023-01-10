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
public class PassReasons {
  private Boolean __fall_rate_filter;
  private Boolean __impact_event;
  private Boolean __impact_fall_back;
  private Boolean __impact_similar_test;
  private Boolean __sale_filter;
  private Boolean _core_fall_zero;
  private Boolean _core_impact_test;
}
