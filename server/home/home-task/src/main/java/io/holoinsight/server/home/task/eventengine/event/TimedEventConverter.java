/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.task.eventengine.event;

import io.holoinsight.server.home.dal.model.TimedEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jsy1001de
 * @version 1.0: TimedEventConverter.java, Date: 2024-03-14 Time: 14:41
 */
public class TimedEventConverter {

  public static List<TimedEventData> Dos2Bos(List<TimedEvent> timedEvents) {
    List<TimedEventData> timedEventDatas = new ArrayList<>(timedEvents.size());

    for (TimedEvent timedEvent : timedEvents) {
      timedEventDatas.add(Do2Dto(timedEvent));
    }

    return timedEventDatas;
  }

  public static TimedEventData Do2Dto(TimedEvent timedEvent) {
    TimedEventData timedEventData =
        new TimedEventData(timedEvent.getTopic(), timedEvent.getTimeoutAt());
    timedEventData.setId(timedEvent.getId());
    timedEventData.setRetryTimes(timedEvent.getRetryTimes());
    timedEventData.setStatus(EventStatusEnum.getEnumByCode(timedEvent.getStatus()));
    timedEventData.setGuardianServer(timedEvent.getGuardianServer());
    timedEventData.setData(timedEvent.getData());
    timedEventData.setCreatedAt(timedEvent.getGmtCreate());
    timedEventData.setModifiedAt(timedEvent.getGmtModified());

    return timedEventData;
  }


  public static List<TimedEvent> Dtos2Dos(List<TimedEventData> timedEventDatas) {
    List<TimedEvent> timedEvents = new ArrayList<>(timedEventDatas.size());

    for (TimedEventData timedEventData : timedEventDatas) {
      timedEvents.add(Dto2Do(timedEventData));
    }

    return timedEvents;
  }

  public static TimedEvent Dto2Do(TimedEventData timedEventData) {
    TimedEvent timedEvent = new TimedEvent();
    timedEvent.setId(timedEventData.getId());
    timedEvent.setTopic(timedEventData.getTopic());
    timedEvent.setStatus(timedEventData.getStatus().getCode());
    timedEvent.setData((null == timedEventData.getData()) ? "" : timedEventData.getData());
    timedEvent.setGuardianServer(timedEventData.getGuardianServer());
    timedEvent.setRetryTimes(timedEventData.getRetryTimes());
    timedEvent.setTimeoutAt(timedEventData.getTimeoutAt());
    timedEvent.setGmtCreate(timedEventData.getCreatedAt());
    timedEvent.setGmtModified(timedEventData.getModifiedAt());
    return timedEvent;
  }
}
