/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.task.eventengine;

import io.holoinsight.server.home.task.eventengine.event.EventMetric;
import io.holoinsight.server.home.task.eventengine.event.EventMetrics;
import io.holoinsight.server.home.task.eventengine.event.EventTimer;
import lombok.extern.slf4j.Slf4j;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author jsy1001de
 * @version 1.0: EventMetricsMonitorDaemon.java, Date: 2024-03-14 Time: 12:03
 */
@Slf4j
public class EventMetricsMonitorDaemon extends Thread {
  public static final int DEFAULT_EVENT_METRICS_MONITOR_INTERVAL = 30 * 1000;

  public static final int DEFAULT_EVENT_COUNTER_THRESHOLD = 512;

  public static final String EVENT_COUNTER = "EVENT_COUNTER";
  private Timer timer;

  private EventTimer eventTimer;

  /**
   *
   */
  public EventMetricsMonitorDaemon(Timer timer, EventTimer eventTimer) {
    this.timer = timer;
    this.eventTimer = eventTimer;

    this.setDaemon(true);
    this.setName("EventMetricsMonitorDaemon");
  }

  /**
   *
   */
  @Override
  public void run() {

    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        EventMetrics eventMetrics = eventTimer.getMetrics();

        boolean needPrint = false;
        for (EventMetric eventMetric : eventMetrics.getEventMetrics()) {

          if (eventMetric.getValue() > 0) {
            needPrint = true;
          }

          if (eventMetric.getName().endsWith("_" + EVENT_COUNTER)
              && eventMetric.getValue() > DEFAULT_EVENT_COUNTER_THRESHOLD) {
            log.error("Too many events: " + eventMetric);
          }
        }

        // 若存在eventMetric的value大于0，才需要打印一下eventMetrics
        if (needPrint) {
          log.info(eventMetrics.toString());
        }
      }
    }, 0, DEFAULT_EVENT_METRICS_MONITOR_INTERVAL);
  }
}
