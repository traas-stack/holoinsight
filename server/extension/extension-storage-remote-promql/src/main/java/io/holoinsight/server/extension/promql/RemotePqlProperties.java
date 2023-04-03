/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.extension.promql;

import java.util.Map;

import io.holoinsight.server.extension.promql.model.Endpoint;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author jinyan.ljw
 * @date 2023/3/8
 */
@ConfigurationProperties(prefix = "holoinsight.metric.pql.remote")
public class RemotePqlProperties {
  /**
   * Whether to enable remote pql.
   */
  private boolean enabled = false;

  Map<String, Endpoint> endpoints;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public Map<String, Endpoint> getEndpoints() {
    return endpoints;
  }

  public void setEndpoints(Map<String, Endpoint> endpoints) {
    this.endpoints = endpoints;
  }
}
