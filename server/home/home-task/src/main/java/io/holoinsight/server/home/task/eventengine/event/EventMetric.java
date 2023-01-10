/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.task.eventengine.event;

import lombok.Data;

/**
 *
 * @author jsy1001de
 * @version 1.0: EventMetric.java, v 0.1 2022年04月07日 11:40 上午 jinsong.yjs Exp $
 */
@Data
public class EventMetric {
  private String name;

  private int value;
}
