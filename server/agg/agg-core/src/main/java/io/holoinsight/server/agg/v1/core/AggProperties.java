/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * <p>
 * created at 2023/9/18
 *
 * @author xzchaoo
 */
@ConfigurationProperties(prefix = "holoinsight.agg")
@Data
public class AggProperties {
  /**
   * kafka bootstrap servers
   */
  private String kafkaBootstrapServers;
  /**
   * topic for agg
   */
  private String topic = "aggv1";
  /**
   * agg executor consumer group
   */
  private String consumerGroupId = "x1";
  /**
   * producer compression type
   */
  private String producerCompressionType = "lz4";

  /**
   * if debugOutput is true, the agg output is printed to logs.
   */
  private boolean debugOutput = false;
}
