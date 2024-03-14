/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.task.eventengine.event;

import io.holoinsight.server.home.task.eventengine.wheeltimer.WheelTimeout;
import io.holoinsight.server.home.task.eventengine.wheeltimer.TimerTask;
import io.holoinsight.server.home.task.eventengine.wheeltimer.WheelTimerImpl;
import io.holoinsight.server.common.DateUtil;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author jsy1001de
 * @version 1.0: DelayTaskTestCase.java, Date: 2024-03-08 Time: 15:01
 */
public class DelayTaskTestCase {
  @Test
  public void testWheelTimer() throws InterruptedException {
    final WheelTimerImpl wheelTimer = new WheelTimerImpl();

    final Counter count = new Counter();
    System.out.print("\n");
    System.out.print(DateUtil.getDateOf_HHMMSS(new Date()));
    System.out.print("\n");
    for (int i = 0; i < 20; i++) {
      wheelTimer.newTimeout(new TimerTask() {
        @Override
        public void run(WheelTimeout wheelTimeout) throws Exception {
          synchronized (count) {
            System.out.print("\n");
            System.out.print(count.getCounter());
            count.setCounter(count.getCounter() + 1);
          }
        }
      }, 10, TimeUnit.SECONDS);
    }
    System.out.print("\n");
    System.out.print(DateUtil.getDateOf_HHMMSS(new Date()));
    System.out.print("\n");
    System.out.print(count.getCounter());
    Thread.sleep(15 * 1000);

  }

  @Test
  public void testEventTimer() throws InterruptedException {
    final EventTimer eventTimer = new EventTimerProxyImpl();

    TimedEventData event = createTimedEvent(10);
    System.out.print(DateUtil.getDateOf_HHMMSS(new Date()));
    System.out.print("\n");
    eventTimer.attach(event, event.getTimeoutMills(), new TimedEventCallback() {
      @Override
      public void callback(TimedEventData timedEventData) {
        System.out.print("\n");
        System.out.println(event);
      }
    });
    System.out.print("\n");
    System.out.print(DateUtil.getDateOf_HHMMSS(new Date()));
    Thread.sleep(15 * 1000);
  }

  private TimedEventData createTimedEvent(Integer timeoutSec) {

    String eventData = "test";

    long timeoutMills = timeoutSec * 1000;

    return new TimedEventData("REQUEST_TIMEOUT_CHECK_EVENT_TOPIC", timeoutMills, eventData);
  }

  @Data
  private static class Counter {
    private int counter = 0;

  }
}
