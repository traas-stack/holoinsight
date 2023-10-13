/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.state;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

import io.holoinsight.server.agg.v1.core.data.AggTaskKey;
import io.holoinsight.server.agg.v1.core.data.PartitionKeysUtils;
import io.holoinsight.server.agg.v1.executor.executor.XAggTask;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * created at 2023/9/16
 *
 * @author xzchaoo
 */
@Data
public class AggTaskState {
  AggTaskKey key;
  /**
   * deletion mark
   */
  transient boolean expired;
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private transient Map<String, String> partitionKeys;

  /**
   * agg window whose ts < watermark should be emitted
   */
  private long watermark;

  private Map<Long, AggWindowState> aggWindowStateMap = new TreeMap<>();

  private long maxEventTimestamp;

  public AggTaskState() {}

  public AggTaskState(AggTaskKey key) {
    this.key = key;
  }

  public AggWindowState getAggWindowState(long timestamp) {
    return aggWindowStateMap.get(timestamp);
  }

  public AggWindowState getOrCreateAggWindowState(long timestamp, XAggTask aggTask,
      Consumer<AggWindowState> callback) {
    AggWindowState ws = aggWindowStateMap.get(timestamp);
    if (ws == null) {
      ws = new AggWindowState();
      ws.setTimestamp(timestamp);
      ws.setAggTask(aggTask);
      aggWindowStateMap.put(timestamp, ws);
      if (callback != null) {
        callback.accept(ws);
      }
    }
    return ws;
  }

  public boolean isEmpty() {
    return aggWindowStateMap.isEmpty();
  }

  public void markExpired() {
    expired = true;
  }

  public void updateMaxEventTimestamp(long timestamp) {
    if (maxEventTimestamp < timestamp) {
      maxEventTimestamp = timestamp;
    }
  }

  public Iterator<AggWindowState> getAggWindowStateIter() {
    return aggWindowStateMap.values().iterator();
  }

  public void reuseStrings() {
    if (aggWindowStateMap != null) {
      aggWindowStateMap.values().forEach(AggWindowState::reuseStrings);
    }
  }

  public void put(AggWindowState x) {
    aggWindowStateMap.put(x.getTimestamp(), x);
  }

  public Map<String, String> partitionKeys() {
    if (partitionKeys == null) {
      if (key.getPartition().isEmpty()) {
        partitionKeys = Collections.emptyMap();
      } else {
        partitionKeys = PartitionKeysUtils.decode(key.getPartition());
      }
    }
    return partitionKeys;
  }
}