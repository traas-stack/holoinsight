/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.task.eventengine.wheeltimer;

/**
 *
 * @author jsy1001de
 * @version 1.0: TimerTask.java, v 0.1 2022年04月07日 11:42 上午 jinsong.yjs Exp $
 */
public interface TimerTask {
  /**
   * 在指定延时后执行task任务
   *
   * @param wheelTimeout
   * @throws Exception
   */
  void run(WheelTimeout wheelTimeout) throws Exception;
}
