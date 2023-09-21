/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.extension.model;

import java.util.List;

import lombok.Data;

/**
 * @author jiwliu
 * @date 2023/1/11
 */
@Data
public class QueryParam {
  private String tenant;
  long start;
  long end;
  String metric;
  List<QueryFilter> filters;
  String aggregator;
  String downsample;
  SlidingWindow slidingWindow;
  List<String> groupBy;
  String ql;

  @Data
  public static class SlidingWindow {
    long windowMs;
    String aggregator;
  }

  @Data
  public static class QueryFilter {
    String type;
    String name;
    String value;
  }
}
