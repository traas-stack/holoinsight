/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.DisposableBean;

import io.holoinsight.server.agg.v1.core.Utils;
import io.holoinsight.server.agg.v1.core.data.AggKeySerdes;
import io.holoinsight.server.agg.v1.core.data.AggTaskKey;
import io.holoinsight.server.agg.v1.core.data.AggValuesSerdes;
import io.holoinsight.server.agg.v1.executor.AggExecutorConfig;
import io.holoinsight.server.agg.v1.executor.CompletenessService;
import io.holoinsight.server.agg.v1.executor.output.AsyncOutput;
import io.holoinsight.server.agg.v1.executor.service.IAggTaskService;
import io.holoinsight.server.agg.v1.executor.state.PartitionStateStore;
import io.holoinsight.server.agg.v1.pb.AggProtos;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * created at 2023/9/23
 *
 * @author xzchaoo
 */
@Slf4j
public class ExecutorManager implements DisposableBean {
  private final ExecutorConfig config;
  private final List<Executor> executors = new ArrayList<>();
  @Setter
  AggMetaService aggMetaService;
  /**
   * When a partition is idle, ExecutorManager will send a timestamp event to it in order to push
   * event time forward. event time forward
   */
  private KafkaProducer<AggTaskKey, AggProtos.AggTaskValue> kafkaProducer;
  @Setter
  private PartitionStateStore stateStore;
  @Setter
  private IAggTaskService aggTaskService;
  @Setter
  private CompletenessService completenessService;
  @Setter
  private AsyncOutput output;
  @Setter
  private Admin adminClient;

  public ExecutorManager(ExecutorConfig config) {
    this.config = Objects.requireNonNull(config);
  }

  private void init() {
    Properties pp = new Properties();
    pp.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getKafkaBootstrapServers());
    pp.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, AggKeySerdes.S.class.getName());
    pp.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AggValuesSerdes.S.class.getName());
    pp.put(ProducerConfig.BATCH_SIZE_CONFIG, 64 * 1024);
    pp.put(ProducerConfig.LINGER_MS_CONFIG, 100);
    kafkaProducer = new KafkaProducer<>(pp);

    Properties adminP = new Properties();
    adminP.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, config.getKafkaBootstrapServers());
    adminClient = Admin.create(adminP);

    StateConfig stateConfig = config.getFaultToleranceConfig().getStateConfig();
    log.info("[manager] use {} state store", stateConfig.getType());

    // It ExecutorManager is managed by Spring, then we don't need to register a hook
    if (config.getFaultToleranceConfig().isRegisterShutdownHook()) {
      Runtime.getRuntime().addShutdownHook(new ShutdownHook());
    }
  }

  public void stop() throws InterruptedException {
    for (Executor executor : executors) {
      executor.markStopped();
    }

    for (Executor executor : executors) {
      try {
        executor.stop(2, TimeUnit.SECONDS);
      } catch (Exception e) {
        log.error("[manager] executor stop error", e);
      }
    }

    kafkaProducer.close();
    adminClient.close();
    try {
      stateStore.close();
    } catch (IOException e) {
      log.error("[manager] close state store", e);
    }
  }

  public void run() {
    init();

    // computingTP is used for concurrent processing of data per partition
    // If the number of partitions that the local machine is responsible for exceeds the size of
    // computingTP, queuing may occur.
    ForkJoinPool computingTP =
        Utils.createForkJoinPool("agg-computing", config.getComputingThreadPoolSize());
    ExecutorService ioTP = Utils.createThreadPool("agg-io", config.getIoThreadPoolSize());

    if (config.getExecutorCount() <= 1) {
      // run in current thread
      Executor executor = new Executor(0, config, stateStore, computingTP, kafkaProducer,
          aggTaskService, completenessService, output, ioTP, adminClient, aggMetaService);
      executors.add(executor);
      executor.run();
    } else {
      ExecutorService executorTP =
          Utils.createThreadPool("agg-executor", config.getExecutorCount());
      List<Future<?>> futures = new ArrayList<>(config.getExecutorCount());

      for (int i = 0; i < config.getExecutorCount(); i++) {
        Executor executor = new Executor(i, config, stateStore, computingTP, kafkaProducer,
            aggTaskService, completenessService, output, ioTP, adminClient, aggMetaService);
        executors.add(executor);
        futures.add(executorTP.submit(executor::run));
      }

      for (Future<?> future : futures) {
        try {
          future.get();
        } catch (InterruptedException e) {
          log.error("wait future, but interrupted", e);
          Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
          log.error("wait future error", e);
        }
      }

      executorTP.shutdown();
    }

    computingTP.shutdown();
  }

  @Override
  public void destroy() throws Exception {
    this.stop();
  }

  private class ShutdownHook extends Thread {
    @Override
    public void run() {
      try {
        ExecutorManager.this.stop();
        log.info("shutdown successfully");
      } catch (InterruptedException e) {
        log.warn("shutdown interrupted");
        Thread.currentThread().interrupt();
      } catch (Exception e) {
        log.error("shutdown error", e);
      }
    }
  }
}
