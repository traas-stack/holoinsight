/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * created at 2022/3/21
 *
 * @author zzhb101
 */
@ToString
@Getter
@Setter
public class Window {
  /**
   * 数据groupBy的时间窗口粒度, 单位是毫秒, 默认是对齐的, 比如 5s 级就是 0s 5s 10s 15s 20s ... 'none' 用于哪些不用聚合的数据, 比如单机系统指标,
   * 它就没有时间窗口这个概念, 它是根据执行时间每次执行都产生一个点的
   */
  private int interval;
}
