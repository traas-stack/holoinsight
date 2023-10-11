/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;

import io.holoinsight.server.agg.v1.core.Utils;
import io.holoinsight.server.agg.v1.core.conf.AggTaskValueTypes;
import io.holoinsight.server.agg.v1.core.data.AggTaskKey;
import io.holoinsight.server.agg.v1.executor.CompletenessService;
import io.holoinsight.server.agg.v1.executor.output.AsyncOutput;
import io.holoinsight.server.agg.v1.executor.service.IAggTaskService;
import io.holoinsight.server.agg.v1.executor.state.AggTaskState;
import io.holoinsight.server.agg.v1.executor.state.OffsetInfo;
import io.holoinsight.server.agg.v1.executor.state.PartitionState;
import io.holoinsight.server.agg.v1.executor.state.PartitionStateStore;
import io.holoinsight.server.agg.v1.executor.state.StateUtils;
import io.holoinsight.server.agg.v1.executor.utils.LastRun;
import io.holoinsight.server.agg.v1.pb.AggProtos;
import lombok.extern.slf4j.Slf4j;

/**
 * 负责一个 partition 数据的消费, 对该实例的所有调用一定是串行的, 因此它不用考虑并发问题
 * <p>
 * created at 2023/9/16
 *
 * @author xzchaoo
 */
@Slf4j
public class PartitionProcessor {
  public static final int EXPECTED_VERSION = 6;

  final TopicPartition partition;

  private final IAggTaskService aggTaskService;
  private final ExecutorConfig config;
  private final CompletenessService completenessService;
  private final AsyncOutput output;
  private final LastRun lastRun;
  PartitionState state = new PartitionState();
  /**
   * The last active time(read event) for this partition
   */
  transient long lastActiveTime;
  /**
   * The last saved MaxEventTimestamp (MET)
   */
  transient long lastSavedOffsetMET;
  transient long minSaveOffset;

  /**
   * Stale latest offset
   */
  volatile long staleLatestOffset;

  private transient Map<AggTaskKey, AggTaskExecutor> aggTaskExecutors = new HashMap<>();

  PartitionProcessor(TopicPartition partition, IAggTaskService aggTaskService,
      ExecutorConfig config, CompletenessService completenessService, AsyncOutput output,
      ExecutorService ioTP) {
    this.partition = Objects.requireNonNull(partition);
    this.aggTaskService = Objects.requireNonNull(aggTaskService);
    this.config = Objects.requireNonNull(config);
    this.completenessService = Objects.requireNonNull(completenessService);
    this.output = Objects.requireNonNull(output);
    lastRun = new LastRun(ioTP);
    state.setVersion(EXPECTED_VERSION);
  }

  private void maybeUpdateTimestamp(long timestamp) {
    if (state.updateMaxEventTimestamp(timestamp)) {
      log.info("[partition] [{}] update ET=[{}]", partition, Utils.formatTime(timestamp));
    }
  }

  void process(List<ConsumerRecord<AggTaskKey, AggProtos.AggTaskValue>> records) {
    long begin = System.currentTimeMillis();
    long oldMaxEventTimestamp = state.getMaxEventTimestamp();
    processRecords(records);
    long newMaxEventTimestamp = state.getMaxEventTimestamp();
    if (newMaxEventTimestamp / 1000 != oldMaxEventTimestamp / 1000) {
      maybeEmit();
    }
    long cost = System.currentTimeMillis() - begin;

    log.info("[partition] [{}] process once records=[{}] cost=[{}] ET=[{}]", //
        partition, records.size(), cost, Utils.formatTime(state.getMaxEventTimestamp()));
  }

  private void processRecords(List<ConsumerRecord<AggTaskKey, AggProtos.AggTaskValue>> records) {
    long partitionMET = state.getMaxEventTimestamp();

    Map<AggTaskKey, List<AggProtos.AggTaskValue>> byKey = new HashMap<>();

    for (ConsumerRecord<AggTaskKey, AggProtos.AggTaskValue> cr : records) {
      state.setOffset(cr.offset());
      AggProtos.AggTaskValue aggTaskValue = cr.value();

      if (aggTaskValue.getType() == AggTaskValueTypes.PUSH_EVENT_TIMESTAMP) {
        partitionMET = Math.max(partitionMET, aggTaskValue.getTimestamp());
        continue;
      }

      byKey.computeIfAbsent(cr.key(), i -> new ArrayList<>()).add(cr.value());
    }

    List<RecursiveTask<Long>> futures = new ArrayList<>(byKey.size());

    for (Map.Entry<AggTaskKey, List<AggProtos.AggTaskValue>> e : byKey.entrySet()) {
      AggTaskKey key = e.getKey();

      XAggTask latestAggTask = aggTaskService.getAggTask(key.getAggId());
      if (latestAggTask == null) {
        continue;
      }

      AggTaskExecutor executor = getOrCreateAggTaskExecutor(key, latestAggTask);
      executor.lastUsedAggTask = latestAggTask;

      RecursiveTask<Long> rt = new RecursiveTask<Long>() {
        @Override
        protected Long compute() {
          for (AggProtos.AggTaskValue aggTaskValue : e.getValue()) {
            executor.process(latestAggTask, aggTaskValue);
          }
          return executor.getState().getMaxEventTimestamp();
        }
      };

      futures.add(rt);
      rt.fork();
    }

    for (RecursiveTask<Long> rt : futures) {
      Long met = rt.join();
      partitionMET = Math.max(partitionMET, met);
    }

    state.updateMaxEventTimestamp(partitionMET);
  }

  private AggTaskExecutor getOrCreateAggTaskExecutor(AggTaskKey key, XAggTask aggTask) {
    AggTaskExecutor e = aggTaskExecutors.get(key);
    if (e == null) {
      AggTaskState s = new AggTaskState(key);
      long watermark = state.getMaxEventTimestamp() //
          - aggTask.getInner().getWindow().getInterval() //
          - config.getFaultToleranceConfig().getMaxOutOfOrderness().toMillis();
      if (watermark < 0) {
        watermark = 0L;
      }
      s.setWatermark(watermark);
      e = new AggTaskExecutor(s, completenessService, output);
      e.ignoredMinWatermark = watermark;
      e.lastUsedAggTask = aggTask;
      aggTaskExecutors.put(key, e);
      state.put(s);
    }
    return e;
  }

  private void maybeEmit() {
    long maxOutOfOrderness = config.getFaultToleranceConfig().getMaxOutOfOrderness().toMillis();
    boolean needRemove = false;
    for (AggTaskExecutor e : aggTaskExecutors.values()) {
      XAggTask lastUsedAggTask = e.lastUsedAggTask;
      if (lastUsedAggTask == null) {
        lastUsedAggTask = aggTaskService.getAggTask(e.key().getAggId());
      }

      if (lastUsedAggTask == null) {
        e.toBeDeleted = true;
        needRemove = true;
        continue;
      }

      long window = lastUsedAggTask.getInner().getWindow().getInterval();
      long watermark = state.getMaxEventTimestamp() //
          - window //
          - maxOutOfOrderness; //

      e.maybeEmit(watermark);

      // If an AggTaskExecutor has no data for a long time, delete it.
      // It will be rebuilt the next time there is data.
      if (e.getState().isEmpty() && e.getState().getMaxEventTimestamp() + 3 * window < watermark) {
        e.toBeDeleted = true;
        needRemove = true;
      }
    }

    if (needRemove) {
      for (Iterator<AggTaskExecutor> iter = aggTaskExecutors.values().iterator(); iter.hasNext();) {
        AggTaskExecutor e = iter.next();
        if (e.toBeDeleted) {
          iter.remove();
          state.getAggTaskStates().remove(e.key());
        }
      }
    }
  }

  void saveState(PartitionStateStore store) throws Exception {
    long begin = System.currentTimeMillis();
    byte[] stateBytes = StateUtils.serialize(state);
    store.saveState(partition, stateBytes);
    long cost = System.currentTimeMillis() - begin;
    log.info("[partition] [{}] [recover] save state successfully, cost=[{}]", partition, cost);
  }

  void loadState(PartitionStateStore store) throws Exception {
    long begin = System.currentTimeMillis();
    byte[] stateBytes = store.loadState(partition);
    if (stateBytes == null) {
      return;
    }

    PartitionState state = StateUtils.deserialize(stateBytes);
    if (state.getVersion() != EXPECTED_VERSION) {
      throw new IllegalStateException(
          "expected state version " + EXPECTED_VERSION + ", but got " + state.getVersion());
    }
    this.state = state;
    for (AggTaskState aggTaskState : state.getAggTaskStates().values()) {
      this.aggTaskExecutors.put(aggTaskState.getKey(),
          new AggTaskExecutor(aggTaskState, completenessService, output));
    }

    long cost = System.currentTimeMillis() - begin;
    log.info("[partition] [{}] load state successfully, cost=[{}]", partition, cost);
  }

  void clearState() {
    state.clear();
  }

  void maybeSaveOffset(FaultToleranceConfig config, PartitionStateStore stateStore, boolean force) {
    // We haven't caught up with the last offset saved yet, so there's no need to save offsets
    // again.
    if (state.getOffset() <= minSaveOffset) {
      return;
    }

    // The new MET must be larger than the old MET by exceeding the threshold before being allowed
    // to be saved.
    if (!force && state.getMaxEventTimestamp() - lastSavedOffsetMET <= config
        .getSaveOffsetsInterval().toMillis()) {
      return;
    }

    lastSavedOffsetMET = state.getMaxEventTimestamp();
    long begin = System.currentTimeMillis();
    OffsetInfo oi = new OffsetInfo();
    oi.setOffset(state.getOffset());
    oi.setMaxEventTimestamp(state.getMaxEventTimestamp());

    Runnable r = () -> {
      try {
        stateStore.saveOffset(partition, oi);
      } catch (Exception e) {
        log.error("[partition] [{}] save offset error", partition, e);
      }
      long cost = System.currentTimeMillis() - begin;
      log.info(
          "[partition] [{}] save offset, MET=[{}] offset=[{}] stale-latest=[{}] lag=[{}] cost=[{}]", //
          partition, Utils.formatTimeShort(state.getMaxEventTimestamp()), state.getOffset(),
          staleLatestOffset, Math.max(0, staleLatestOffset - state.getOffset()), cost);
    };

    if (force) {
      CountDownLatch cdl = new CountDownLatch(1);
      lastRun.add(cdl::countDown);
      try {
        if (cdl.await(3, TimeUnit.SECONDS)) {
          r.run();
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    } else {
      lastRun.add(r);
    }
  }

}
