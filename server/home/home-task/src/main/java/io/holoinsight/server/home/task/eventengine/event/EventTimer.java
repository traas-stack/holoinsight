/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.task.eventengine.event;

import java.util.concurrent.TimeUnit;

/**
 * 事件定时器接口
 * 
 * @author jsy1001de
 * @version 1.0: EventTimer.java, v 0.1 2022年04月07日 11:35 上午 jinsong.yjs Exp $
 */
public interface EventTimer {
  /**
   * 添加定时事件
   *
   * @param event
   * @param timerMills
   * @param timedEventCallback
   */
  void attach(TimedEventData event, long timerMills, TimedEventCallback timedEventCallback);

  /**
   * 根据时间粒度清除定时事件
   *
   * @param timeUnit
   */
  void cleanEvents(TimeUnit timeUnit);

  /**
   * 收集eventtimer当前运行时数据
   *
   * @return
   */
  EventMetrics getMetrics();
}
