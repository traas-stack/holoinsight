/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.stat;

/**
 * @author masaimu
 * @version 2024-04-24 11:06:00
 */
public interface StatAccumulator<K> {
  /**
   * Add metrics
   *
   * @param key
   * @param values
   */
  void add(K key, long[] values);

  void add(K key, double[] values);

  void set(K key, long[] values);

  void set(K key, double[] values);

  default StatAccumulator.KeyBind<K> bind(K key) {
    return new StatAccumulator.KeyBind<>(this, key);
  }

  class KeyBind<K> {
    private final StatAccumulator<K> sa;
    private final K key;

    public KeyBind(StatAccumulator<K> sa, K key) {
      this.sa = sa;
      this.key = key;
    }

    public void add(long[] values) {
      sa.add(key, values);
    }

    public void add(double[] values) {
      sa.add(key, values);
    }
  }
}
