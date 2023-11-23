/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import java.time.Duration;
import java.util.Properties;

import lombok.Data;

/**
 * <p>
 * created at 2023/9/17
 *
 * @author xzchaoo
 */
@Data
public class ExecutorConfig {
  /**
   * kafka topic for agg
   */
  private String topic;
  /**
   * kafka bootstrap servers
   */
  private String kafkaBootstrapServers;

  /**
   * kafka consumer groupId
   */
  private String groupId;

  /**
   * kafka consumer base properties
   */
  private Properties consumerProperties = new Properties();

  /**
   * executor count
   */
  private int executorCount = 1;

  /**
   * size of threadpool shared by all executors
   */
  private int computingThreadPoolSize = 4;
  private int ioThreadPoolSize = 2;

  private Duration idleTimeout = Duration.ofSeconds(5);

  private FaultToleranceConfig faultToleranceConfig = new FaultToleranceConfig();

}
