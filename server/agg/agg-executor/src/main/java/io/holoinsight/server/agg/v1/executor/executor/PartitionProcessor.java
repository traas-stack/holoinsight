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
  private final AggMetaService aggMetaService;
  private final LastRun lastRun;
  PartitionState state = new PartitionState();
  /**
   * The last saved MaxEventTimestamp (MET)
   */
  transient long lastSavedOffsetMET;
  transient long minSaveOffset;
  /**
   * Stale latest offset
   */
  volatile long staleLatestOffset;

  /**
   * The last active time(read event) for this partition
   */
  private transient long lastActiveTime;

  private transient Map<AggTaskKey, AggTaskExecutor> aggTaskExecutors = new HashMap<>();

  PartitionProcessor(TopicPartition partition, IAggTaskService aggTaskService,
      ExecutorConfig config, CompletenessService completenessService, AsyncOutput output,
      ExecutorService ioTP, AggMetaService aggMetaService) {
    this.partition = Objects.requireNonNull(partition);
    this.aggTaskService = Objects.requireNonNull(aggTaskService);
    this.config = Objects.requireNonNull(config);
    this.completenessService = Objects.requireNonNull(completenessService);
    this.output = Objects.requireNonNull(output);
    this.aggMetaService = Objects.requireNonNull(aggMetaService);
    lastRun = new LastRun(ioTP);
    state.setVersion(EXPECTED_VERSION);
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

    Map<AggTaskKey, List<ConsumerRecord<AggTaskKey, AggProtos.AggTaskValue>>> byKey =
        new HashMap<>();

    for (ConsumerRecord<AggTaskKey, AggProtos.AggTaskValue> cr : records) {
      state.setOffset(cr.offset());
      AggProtos.AggTaskValue aggTaskValue = cr.value();

      if (aggTaskValue.getType() == AggTaskValueTypes.PUSH_EVENT_TIMESTAMP) {
        partitionMET = Math.max(partitionMET, aggTaskValue.getTimestamp());
        continue;
      }

      byKey.computeIfAbsent(cr.key(), i -> new ArrayList<>()).add(cr);
    }

    List<RecursiveTask<Long>> futures = new ArrayList<>(byKey.size());

    for (Map.Entry<AggTaskKey, List<ConsumerRecord<AggTaskKey, AggProtos.AggTaskValue>>> e : byKey
        .entrySet()) {
      AggTaskKey key = e.getKey();

      XAggTask latestAggTask = aggTaskService.getAggTask(key.getAggId());
      if (latestAggTask == null) {
        continue;
      }

      AggTaskExecutor executor = getOrCreateAggTaskExecutor(key, latestAggTask);

      RecursiveTask<Long> rt = new RecursiveTask<Long>() {
        @Override
        protected Long compute() {
          boolean processed = false;
          for (ConsumerRecord<AggTaskKey, AggProtos.AggTaskValue> cr : e.getValue()) {
            // If this AggTaskExecutor is recovered by state, we should ignore offsets that have
            // offset <= restoredOffset
            if (cr.offset() <= executor.restoredOffset) {
              continue;
            }
            processed = true;
            executor.process(latestAggTask, cr.value());
          }

          // If all offsets are <= executor.ignoreMinOffset, it means that it is in the recomputing
          // process at this time.
          // We should not return our max event timestamp because it will cause the MET value on the
          // partition to be larger.
          if (!processed) {
            return 0L;
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

  private void removeAggTaskExecutor(AggTaskKey key) {
    aggTaskExecutors.remove(key);
    state.getAggTaskStates().remove(key);
  }

  private AggTaskExecutor getOrCreateAggTaskExecutor(AggTaskKey key, XAggTask aggTask) {
    AggTaskExecutor e = aggTaskExecutors.get(key);

    // When an AggTask update is found, discard the intermediate calculation results of
    // the current cycle. Doing so allows you to immediately update the day-level tasks instead of
    // waiting until the next day to take effect. But the disadvantage is that the intermediate
    // state will be lost and the data will be calculated from zero.
    if (e != null && e.lastUsedAggTask != null
        && aggTask.getInner().getExtension().isDiscardWhenUpdate()
        && !e.lastUsedAggTask.hasSameVersion(aggTask)) {
      removeAggTaskExecutor(key);
      e = null;
    }

    if (e == null) {
      AggTaskState s = new AggTaskState(key);
      long watermark = state.getMaxEventTimestamp() //
          - aggTask.getInner().getWindow().getInterval() //
          - config.getFaultToleranceConfig().getMaxOutOfOrderness().toMillis();
      if (watermark < 0) {
        watermark = 0L;
      }
      s.setWatermark(watermark);
      e = new AggTaskExecutor(s, completenessService, output, aggMetaService);
      e.ignoredMinWatermark = watermark;
      aggTaskExecutors.put(key, e);
      state.put(s);
    }
    e.lastUsedAggTask = aggTask;
    return e;
  }

  private void maybeEmit() {
    long maxOutOfOrderness = config.getFaultToleranceConfig().getMaxOutOfOrderness().toMillis();
    boolean needRemove = false;
    for (AggTaskExecutor e : aggTaskExecutors.values()) {
      XAggTask lastUsedAggTask = e.lastUsedAggTask;
      if (lastUsedAggTask == null) {
        lastUsedAggTask = aggTaskService.getAggTask(e.key().getAggId());
        e.lastUsedAggTask = lastUsedAggTask;
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
    if (state.getOffset() <= state.getRestoredOffset()) {
      // no need to save this state
      // During the recalculation phase, there may be situations where offset <= resetredOffset
      return;
    }

    long begin = System.currentTimeMillis();
    PartitionState persistentState = buildPersistentState();
    byte[] stateBytes = StateUtils.serialize(persistentState);
    store.saveState(partition, stateBytes);
    long cost = System.currentTimeMillis() - begin;
    log.info("[partition] [{}] [recover] save state successfully, cost=[{}]", partition, cost);
  }

  /**
   * Build a state that needs to be persisted.
   * 
   * @return
   */
  private PartitionState buildPersistentState() {
    PartitionState ps = new PartitionState();
    ps.setVersion(state.getVersion());
    ps.setOffset(state.getOffset());
    ps.setMaxEventTimestamp(state.getMaxEventTimestamp());

    // Only AggTasks with "state.enabled" set to true need to be persistent.
    for (AggTaskState ats : state.getAggTaskStates().values()) {
      XAggTask aggTask = aggTaskService.getAggTask(ats.getKey().getAggId());
      if (aggTask == null) {
        continue;
      }
      if (aggTask.getInner().getState().isEnabled()) {
        log.info("[partition] [{}] save agg task state {}", partition, ats.getKey());
        ps.getAggTaskStates().put(ats.getKey(), ats);
      }
    }
    return ps;
  }

  void loadState(PartitionStateStore store) throws Exception {
    long begin = System.currentTimeMillis();
    byte[] stateBytes = store.loadState(partition);
    if (stateBytes == null) {
      return;
    }

    PartitionState restored = StateUtils.deserialize(stateBytes);
    if (restored.getVersion() != EXPECTED_VERSION) {
      log.info("[partition] [{}] discard old restored with version=[{}], expectedVersion=[{}]", //
          partition, restored.getVersion(), EXPECTED_VERSION);
      return;
    }

    state.setRestoredOffset(restored.getOffset());
    for (AggTaskState ats : restored.getAggTaskStates().values()) {
      log.info("[partition] [{}] load agg task restored {} watermark=[{}]", partition, ats.getKey(),
          Utils.formatTime(ats.getWatermark()));
      AggTaskExecutor e = new AggTaskExecutor(ats, completenessService, output, aggMetaService);
      e.restoredOffset = restored.getOffset();
      aggTaskExecutors.put(ats.getKey(), e);
      state.put(ats);
    }

    // There is no need to update state.offset and state.maxEventTimestamp, they will be
    // automatically updated to reasonable values using the recalculation mechanism.

    long cost = System.currentTimeMillis() - begin;
    log.info("[partition] [{}] load restored successfully, cost=[{}], aggTaskStates=[{}] MET=[{}]",
        partition, cost, restored.getAggTaskStates().size(),
        Utils.formatTime(state.getMaxEventTimestamp()));
  }

  void clearState() {
    state.clear();
    aggTaskExecutors.clear();
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
    OffsetInfo oi = new OffsetInfo(state.getOffset(), state.getMaxEventTimestamp());

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

  /**
   * Check if this partition processor is idle
   * 
   * @param now
   * @param minActiveTime
   * @return
   */
  boolean isIdle(long now, long minActiveTime) {
    if (lastActiveTime < minActiveTime) {
      lastActiveTime = now;
      return true;
    }
    return false;
  }

  /**
   * update last active time
   * 
   * @param t
   */
  void touch(long t) {
    lastActiveTime = t;
  }
}
