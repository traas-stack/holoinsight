/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.state;

import java.util.HashMap;
import java.util.Map;

import io.holoinsight.server.agg.v1.core.data.AggTaskKey;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * State of kafka topic partition
 * <p>
 * created at 2023/9/16
 *
 * @author xzchaoo
 */
@Data
public class PartitionState {

  /**
   * Version of this state. If it is different with expected version, then we should discard the
   * state.
   */
  private int version;

  /**
   * The last offset this state has consumed.
   */
  private long offset;

  /**
   * The maximum event timestamp that has ever been encountered
   */
  private long maxEventTimestamp;

  /**
   * Agg task states in this partition.
   */
  @Setter(AccessLevel.NONE)
  private Map<AggTaskKey, AggTaskState> aggTaskStates = new HashMap<>();

  /**
   * If restoredOffset is greater than 0, it means that the state is initially restored from this
   * restoredOffset. Only when the {@link #offset} exceeds this value will the state be allowed to
   * be saved again, otherwise it will be meaningless.
   */
  private transient long restoredOffset;


  public void clear() {
    offset = 0L;
    aggTaskStates.clear();
    maxEventTimestamp = 0;
    restoredOffset = 0;
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
