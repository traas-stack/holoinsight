/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.task.eventengine.broker;

/**
 * broker分组观察者，观察broker分组中成员变化
 * 
 * @author jsy1001de
 * @version 1.0: EventBrokerGroupObserver.java, Date: 2024-03-14 Time: 15:17
 */
public interface EventBrokerGroupObserver {
  void watch(EventBrokerGroupCallback groupChangeCallback);
}
