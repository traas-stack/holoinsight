/*
 * Alipay.com Inc. Copyright (c) 2004-2018 All Rights Reserved.
 */
package io.holoinsight.server.common.config;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author masaimu
 * @version 2024-04-26 16:25:00
 */
@ConfigurationProperties(prefix = "holoinsight.kafka")
@Data
public class KafkaProperties {
  /**
   * kafka bootstrap servers
   */
  private String kafkaBootstrapServers = StringUtils.EMPTY;
  /**
   * agg executor consumer group
   */
  private String consumerGroupId = "x1";

  private boolean enable = false;
}
