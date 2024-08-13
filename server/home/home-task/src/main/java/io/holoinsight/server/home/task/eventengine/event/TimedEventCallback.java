/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.task.eventengine.event;

/**
 *
 * @author jsy1001de
 * @version 1.0: TimedEventCallback.java, v 0.1 2022年04月07日 11:38 上午 jinsong.yjs Exp $
 */
public interface TimedEventCallback {
  void callback(TimedEventData timedEventData);
}
