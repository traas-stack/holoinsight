/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.state;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

import io.holoinsight.server.agg.v1.core.conf.AggTaskVersion;
import io.holoinsight.server.agg.v1.core.data.AggTaskKey;
import io.holoinsight.server.agg.v1.executor.executor.FixedSizeTags;
import io.holoinsight.server.agg.v1.executor.executor.XAggTask;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * created at 2023/9/16
 *
 * @author xzchaoo
 */
@Data
@NoArgsConstructor
public class AggTaskState {
  /**
   * deletion mark
   */
  transient boolean expired;

  /**
   * agg task key
   */
  private AggTaskKey key;

  /**
   * The maximum event timestamp that has ever been encountered
   */
  private long maxEventTimestamp;

  /**
   * agg window whose ts < watermark should be emitted
   */
  private long watermark;

  /**
   * Agg window state, keyed by time window start
   */
  private TreeMap<Long, AggWindowState> aggWindowStateMap = new TreeMap<>();


  /**
   * This field represents the tags that have appeared in the aggregation task, and its value is the
   * timestamp of the last occurrence of the corresponding tags.
   */
  private Map<FixedSizeTags, Long> historyTags = new HashMap<>();

  /**
   * This field indicates the version of AggTask corresponding to historyTags. Different versions of
   * historyTags may be incompatible.
   */
  private AggTaskVersion historyTagsVersion = new AggTaskVersion();

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

}
