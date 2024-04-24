/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.stat;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author masaimu
 * @version 2024-04-24 11:08:00
 */
public class DefaultStatAccumulator<K> implements StatAccumulator<K> {
  private final ConcurrentHashMap<K, StatValue> map = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<K, StatDoubleValue> map2 = new ConcurrentHashMap<>();
  private final StatManager sm;

  DefaultStatAccumulator(StatManager sm) {
    this.sm = sm;
  }

  @Override
  public void add(K key, long[] values) {
    StatValue statValue = map.get(key);
    if (statValue == null) {
      statValue = new StatValue(values.length);
      StatValue oldStatValue = map.putIfAbsent(key, statValue);
      if (oldStatValue != null) {
        statValue = oldStatValue;
      }
    }
    statValue.add(values);
  }

  void clean(long expireTime) {
    // 如果一个StatValue太久没有被用到, 那么可以删掉它
    // 但该删除动作目前无法做成原子动作动作, 害, 难...
    // 与add方法冲突
    map.values().removeIf(st -> st.getLastUsedTime() < expireTime);
  }

  @Override
  public void add(K key, double[] values) {
    StatDoubleValue statValue = map2.get(key);
    if (statValue == null) {
      statValue = new StatDoubleValue(values.length);
      StatDoubleValue oldStatValue = map2.putIfAbsent(key, statValue);
      if (oldStatValue != null) {
        statValue = oldStatValue;
      }
    }
    statValue.add(values);
  }

  @Override
  public void set(K key, long[] values) {
    StatValue statValue = map.get(key);
    if (statValue == null) {
      statValue = new StatValue(values.length);
      StatValue oldStatValue = map.putIfAbsent(key, statValue);
      if (oldStatValue != null) {
        statValue = oldStatValue;
      }
    }
    statValue.set(values);
  }

  @Override
  public void set(K key, double[] values) {
    StatDoubleValue statValue = map2.get(key);
    if (statValue == null) {
      statValue = new StatDoubleValue(values.length);
      StatDoubleValue oldStatValue = map2.putIfAbsent(key, statValue);
      if (oldStatValue != null) {
        statValue = oldStatValue;
      }
    }
    statValue.set(values);
  }

  /**
   * Get and clear metrics
   *
   * @return
   */
  Map<K, long[]> getAndClear() {
    Map<K, long[]> map = Maps.newHashMapWithExpectedSize(this.map.size());
    for (Map.Entry<K, StatValue> e : this.map.entrySet()) {
      map.put(e.getKey(), e.getValue().getAndClear());
    }
    return map;
  }

  Map<K, double[]> getAndClear2() {
    Map<K, double[]> map = Maps.newHashMapWithExpectedSize(map2.size());
    for (Map.Entry<K, StatDoubleValue> e : this.map2.entrySet()) {
      map.put(e.getKey(), e.getValue().getAndClear());
    }
    return map;
  }
}
