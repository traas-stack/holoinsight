/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.query;

import lombok.Data;

import java.util.List;

@Data
public class BizopsEndpoint {
  private String service;
  private String endpoint;
  private String stamp;
  private String spanLayer;
  private String errorCode;
  private String rootErrorCode;
  private List<String> traceIds;
  private ResponseMetric metric;
}
