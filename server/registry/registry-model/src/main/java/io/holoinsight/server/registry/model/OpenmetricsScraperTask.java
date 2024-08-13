/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.model;

import java.util.List;
import java.util.Map;

import io.holoinsight.server.registry.model.integration.GaeaTask;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class OpenmetricsScraperTask extends GaeaTask {

  private String schema;
  private String metricsPath;
  private String scrapeInterval;
  private String scrapeTimeout;
  private String scrapePort;
  private List<String> targets;
  private Map<String, String> labels;
  private Boolean honorLabels;
  private Boolean honorTimestamps;
  private List<RelabelConfig> relabelConfigs;
  private List<RelabelConfig> metricRelabelConfigs;

  /**
   * Prometheus relabel config
   */
  @Data
  public static class RelabelConfig {
    private List<String> sourceLabels;
    private String targetLabel;
    private String action;
    private String separator;
    private String replacement;
    private String regex;
  }
}
