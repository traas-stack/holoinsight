/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.postcal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * MetricDefine defines how to calculate an APM metric, which may be obtained through some DSL of
 * the search engine, may be obtained through a metric materialized into MetricStore, or may be
 * obtained by including other MetricDefine arithmetic expressions
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetricDefine {

  private String name;
  private String index;
  private String field;
  private String function;
  private List<String> groups;
  private Map<String, Object> conditions;
  // Whether this metric should be materialized into a MetricStore
  private boolean materialized;
  // Can be calculated by a parent metric which has been materialized into a MetricStore
  private String parent;
  // Whether to force statistics from search engines, such as some functions(e.g. Percentile) not
  // supported by MetricStore
  private boolean forceQuerySearchEngine;
  // Set when the metric is calculated from an expression with other metrics
  private String expr;

}
