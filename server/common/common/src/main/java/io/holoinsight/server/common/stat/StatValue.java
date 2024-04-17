/*
 * Alipay.com Inc. Copyright (c) 2004-2018 All Rights Reserved.
 */
package io.holoinsight.server.common.stat;

import com.xzchaoo.commons.basic.lang.TimeService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * @author masaimu
 * @version 2024-04-24 11:09:00
 */
public class StatValue {
  private static final AtomicReferenceFieldUpdater<StatValue, long[]> UPDATER
  //
      = AtomicReferenceFieldUpdater //
          .newUpdater(StatValue.class, long[].class, "values"); //

  private static final AtomicLongFieldUpdater<StatValue> LAST_USED_TIME_UPDATER =
      AtomicLongFieldUpdater.newUpdater(StatValue.class, "lastUsedTime");

  /**
   * EMPTY cache
   */
  private static final Map<Integer, long[]> EMPTY = new ConcurrentHashMap<>();

  private final long[] empty;
  private volatile long[] values;

  private volatile long lastUsedTime = TimeService.getMillTime();


  StatValue(int size) {
    values = empty = EMPTY.computeIfAbsent(size, long[]::new);
  }

  long getLastUsedTime() {
    return lastUsedTime;
  }

  void add(long[] add) {

    if (add.length != values.length) {
      // TODO warn?
      return;
    }

    // 没必要volatile写
    LAST_USED_TIME_UPDATER.lazySet(this, TimeService.getMillTime());

    long[] temp = new long[values.length];
    for (;;) {
      long[] values = this.values;
      for (int i = 0; i < values.length; i++) {
        temp[i] = values[i] + add[i];
      }
      if (UPDATER.compareAndSet(this, values, temp)) {
        break;
      }
    }
  }

  void set(long[] set) {

    if (set.length != values.length) {
      // TODO warn?
      return;
    }

    // 没必要volatile写
    LAST_USED_TIME_UPDATER.lazySet(this, TimeService.getMillTime());

    long[] temp = new long[values.length];
    for (;;) {
      long[] values = this.values;
      System.arraycopy(set, 0, temp, 0, values.length);
      if (UPDATER.compareAndSet(this, values, temp)) {
        break;
      }
    }
  }

  long[] getAndClear() {
    return UPDATER.getAndSet(this, empty);
  }
}
