/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.extension.model;

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * @author jiwliu
 * @date 2023/1/11
 */
@Data
public class QueryResult {

  @Data
  public static class Result {
    String metric;
    Map<String, String> tags;
    List<Point> points;
  }

  @Data
  public static class Point {
    Long timestamp;
    double value;
    String strValue;
  }

}
