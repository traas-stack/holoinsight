/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.measure;

/**
 * @author masaimu
 * @version 2023-03-01 22:22:00
 */
public enum ResourceType {
  log("log", "LOG"), //
  metric("metric", "METRIC"), //
  metric_tag("metric_tag", "METRIC"), //
  tag("tag", "METRIC"), //
  trace("trace", "TRACE"), //
  alert_config("alert_config", "CONFIG"), //
  agent_config("agent_config", "CONFIG"), //
  unknown("unknown", "UNKNOWN"),;

  String code;
  String type;

  ResourceType(String code, String type) {
    this.code = code;
    this.type = type;
  }
}
