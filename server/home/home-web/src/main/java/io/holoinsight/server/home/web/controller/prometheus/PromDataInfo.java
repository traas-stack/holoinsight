/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.web.controller.prometheus;

import lombok.Data;

import java.util.List;

/**
 * @author jsy1001de
 * @version 1.0: PromDataInfo.java, Date: 2024-05-20 Time: 17:50
 */
@Data
public class PromDataInfo {
  /**
   * prometheus结果类型 vector--瞬时向量 matrix--区间向量 scalar--标量 string--字符串
   */
  private String resultType;
  /**
   * prometheus指标属性和值
   */
  private List<PromResultInfo> result;
}
