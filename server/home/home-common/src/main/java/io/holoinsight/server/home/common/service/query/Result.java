/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.service.query;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Result {

  private String metric;
  private Map<String, String> tags;
  private List<Object[]> values;

}
