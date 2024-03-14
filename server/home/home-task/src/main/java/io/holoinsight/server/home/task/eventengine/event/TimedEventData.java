/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.task.eventengine.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author jsy1001de
 * @version 1.0: TimedEventData.java, v 0.1 2022年04月07日 11:37 上午 jinsong.yjs Exp $
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TimedEventData extends EventData {

  public static final long EVENT_RETRY_TIMEOUT = 60 * 1000;
  public static final int EVENT_MAX_RETRY_TIMES = 3;
  public static final long[] TIME_UNIT_BASE = new long[] {1000, 60, 60};

  public static final List<TimeUnit> DEFAULT_TIME_UNIT_TO_PERSIST =
      Arrays.asList(TimeUnit.MINUTES, TimeUnit.HOURS);

  public static final TimeUnit[] SUPPORTED_TIME_UNITS =
      new TimeUnit[] {TimeUnit.MILLISECONDS, TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS};

  // ========== properties ==========

  private Long id;

  private Date timeoutAt;

  private String guardianServer;

  private Date createdAt;
  private Date modifiedAt;

  private List<TimeUnit> timeUnitToPersist = DEFAULT_TIME_UNIT_TO_PERSIST;

  // ========== constructions ==========

  public TimedEventData(String topic, long timeoutMills, String data) {
    super(topic, data);
    this.timeoutAt = new Date(System.currentTimeMillis() + timeoutMills);
  }

  public TimedEventData(String topic, Date timeoutAt) {
    super(topic);
    this.timeoutAt = timeoutAt;
  }

  public TimedEventData(String topic, Date timeoutAt, String data) {
    super(topic, data);
    this.timeoutAt = timeoutAt;
  }

  public static int decideTimerIndex(final long timerMills) {
    if (timerMills < 0) {
      return -1;
    }

    long timerValue = timerMills;

    for (int i = 0; i < SUPPORTED_TIME_UNITS.length - 1; i++) {
      timerValue = timerValue / TIME_UNIT_BASE[i];

      if (timerValue < 1) {
        return i;
      }
    }

    return SUPPORTED_TIME_UNITS.length - 1;
  }

  public boolean isValid() {
    return null != timeoutAt && (timeoutAt.getTime() - System.currentTimeMillis() > 0)
        && super.isValid();
  }

  public long getTimeoutMills() {
    long timeoutMills = timeoutAt.getTime() - System.currentTimeMillis();

    return timeoutMills > 0 ? timeoutMills : -1;
  }

  public boolean isExpired() {
    return System.currentTimeMillis() - timeoutAt.getTime() >= 0;
  }

  public boolean shouldPersist() {
    if (null != id) {
      return true;
    }

    long timeoutMills = timeoutAt.getTime() - System.currentTimeMillis();
    int index = decideTimerIndex(timeoutMills);

    return timeUnitToPersist.contains(SUPPORTED_TIME_UNITS[index]);
  }

  public void extendTimeout(boolean startFromCurTimestamp) {
    long newTimeoutMills;

    if (startFromCurTimestamp) {
      newTimeoutMills = System.currentTimeMillis() + EVENT_RETRY_TIMEOUT;
    } else {
      newTimeoutMills = this.getTimeoutAt().getTime() + EVENT_RETRY_TIMEOUT;
    }

    this.setTimeoutAt(new Date(newTimeoutMills));
  }

  public boolean shouldRetry() {
    return this.getRetryTimes() >= EVENT_MAX_RETRY_TIMES;
  }
}
