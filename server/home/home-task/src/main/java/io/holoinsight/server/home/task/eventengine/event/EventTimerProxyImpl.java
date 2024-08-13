/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.task.eventengine.event;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 定时器代理类
 * 
 * @author jsy1001de
 * @version 1.0: EventTimerProxyImpl.java, v 0.1 2022年04月07日 11:47 上午 jinsong.yjs Exp $
 */
public class EventTimerProxyImpl implements EventTimer {

  private EventTimer[] eventTimers = new EventTimer[TimedEventData.SUPPORTED_TIME_UNITS.length];

  public EventTimerProxyImpl() {
    eventTimers[0] = new EventWheelTimer(TimeUnit.MILLISECONDS);
    eventTimers[1] = new EventWheelTimer(TimeUnit.SECONDS);
    eventTimers[2] = new EventWheelTimer(TimeUnit.MINUTES);
    eventTimers[3] = new EventWheelTimer(TimeUnit.HOURS);
  }

  @Override
  public void attach(final TimedEventData event, long timerMills,
      final TimedEventCallback timedEventCallback) {
    int timerIndex = TimedEventData.decideTimerIndex(timerMills);

    if (timerIndex >= 0) {

      EventTimer eventTimer = eventTimers[timerIndex];
      eventTimer.attach(event, timerMills, timedEventCallback);
    } else {
      throw new RuntimeException("Unsupported timer: " + timerMills);
    }
  }

  @Override
  public void cleanEvents(TimeUnit timeUnit) {
    for (EventTimer eventTimer : eventTimers) {
      if (((EventWheelTimer) eventTimer).getTimeUnit().equals(timeUnit)) {
        eventTimer.cleanEvents(timeUnit);
      }
    }
  }

  @Override
  public EventMetrics getMetrics() {
    EventMetrics eventMetrics = new EventMetrics();
    eventMetrics.setTimestamp(new Date());

    for (EventTimer eventTimer : eventTimers) {
      EventMetrics eventMetricsPerTimer = eventTimer.getMetrics();
      eventMetrics.merge(eventMetricsPerTimer);
    }

    return eventMetrics;
  }

}
