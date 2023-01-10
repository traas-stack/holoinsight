/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 执行规则, 某些任务不需要执行规则(比如日志清洗, 它是通过定义数据时间窗口来控制最终的数据粒度)
 * <p>
 * created at 2022/3/21
 *
 * @author zzhb101
 */
@ToString
@Getter
@Setter
public class ExecuteRule {
  /**
   * fixedRate: 固定频率 0s 5s 10s 15s ... oneshot: 一次性任务, 目前没有场景 timerange: 指定时间范围(一般是用于日志回放)
   */
  private String type;
  /**
   * 5s 1m, 单位是毫秒
   */
  private int fixedRate;
}
