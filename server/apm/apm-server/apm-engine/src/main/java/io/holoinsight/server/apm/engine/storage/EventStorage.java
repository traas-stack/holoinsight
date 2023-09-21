/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.storage;

import io.holoinsight.server.common.event.Event;
import io.holoinsight.server.apm.engine.model.EventDO;

import java.util.List;
import java.util.Map;

/**
 * @author sw1136562366
 * @version : EventStorage.java, v 0.1 2023年08月22日 16:56 sw1136562366 Exp $
 */
public interface EventStorage extends WritableStorage<EventDO> {
  List<Event> queryEvents(String tenant, long startTime, long endTime,
      Map<String, String> termParams) throws Exception;
}
