/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.output;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.beans.factory.annotation.Autowired;

import io.holoinsight.server.agg.v1.core.AggProperties;
import io.holoinsight.server.agg.v1.core.Utils;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * created at 2023/10/3
 *
 * @author xzchaoo
 */
@Slf4j
public class AsyncOutput {
  private static final int MAX_REQUEST_SIZE = 10 * 1024 * 1024;
  private static final Duration POLL_TIMEOUT = Duration.ofSeconds(2);
  private final CountDownLatch cdl = new CountDownLatch(1);
  private final XConsoleOutput consoleOutput = new XConsoleOutput();
  private KafkaConsumer<Integer, XOutput.Batch> consumer;
  private KafkaProducer<Integer, XOutput.Batch> producer;
  private volatile boolean stopped;
  @Autowired
  private AggProperties aggProperties;
  private String outputTopic;
  @Autowired(required = false)
  private List<XOutput> outputs = new ArrayList<>();
  private transient Map<String, XOutput> outputMap = new HashMap<>();

  public AsyncOutput() {}

  @PostConstruct
  public void start() {
    if (aggProperties.isDebugOutput()) {
      return;
    }

    for (XOutput out : outputs) {
      outputMap.put(out.type(), out);
    }

    Properties pp = new Properties();
    pp.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, aggProperties.getKafkaBootstrapServers());
    pp.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, Serdes.Integer().serializer().getClass());
    pp.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, BatchSerdes.S.class);
    pp.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");
    pp.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, MAX_REQUEST_SIZE);

    Properties cp = new Properties();
    cp.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, aggProperties.getKafkaBootstrapServers());
    cp.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        Serdes.Integer().deserializer().getClass());
    cp.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, BatchSerdes.D.class);
    cp.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
        CooperativeStickyAssignor.class.getName());
    cp.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
    cp.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 1000);
    cp.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
    cp.put(ConsumerConfig.GROUP_ID_CONFIG, aggProperties.getConsumerGroupId());
    String rack = System.getenv().getOrDefault("holoinsight.env.location", "");
    cp.put(ConsumerConfig.CLIENT_RACK_CONFIG, rack);

    producer = new KafkaProducer<>(pp);
    consumer = new KafkaConsumer<>(cp);

    outputTopic = aggProperties.getTopic() + "_output";
    consumer.subscribe(Collections.singletonList(outputTopic));
    log.info("[output] subscribe to {}", outputTopic);

    new Thread(this::consumerLoop, "agg-output-consumer").start();
  }

  @PreDestroy
  public void stop() {
    if (aggProperties.isDebugOutput()) {
      return;
    }

    this.stopped = true;
    consumer.wakeup();

    try {
      cdl.await(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
    }

    consumer.close();
    producer.close();
  }

  private void consumerLoop() {
    while (!stopped) {
      try {
        consumerPoll0();
      } catch (WakeupException e) {
        break;
      } catch (Exception e) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException ex) {
          Thread.currentThread().interrupt();
          break;
        }
      }
    }

    cdl.countDown();
  }

  private void consumerPoll0() {
    long time0 = System.currentTimeMillis();
    ConsumerRecords<Integer, XOutput.Batch> crs = consumer.poll(POLL_TIMEOUT);
    if (crs.isEmpty()) {
      return;
    }
    long time1 = System.currentTimeMillis();

    for (ConsumerRecord<Integer, XOutput.Batch> cr : crs) {
      write0(cr.value());
    }

    long time2 = System.currentTimeMillis();

    log.info("[output] once, pollCost=[{}] count=[{}] sendCost=[{}]", //
        time1 - time0, crs.count(), time2 - time1);
  }

  private void write0(XOutput.Batch batch) {
    if (batch == null || batch.groups == null || batch.groups.isEmpty()) {
      return;
    }

    XOutput out = outputMap.get(batch.oi.getType());
    if (out == null) {
      log.error("[output] [{}] ts=[{}] unsupported output type {}", //
          batch.key, batch.window.timestamp, batch.oi.getType());
      return;
    }

    while (true) {
      try {
        out.write(batch);
        break;
      } catch (Exception e) {
        // TODO ignore some un-retryable errors
        log.error("[output] [{}] ts=[{}] output error", batch.key,
            Utils.formatTime(batch.window.timestamp), e);
        try {
          Thread.sleep(1000);
        } catch (InterruptedException ex) {
          Thread.currentThread().interrupt();
          break;
        }
      }
    }
  }

  public void write(XOutput.Batch batch) {
    if (aggProperties.isDebugOutput() || XConsoleOutput.TYPE.equals(batch.getOi().getType())) {
      // write to console of current instance directly
      // avoid logging on another machine
      consoleOutput.write(batch);
      return;
    }

    int random = ThreadLocalRandom.current().nextInt();
    ProducerRecord<Integer, XOutput.Batch> r = new ProducerRecord<>(outputTopic, random, batch);
    producer.send(r, new Callback() {
      @Override
      public void onCompletion(RecordMetadata metadata, Exception e) {
        if (e != null) {
          log.error("[output] agg=[{}] ts=[{}] write batch error", batch.key,
              Utils.formatTime(batch.window.timestamp), e);
        }
      }
    });
  }

}
