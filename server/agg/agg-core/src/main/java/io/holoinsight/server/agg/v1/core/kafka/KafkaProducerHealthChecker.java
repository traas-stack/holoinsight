/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core.kafka;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.KafkaProducer;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * created at 2023/11/2
 *
 * @author xzchaoo
 */
@Slf4j
public class KafkaProducerHealthChecker {
  private static final int UNHEALTHY_CHECK_INTERVAL_MILLIS = 100;

  private final KafkaProducer<?, ?> kp;
  private final String testTopic;
  private final Duration interval;
  private final ScheduledExecutorService scheduler;
  private final Executor ioExecutor;
  @Getter
  private volatile boolean healthy = true;
  private volatile boolean stopped;

  public KafkaProducerHealthChecker(KafkaProducer<?, ?> kp, String testTopic, Duration interval,
      ScheduledExecutorService scheduler) {
    this(kp, testTopic, interval, scheduler, scheduler);
  }

  public KafkaProducerHealthChecker(KafkaProducer<?, ?> kp, String testTopic, Duration interval,
      ScheduledExecutorService scheduler, Executor ioExecutor) {
    this.kp = kp;
    this.testTopic = testTopic;
    this.interval = interval;
    this.scheduler = scheduler;
    this.ioExecutor = ioExecutor;
  }

  public void start() {
    scheduleNext();
  }

  private void scheduleNext() {
    if (stopped) {
      return;
    }

    try {
      if (healthy) {
        scheduler.schedule(this::checkOnce, interval.toMillis(), TimeUnit.MILLISECONDS);
      } else {
        scheduler.schedule(this::checkOnce, UNHEALTHY_CHECK_INTERVAL_MILLIS, TimeUnit.MILLISECONDS);
      }
    } catch (RejectedExecutionException e) {
      log.error("schedule rejected", e);
    }
  }

  private void checkOnce() {
    if (stopped) {
      return;
    }

    if (scheduler == ioExecutor) {
      checkOnce0();
      scheduleNext();
    } else {
      ioExecutor.execute(() -> {
        checkOnce0();
        scheduleNext();
      });
    }
  }

  private void checkOnce0() {
    long begin = System.currentTimeMillis();
    try {
      // When kafka is unhealthy, the partitionsFor method blocks for
      // ProducerConfig.MAX_BLOCK_MS_CONFIG millis.
      int partitions = kp.partitionsFor(testTopic).size();
      long cost = System.currentTimeMillis() - begin;
      healthy = true;
      log.info("kafka producer healthcheck success testTopic=[{}] partitions=[{}] cost=[{}]", //
          testTopic, partitions, cost);
    } catch (Exception e) {
      healthy = false;
      long cost = System.currentTimeMillis() - begin;
      log.error("kafka producer healthcheck error, testTopic=[{}] cost=[{}]", testTopic, cost, e);
    }
  }

  public void stop() {
    stopped = true;
  }
}
