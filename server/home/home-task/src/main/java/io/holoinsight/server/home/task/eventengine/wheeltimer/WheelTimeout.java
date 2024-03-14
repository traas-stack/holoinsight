/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.task.eventengine.wheeltimer;

/**
 *
 * @author jsy1001de
 * @version 1.0: Timeout.java, v 0.1 2022年04月07日 11:41 上午 jinsong.yjs Exp $
 */
public interface WheelTimeout {
  /**
   * 返回创建handler的timer
   *
   * @return
   */
  WheelTimer getTimer();

  /**
   * 返回与handler关联的TimerTask
   *
   * @return
   */
  TimerTask getTask();

  /**
   * 判断与handler关联的TimerTask是否超时
   *
   * @return
   */
  boolean isExpired();

  /**
   * 判断与handler关联的TimerTask是否已被取消
   *
   * @return
   */
  boolean isCancelled();

  /**
   * 取消与handle关联的TimerTask。如果task已经执行或取消则直接返回
   */
  void cancel();
}
