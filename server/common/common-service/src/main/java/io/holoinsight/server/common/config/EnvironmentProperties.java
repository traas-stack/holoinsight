/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author likun (saimu.msm@antfin.com)
 * @version 2023-01-06 14:55:00
 */
@ConfigurationProperties(prefix = "holoinsight.env")
@Data
public class EnvironmentProperties {
  private String deploymentSite;
  private String userCenter;
  private String location;
  private String gptUrl;
}
