/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.task.eventengine.broker;

import java.util.Set;

/**
 * @author jsy1001de
 * @version 1.0: EventBrokerGroupCallback.java, Date: 2024-03-14 Time: 15:14
 */
public interface EventBrokerGroupCallback {
  void updateBrokerGroup(Set<String> groupMembers);
}
