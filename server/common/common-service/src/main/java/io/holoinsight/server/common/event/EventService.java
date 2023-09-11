/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.event;

import io.holoinsight.server.common.event.Event;

import java.util.List;
import java.util.Map;

public interface EventService {

  List<Event> queryEvents(String tenant, long startTime, long endTime,
      Map<String, String> termParams) throws Exception;

  void insert(List<Event> events) throws Exception;
}
