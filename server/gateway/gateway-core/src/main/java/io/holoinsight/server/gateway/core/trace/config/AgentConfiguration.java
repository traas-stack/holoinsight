/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.trace.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * Dynamic configuration items, save the dynamic configuration of the agent corresponding to the
 * service.
 *
 * @author sw1136562366
 */
@Setter
@Getter
@ToString
public class AgentConfiguration {
  private String tenant;
  private String service;
  private String appId;
  private String envId;
  private Map<String, String> configuration;
  /**
   * The uuid is calculated by the dynamic configuration of the service.
   */
  private volatile String uuid;

  /**
   * <p>
   * Constructor for AgentConfiguration.
   * </p>
   */
  public AgentConfiguration(final String tenant, final String service, final String appId,
      final String envId, final Map<String, String> configuration, final String uuid) {
    this.tenant = tenant;
    this.service = service;
    this.appId = appId;
    this.envId = envId;
    this.configuration = configuration;
    this.uuid = uuid;
  }

  /**
   * <p>
   * getCacheKey.
   * </p>
   */
  public String getCacheKey() {
    return this.tenant + "_" + this.service + "_" + this.appId + "_" + this.envId;
  }
}
