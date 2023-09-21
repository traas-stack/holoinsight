/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.web.impl;

import com.google.common.base.Strings;
import io.holoinsight.server.common.event.Event;
import io.holoinsight.server.apm.common.model.query.Request;
import io.holoinsight.server.common.event.EventService;
import io.holoinsight.server.apm.web.EventApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class EventApiController implements EventApi {
  @Autowired
  private EventService eventService;

  @Override
  public ResponseEntity<List<Event>> queryEvents(Request request) throws Exception {
    String tenant = request.getTenant();

    if (Strings.isNullOrEmpty(tenant)) {
      throw new IllegalArgumentException("The condition must contains tenant.");
    }
    List<Event> events = eventService.queryEvents(request.getTenant(), request.getStartTime(),
        request.getEndTime(), request.getTermParams());
    return ResponseEntity.ok(events);
  }

  @Override
  public ResponseEntity<Boolean> insertEvents(List<Event> events) {
    try {
      eventService.insert(events);
    } catch (Exception e) {
      return ResponseEntity.ok(false);
    }
    return ResponseEntity.ok(true);
  }
}
