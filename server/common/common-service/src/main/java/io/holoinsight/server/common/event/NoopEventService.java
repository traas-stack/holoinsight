/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.event;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * created at 2023/9/11
 *
 * @author xzchaoo
 */
public class NoopEventService implements EventService {
  @Override
  public List<Event> queryEvents(String tenant, long startTime, long endTime,
      Map<String, String> termParams) throws Exception {
    return Collections.emptyList();
  }

  @Override
  public void insert(List<Event> events) throws Exception {}
}
