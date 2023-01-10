/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.function;

import io.holoinsight.server.home.alert.model.trigger.CompareParam;
import lombok.Data;

import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/3/17 9:51 下午
 */
@Data
public class FunctionConfigParam {

  /**
   * 执行时间点
   */
  private Long period;

  /**
   * 数据周期
   */
  private Long stepTime;

  /**
   * 持续时间
   */
  private int duration;

  /**
   * 比较类型,算法：up/down
   */
  private List<CompareParam> cmp;

  /**
   * 跟踪id
   */
  private String traceId;

}
