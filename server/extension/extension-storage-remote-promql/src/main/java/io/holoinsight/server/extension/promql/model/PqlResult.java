/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.extension.promql.model;

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * @author jiwliu
 * @date 2023/3/8
 */
@Data
public class PqlResult {

  private String status;

  private Data data;

  @lombok.Data
  public static class Data {

    private String resultType;

    private List<Map<String, Object>> result;
  }
}
