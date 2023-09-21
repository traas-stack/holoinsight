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
public class WriteMetricsParam {

  private String tenant;
  private List<Point> points;
  // whether this writing is not included in the fee
  private boolean isFree;

  @Data
  public static class Point {
    String metricName;
    long timeStamp;
    Map<String, String> tags;
    double value;
    String strValue;
  }

}

