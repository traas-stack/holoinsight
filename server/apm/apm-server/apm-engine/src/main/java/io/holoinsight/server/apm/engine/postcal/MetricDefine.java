/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.postcal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

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
  private Boolean materialized;

}
