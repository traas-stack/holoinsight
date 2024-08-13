/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.task.eventengine.event;

/**
 * @author jsy1001de
 * @version 1.0: EventSubscriber.java, Date: 2024-03-14 Time: 14:16
 */
public interface EventSubscriber {
  /**
   * 事件通知接口
   *
   * @param eventData
   */
  void inform(EventData eventData);
}
