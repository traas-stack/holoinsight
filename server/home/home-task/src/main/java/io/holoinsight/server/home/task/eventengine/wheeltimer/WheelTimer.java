/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.task.eventengine.wheeltimer;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author jsy1001de
 * @version 1.0: Timer.java, v 0.1 2022年04月07日 11:41 上午 jinsong.yjs Exp $
 */
public interface WheelTimer {
  /**
   * 定时执行task
   *
   * @param task
   * @param delay
   * @param unit
   * @return 与task关联的超时handler接口
   * @throws IllegalStateException 如果timer已经停止则抛出此异常
   */
  WheelTimeout newTimeout(TimerTask task, long delay, TimeUnit unit);

  /**
   * 停止定时器，取消所有未执行任务并释放资源
   *
   * @return 被取消的未执行timeout
   */
  Set<WheelTimeout> stop();
}
