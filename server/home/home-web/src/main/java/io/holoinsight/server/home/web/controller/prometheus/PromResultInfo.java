/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.web.controller.prometheus;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author jsy1001de
 * @version 1.0: PromResultInfo.java, Date: 2024-05-20 Time: 17:51
 */
@Data
public class PromResultInfo {
  /**
   * prometheus指标属性
   */
  private Map<String, String> metric;
  /**
   * prometheus指标值
   */
  private Object[] value;

  private List<Object[]> values;

}
