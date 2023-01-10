/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.collecttarget;

import lombok.Data;
import lombok.ToString;

/**
 * <p>
 * created at 2022/4/18
 *
 * @author zzhb101
 */
@Data
@ToString
public class MaintainStat {
  /**
   * 开始时间
   */
  long begin;
  /**
   * listDim的结束时间, 与 begin 的差值是listDim的耗时
   */
  long listDimEnd;
  /**
   * build的结束时间, 与 listDimEnd 的差值是build的耗时
   */
  long buildEnd;
  /**
   * 拿到lock的时间, 与buildENd 的差值是拿锁阻塞的时间
   */
  long lockEnd;
  /**
   * 总的结束时间
   */
  long end;
  /**
   * 新增的任务数
   */
  int add;
  /**
   * 更新的任务数
   */
  int update;
  /**
   * 删除的任务数
   */
  int delete;
  /**
   * 匹配的dim数
   */
  int dims;
  /**
   * 上一次最终构建成功的数
   */
  int oldSize;
  /**
   * 最近一次最终构建成功的数
   */
  int newSize;
}
