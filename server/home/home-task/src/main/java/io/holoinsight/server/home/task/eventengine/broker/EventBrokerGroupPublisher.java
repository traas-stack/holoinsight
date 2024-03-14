/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.task.eventengine.broker;

/**
 * broker发布者，用于发布当前broker到broker分组
 * 
 * @author jsy1001de
 * @version 1.0: EventBrokerGroupPublisher.java, Date: 2024-03-14 Time: 15:18
 */
public interface EventBrokerGroupPublisher {
  /**
   * 将当前节点发布到broker集群中
   */
  void publish();
}
