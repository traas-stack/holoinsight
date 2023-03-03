/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author jiwliu
 * @version : SearchTraceRequest.java, v 0.1 2022年09月19日 16:59 xiangwanpeng Exp $
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryMetricRequest {
  private String tenant;
  private String metric;
  private Duration duration;
  private Map<String, Object> conditions;
}
