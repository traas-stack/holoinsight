/*
 * Alipay.com Inc. Copyright (c) 2004-2018 All Rights Reserved.
 */
package io.holoinsight.server.common.stat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * @author masaimu
 * @version 2024-04-24 11:11:00
 */
public class StatDoubleValue {
  private static final AtomicReferenceFieldUpdater<StatDoubleValue, double[]> UPDATER
  //
      = AtomicReferenceFieldUpdater //
          .newUpdater(StatDoubleValue.class, double[].class, "values"); //

  /**
   * EMPTY cache
   */
  private static final Map<Integer, double[]> EMPTY = new ConcurrentHashMap<>();

  private final double[] empty;
  private volatile double[] values;

  StatDoubleValue(int size) {
    values = empty = EMPTY.computeIfAbsent(size, double[]::new);
  }

  void add(double[] add) {
    if (add.length != values.length) {
      // TODO warn?
      return;
    }
    double[] temp = new double[values.length];
    for (;;) {
      double[] values = this.values;
      for (int i = 0; i < values.length; i++) {
        temp[i] = values[i] + add[i];
      }
      if (UPDATER.compareAndSet(this, values, temp)) {
        break;
      }
    }
  }

  void set(double[] set) {
    if (set.length != values.length) {
      // TODO warn?
      return;
    }
    double[] temp = new double[values.length];
    for (;;) {
      double[] values = this.values;
      System.arraycopy(set, 0, temp, 0, values.length);
      if (UPDATER.compareAndSet(this, values, temp)) {
        break;
      }
    }
  }

  double[] getAndClear() {
    return UPDATER.getAndSet(this, empty);
  }
}
