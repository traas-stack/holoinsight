/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.task.eventengine.event;

import io.holoinsight.server.home.task.eventengine.wheeltimer.WheelTimeout;
import io.holoinsight.server.home.task.eventengine.wheeltimer.TimerTask;
import io.holoinsight.server.home.task.eventengine.wheeltimer.WheelTimer;
import io.holoinsight.server.home.task.eventengine.wheeltimer.WheelTimerImpl;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 时间轮定时器wrapper
 * 
 * @author jsy1001de
 * @version 1.0: WheelTimer.java, v 0.1 2022年04月07日 11:40 上午 jinsong.yjs Exp $
 */
public class EventWheelTimer implements EventTimer {
  public static final String EVENT_COUNTER = "EVENT_COUNTER";
  private WheelTimer wheelTimer;

  private TimeUnit timeUnit;

  private AtomicInteger eventCounter = new AtomicInteger();

  /**
   * 默认构造器
   */
  public EventWheelTimer(TimeUnit timeUnit) {
    this.wheelTimer = new WheelTimerImpl();
    this.timeUnit = timeUnit;
  }

  /**
   * 指定tick单位时间的构造器
   *
   * @param tickDuration
   * @param timeUnit
   */
  public EventWheelTimer(long tickDuration, TimeUnit timeUnit) {
    this.wheelTimer = new WheelTimerImpl(tickDuration, timeUnit);
    this.timeUnit = timeUnit;
  }

  /**
   * 指定tick单位时间及时间轮大小的构造器
   *
   * @param tickDuration
   * @param timeUnit
   * @param ticksPerWheel
   */
  public EventWheelTimer(long tickDuration, TimeUnit timeUnit, int ticksPerWheel) {
    this.wheelTimer = new WheelTimerImpl(tickDuration, timeUnit, ticksPerWheel);
    this.timeUnit = timeUnit;
  }

  /**
   *
   */
  @Override
  public void attach(final TimedEventData event, long timerMills,
      final TimedEventCallback timedEventCallback) {
    wheelTimer.newTimeout(new TimerTask() {

      @Override
      public void run(WheelTimeout wheelTimeout) throws Exception {
        eventCounter.decrementAndGet();

        timedEventCallback.callback(event);
      }

    }, timerMills, TimeUnit.MILLISECONDS);

    eventCounter.incrementAndGet();
  }

  /**
   *
   */
  @Override
  public void cleanEvents(TimeUnit timeUnit) {
    if (this.timeUnit == timeUnit) {
      wheelTimer.stop();
      eventCounter.getAndSet(0);
    }
  }

  /**
   *
   */
  @Override
  public EventMetrics getMetrics() {
    EventMetrics eventMetrics = new EventMetrics();
    eventMetrics.setTimestamp(new Date());
    EventMetric eventMetric = new EventMetric();
    eventMetric.setName(EVENT_COUNTER);
    eventMetric.setValue(eventCounter.get());
    eventMetrics.add(eventMetric);

    EventMetric eventMetricByUnit = new EventMetric();
    eventMetricByUnit.setName(timeUnit.name() + "_" + EVENT_COUNTER);
    eventMetricByUnit.setValue(eventCounter.get());
    eventMetrics.add(eventMetricByUnit);

    return eventMetrics;
  }

  /**
   * Getter method for property timeUnit.
   *
   * @return property value of timeUnit
   */
  public TimeUnit getTimeUnit() {
    return timeUnit;
  }
}
