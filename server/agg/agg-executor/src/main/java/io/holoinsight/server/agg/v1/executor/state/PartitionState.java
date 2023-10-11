/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.state;

import java.util.HashMap;
import java.util.Map;

import io.holoinsight.server.agg.v1.core.data.AggTaskKey;
import io.holoinsight.server.agg.v1.executor.state.AggTaskState;
import lombok.Data;
import lombok.Getter;

/**
 * 持有单个 partition 的状态: 持有每个聚合分片的状态
 * <p>
 * created at 2023/9/16
 *
 * @author xzchaoo
 */
@Data
public class PartitionState {
  private int version;
  private long offset;
  private long maxEventTimestamp;

  @Getter
  private Map<AggTaskKey, AggTaskState> aggTaskStates = new HashMap<>();

  public void clear() {
    offset = 0L;
    aggTaskStates.clear();
    maxEventTimestamp = 0;
  }

  public boolean updateMaxEventTimestamp(long timestamp) {
    if (maxEventTimestamp < timestamp) {
      maxEventTimestamp = timestamp;
      return true;
    }
    return false;
  }

  public void put(AggTaskState x) {
    aggTaskStates.put(x.getKey(), x);
  }

}
