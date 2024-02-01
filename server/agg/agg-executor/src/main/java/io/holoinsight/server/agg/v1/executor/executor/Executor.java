/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.ListOffsetsResult;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

import io.holoinsight.server.agg.v1.core.Utils;
import io.holoinsight.server.agg.v1.core.conf.AggTaskValueTypes;
import io.holoinsight.server.agg.v1.core.data.AggKeySerdes;
import io.holoinsight.server.agg.v1.core.data.AggTaskKey;
import io.holoinsight.server.agg.v1.core.data.AggValuesSerdes;
import io.holoinsight.server.agg.v1.executor.CompletenessService;
import io.holoinsight.server.agg.v1.executor.output.AsyncOutput;
import io.holoinsight.server.agg.v1.executor.service.IAggTaskService;
import io.holoinsight.server.agg.v1.executor.state.OffsetInfo;
import io.holoinsight.server.agg.v1.executor.state.PartitionStateStore;
import io.holoinsight.server.agg.v1.executor.utils.LastRun;
import io.holoinsight.server.agg.v1.pb.AggProtos;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * created at 2023/9/16
 *
 * @author xzchaoo
 */
@Slf4j
public class Executor {
  private static final int ERROR_SLEEP = 1000;
  private static final Duration POLL_TIMEOUT = Duration.ofSeconds(3);
  private static final Duration MAX_WINDOW = Duration.ofMinutes(1);
  private static final long UPDATE_LATEST_OFFSET_INTERVAL = 10_000L;

  private final int index;
  private final ExecutorConfig config;
  private final Map<TopicPartition, PartitionProcessor> partitionProcessorMap = new HashMap<>();
  private final PartitionStateStore stateStore;
  private final ForkJoinPool threadpool;
  private final ExecutorService ioTP;
  private final KafkaProducer<AggTaskKey, AggProtos.AggTaskValue> kafkaProducer;
  private final IAggTaskService aggTaskService;
  private final CompletenessService completenessService;
  private final AsyncOutput output;
  private final LastRun saveOffsetsLastRun;
  private final Admin adminClient;
  private final AggMetaService aggMetaService;

  private final CountDownLatch stopCDL = new CountDownLatch(1);
  private volatile boolean stopped;

  private KafkaConsumer<AggTaskKey, AggProtos.AggTaskValue> kafkaConsumer;
  private transient long lastSaveStateTime = System.currentTimeMillis();
  private transient long lastUpdateLatestOffsetsTime;

  Executor(int index, ExecutorConfig config, PartitionStateStore stateStore,
      ForkJoinPool threadpool, KafkaProducer<AggTaskKey, AggProtos.AggTaskValue> kafkaProducer,
      IAggTaskService aggTaskService, CompletenessService completenessService, AsyncOutput output,
      ExecutorService ioTP, Admin adminClient, AggMetaService aggMetaService) {
    this.index = index;
    this.config = Objects.requireNonNull(config);
    this.stateStore = Objects.requireNonNull(stateStore);
    this.threadpool = Objects.requireNonNull(threadpool);
    this.kafkaProducer = Objects.requireNonNull(kafkaProducer);
    this.aggTaskService = Objects.requireNonNull(aggTaskService);
    this.completenessService = Objects.requireNonNull(completenessService);
    this.output = Objects.requireNonNull(output);
    this.ioTP = Objects.requireNonNull(ioTP);
    this.adminClient = Objects.requireNonNull(adminClient);
    this.saveOffsetsLastRun = new LastRun(ioTP);
    this.aggMetaService = Objects.requireNonNull(aggMetaService);
  }

  private void loop() {
    kafkaConsumer = createKafkaConsumer();
    log.info("[executor] [{}] subscribe topic {}", index, config.getTopic());
    kafkaConsumer.subscribe(Collections.singletonList(config.getTopic()),
        new ExecutorConsumerRebalanceListener());

    while (!stopped) {
      try {
        loop0();

        maybePushEventTimes();
        maybeSaveOffset();
        maybeSaveState();
        maybeUpdateLastOffsets();
      } catch (Exception e) {
        if (e instanceof WakeupException) {
          break;
        }

        // unexpected error
        log.error("[executor] [{}] computing executor loop met an unexpected error", index, e);
        try {
          Thread.sleep(ERROR_SLEEP);
        } catch (InterruptedException ex) {
          Thread.currentThread().interrupt();
          break;
        }
      }
    }

    try {
      kafkaConsumer.close();
    } catch (Exception e) {
      log.error("[executor] [{}] close kafka consumer error", index, e);
    }
  }

  private void maybeUpdateLastOffsets() {
    long now = System.currentTimeMillis();
    if (this.lastUpdateLatestOffsetsTime + UPDATE_LATEST_OFFSET_INTERVAL < now) {
      this.lastUpdateLatestOffsetsTime = now;
      saveOffsetsLastRun.add(this::updateLatestOffsets);
    }
  }

  /**
   * Save consumption location information, which can be used for subsequent recalculation
   */
  private void maybeSaveOffset() {
    for (PartitionProcessor pp : this.partitionProcessorMap.values()) {
      pp.maybeSaveOffset(config.getFaultToleranceConfig(), stateStore, false);
    }
  }

  /**
   * Save state info for subsequent recovery
   */
  private void maybeSaveState() {
    FaultToleranceConfig faultToleranceConfig = config.getFaultToleranceConfig();

    long now = System.currentTimeMillis();
    if (lastSaveStateTime + faultToleranceConfig.getSaveStateInterval().toMillis() >= now) {
      return;
    }
    lastSaveStateTime = now;

    for (PartitionProcessor pp : this.partitionProcessorMap.values()) {
      try {
        pp.saveState(stateStore);
      } catch (Exception e) {
        log.error("[partition] [{}] save state error", pp.partition, e);
      }
    }

    long cost = System.currentTimeMillis() - now;
    log.info("[executor] [{}] save state successfully, cost=[{}]", index, cost);
  }

  /**
   * The processing of each partition is event time driven. We expect that each partition has
   * received a message within the latest idleTimeout. If no message is received, it is probably
   * because there is no message or there is a fault (disregarding such situations for now). At this
   * time we need to go to the partition Post a message to move event time forward.
   */
  private void maybePushEventTimes() {
    long now = System.currentTimeMillis();
    long minActiveTime = now - config.getIdleTimeout().toMillis();

    AggProtos.AggTaskValue timestampValue = AggProtos.AggTaskValue.newBuilder() //
        .setType(AggTaskValueTypes.PUSH_EVENT_TIMESTAMP) //
        .setTimestamp(now) //
        .build(); //

    for (PartitionProcessor pp : this.partitionProcessorMap.values()) {
      if (pp.isIdle(now, minActiveTime)) {
        log.info("[partition] [{}] push ET=[{}]", pp.partition, Utils.formatTime(now));
        kafkaProducer.send(new ProducerRecord<>(pp.partition.topic(), pp.partition.partition(),
            null, timestampValue));
      }
    }
  }

  private void loop0() {
    long time0 = System.currentTimeMillis();
    ConsumerRecords<AggTaskKey, AggProtos.AggTaskValue> crs = kafkaConsumer.poll(POLL_TIMEOUT);
    if (crs.isEmpty()) {
      return;
    }
    long time1 = System.currentTimeMillis();


    Set<TopicPartition> partitions = crs.partitions();
    List<Callable<Void>> tasks = new ArrayList<>(partitions.size());
    for (TopicPartition partition : partitions) {
      PartitionProcessor pp = partitionProcessorMap.get(partition);
      pp.touch(time1);
      tasks.add(() -> {
        pp.process(crs.records(partition));
        return null;
      });
    }
    threadpool.invokeAll(tasks);


    long time2 = System.currentTimeMillis();
    log.info("[executor] [{}] loop once count=[{}] pullCost=[{}] processCost=[{}]", //
        index, crs.count(), time1 - time0, time2 - time1);
  }

  private KafkaConsumer<AggTaskKey, AggProtos.AggTaskValue> createKafkaConsumer() {
    Properties properties = new Properties(config.getConsumerProperties());
    properties.putAll(config.getConsumerProperties());

    properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getKafkaBootstrapServers());
    properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, AggKeySerdes.D.class.getName());
    properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        AggValuesSerdes.D.class.getName());
    properties.put(ConsumerConfig.GROUP_ID_CONFIG, config.getGroupId());
    properties.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
        CooperativeStickyAssignor.class.getName());
    properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
    properties.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 3_000);
    properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 12_000);//
    properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 5000);
    properties.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, 50 * 1024 * 1024);
    properties.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 10 * 1024 * 1024);
    properties.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 10 * 1024 * 1024);
    properties.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 1000);
    properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

    return new KafkaConsumer<>(properties);
  }

  public void run() {
    try {
      loop();
    } finally {
      stopped = true;
      stopCDL.countDown();
    }
  }

  public void markStopped() {
    this.stopped = true;
  }

  public boolean stop(int timeout, TimeUnit unit) throws InterruptedException {
    this.stopped = true;
    if (kafkaConsumer != null) {
      kafkaConsumer.wakeup();
    }
    if (stopCDL.await(timeout, unit)) {
      log.info("[executor] [{}] shutdown successfully", index);
      return true;
    } else {
      log.info("[executor] [{}] shutdown error", index);
    }
    return false;
  }

  private RecomputingOffsets findRecomputingOffsets(TopicPartition partition, long mustCoverOffset)
      throws Exception {
    List<OffsetInfo> offsetInfos = stateStore.loadOffsets(partition) //
        .stream() //
        .sorted(Comparator.comparingLong(OffsetInfo::getOffset).reversed()) //
        .collect(Collectors.toList());

    if (offsetInfos.size() < 2) {
      return null;
    }
    OffsetInfo last = offsetInfos.get(0);

    long minWindow = last.getMaxEventTimestamp() //
        - MAX_WINDOW.toMillis() //
        - config.getFaultToleranceConfig().getMaxOutOfOrderness().toMillis(); //

    for (int i = 1; i < offsetInfos.size(); i++) {
      OffsetInfo oi = offsetInfos.get(i);
      if (oi.getMaxEventTimestamp() < minWindow
          && (mustCoverOffset <= 0 || oi.getOffset() < mustCoverOffset)) {
        return new RecomputingOffsets(last, oi);
      }
    }
    return null;
  }

  private void maybeRecoverState(PartitionProcessor pp) {
    try {
      pp.loadState(stateStore);
    } catch (Exception e) {
      pp.clearState();
      log.error("[partition] [{}] [recover] load state error", pp.partition, e);
    }
  }

  private boolean recoverByRecomputing(PartitionProcessor pp, Map<TopicPartition, Long> endOffsets,
      long mustCoverOffset) {
    TopicPartition partition = pp.partition;

    RecomputingOffsets ro = null;
    try {
      ro = findRecomputingOffsets(partition, mustCoverOffset);
    } catch (Exception e) {
      log.error("[partition] [{}] [recover] findRecomputingOffsets error", partition, e);
    }

    if (ro == null) {
      log.error(
          "[partition] [{}] [recover] no good offset for recomputing, will seek to end, mustCoverOffset=[{}]",
          partition, mustCoverOffset);
      return false;
    }

    // ro.last.getOffset() is the last offset we have consumed.
    // ro.start.getOffset() + 1 is the next offset we want to consume.
    // so we seek to ro.start.getOffset() + 1
    kafkaConsumer.seek(partition, ro.getStart().getOffset() + 1);
    long latest = endOffsets.get(partition);
    log.info(
        "[partition] [{}] [recover] prepare recomputing last=[{},{}] seek=[{},{}] latest=[{}] lag=[{}]",
        partition, //
        ro.getLast().getOffset(), Utils.formatTimeShort(ro.getLast().getMaxEventTimestamp()), //
        ro.getStart().getOffset(), Utils.formatTimeShort(ro.getStart().getMaxEventTimestamp()), //
        latest, //
        latest - ro.start.getOffset());

    pp.state.setOffset(ro.start.getOffset());
    pp.minSaveOffset = ro.last.getOffset();
    pp.state.setMaxEventTimestamp(ro.last.getMaxEventTimestamp());
    pp.lastSavedOffsetMET = ro.last.getMaxEventTimestamp();

    return true;
  }

  private void updateLatestOffsets() {
    Map<TopicPartition, OffsetSpec> request = new HashMap<>();
    for (TopicPartition e : partitionProcessorMap.keySet()) {
      request.put(e, OffsetSpec.latest());
    }
    if (request.isEmpty()) {
      return;
    }

    try {
      Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> result =
          adminClient.listOffsets(request).all().get();
      result.forEach((partition, info) -> {
        PartitionProcessor pp = partitionProcessorMap.get(partition);
        if (pp != null) {
          pp.staleLatestOffset = info.offset();
        }
      });
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (ExecutionException e) {
      log.error("[executor] [{}] updateLatestOffsets error", index, e);
    }
  }

  private class ExecutorConsumerRebalanceListener implements ConsumerRebalanceListener {
    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
      onPartitionsRevoked0(partitions, true);
    }

    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
      if (partitions.isEmpty()) {
        return;
      }
      long begin = System.currentTimeMillis();
      onPartitionsAssigned0(partitions);
      long cost = System.currentTimeMillis() - begin;
      log.info("[executor] [{}] onPartitionsAssigned partitions={} cost=[{}]", //
          index, partitions, cost);
    }

    @Override
    public void onPartitionsLost(Collection<TopicPartition> partitions) {
      onPartitionsRevoked0(partitions, false);
    }

    private void onPartitionsRevoked0(Collection<TopicPartition> partitions, boolean revoked) {
      long begin = System.currentTimeMillis();

      for (TopicPartition partition : partitions) {
        PartitionProcessor pp = partitionProcessorMap.remove(partition);
        if (pp == null) {
          log.error(
              "[executor] [{}] no PartitionProcessor for partition {} when onPartitionsRevoked",
              index, partition);
          continue;
        }
        if (revoked) {
          try {
            pp.maybeSaveOffset(config.getFaultToleranceConfig(), stateStore, true);
          } catch (Exception e) {
            log.error("[partition] [{}] save offset error", partition, e);
          }

          try {
            pp.saveState(stateStore);
          } catch (Exception e) {
            log.error("[partition] [{}] save state error", partition, e);
          }
        }
      }

      long cost = System.currentTimeMillis() - begin;
      log.info("[executor] [{}] onPartitions{} partitions={} cost=[{}]", //
          index, revoked ? "Revoked" : "Lost", partitions, cost);
    }


    private void onPartitionsAssigned0(Collection<TopicPartition> partitions) {
      Map<TopicPartition, Long> beginningOffsets = kafkaConsumer.beginningOffsets(partitions);
      Map<TopicPartition, Long> endOffsets = kafkaConsumer.endOffsets(partitions);

      // for PERFORMANCE TEST
      boolean forceBegin = "1".equals(System.getenv("FORCE_BEGIN"));

      List<TopicPartition> seekToBeginnings = new ArrayList<>();
      List<TopicPartition> seekToEnds = new ArrayList<>();

      for (TopicPartition partition : partitions) {
        PartitionProcessor pp = partitionProcessorMap.remove(partition);
        if (pp != null) {
          log.error("[executor] [{}] [partition] [{}] duplicate assignment", index, partition);
        }

        pp = new PartitionProcessor( //
            partition, aggTaskService, config, completenessService, output, ioTP, aggMetaService);
        partitionProcessorMap.put(partition, pp);

        if (forceBegin) {
          log.info("[partition] [{}] [recover] force seek to beginning", partition);
          seekToBeginnings.add(partition);
          continue;
        }

        maybeRecoverState(pp);

        if (pp.state.getRestoredOffset() > 0) {
          long restored = pp.state.getRestoredOffset();
          Long begin = beginningOffsets.get(partition);
          if (begin != null && restored < begin) {
            log.info("[partition] [{}] clear expired restored state, restored=[{}] begin=[{}]", //
                partition, restored, begin);
            pp.state.clear();
          }

          restored = pp.state.getRestoredOffset();
          Long end = endOffsets.get(partition);
          if (restored > 0 && end != null && restored > end) {
            log.info("[partition] [{}] clear expired restored state, restored=[{}] end=[{}]", //
                partition, restored, end);
            pp.state.clear();
          }
        }

        if (!recoverByRecomputing(pp, endOffsets, pp.state.getRestoredOffset())) {
          pp.clearState();
          seekToEnds.add(pp.partition);
        }
      }

      if (!seekToEnds.isEmpty()) {
        kafkaConsumer.seekToEnd(seekToEnds);
      }
      if (!seekToBeginnings.isEmpty()) {
        kafkaConsumer.seekToBeginning(seekToBeginnings);
      }
    }
  }
}
