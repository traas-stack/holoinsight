/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.server.service.impl;

import io.holoinsight.server.common.event.Event;
import io.holoinsight.server.apm.engine.model.EventDO;
import io.holoinsight.server.apm.engine.storage.EventStorage;
import io.holoinsight.server.common.event.EventService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventServiceImpl implements EventService {

  @Autowired
  private EventStorage eventStorage;

  @Override
  public List<Event> queryEvents(String tenant, long startTime, long endTime,
      Map<String, String> termParams) throws Exception {
    return eventStorage.queryEvents(tenant, startTime, endTime, termParams);
  }

  @Override
  public void insert(List<Event> events) throws Exception {
    List<EventDO> eventDOS = new ArrayList<>();
    for (Event event : events) {
      EventDO eventDO = new EventDO();
      BeanUtils.copyProperties(event, eventDO);
      eventDOS.add(eventDO);
    }
    eventStorage.insert(eventDOS);
  }
}
