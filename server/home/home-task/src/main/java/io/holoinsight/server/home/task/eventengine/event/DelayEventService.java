/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.task.eventengine.event;

import java.util.concurrent.TimeUnit;

/**
 * @author jsy1001de
 * @version 1.0: DelayEventService.java, Date: 2024-03-14 Time: 14:15
 */
public interface DelayEventService {
  /**
   * 订阅事件
   *
   * @param eventTopic
   * @param subscriber
   */
  void subscribe(String eventTopic, EventSubscriber subscriber);

  /**
   * 退订事件
   *
   * @param eventTopic
   * @param subscriber
   */
  void unsubscribe(String eventTopic, EventSubscriber subscriber);

  /**
   * 发布即时事件
   *
   * @param eventData
   */
  void publish(EventData eventData);

  /**
   * 发布定时事件
   *
   * @param event
   * @return
   */
  void schedule(TimedEventData event);

  /**
   * 取消定时事件发布
   */
  void cleanUpScheduler(TimeUnit timeUnit);
}
