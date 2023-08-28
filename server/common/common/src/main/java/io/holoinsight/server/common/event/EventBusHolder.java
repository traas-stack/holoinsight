/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.event;

import com.google.common.eventbus.EventBus;

/**
 * <p>
 * created at 2022/8/24
 *
 * @author zzhb101
 */
public class EventBusHolder {
  public static final EventBus INSTANCE = new EventBus();
}
