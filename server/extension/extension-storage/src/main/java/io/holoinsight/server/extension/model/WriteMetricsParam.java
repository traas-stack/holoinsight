/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.extension.model;

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * @author jinyan.ljw
 * @date 2023/1/11
 */
@Data
public class WriteMetricsParam {

  private String tenant;
  private List<Point> points;

  @Data
  public static class Point {
    String metricName;
    long timeStamp;
    Map<String, String> tags;
    double value;
    String strValue;
  }

}

