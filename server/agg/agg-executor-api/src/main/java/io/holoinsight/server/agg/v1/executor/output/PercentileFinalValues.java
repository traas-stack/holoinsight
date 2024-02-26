/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.output;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * created at 2024/2/22
 *
 * @author xzchaoo
 */
@Data
public class PercentileFinalValues {
  private List<PercentileFinalValue> values;

  public PercentileFinalValues() {
    this.values = new ArrayList<>();
  }

  public PercentileFinalValues(int capacity) {
    this.values = new ArrayList<>(capacity);
  }

  public void add(double rank, double quantile) {
    values.add(new PercentileFinalValue(rank, quantile));
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PercentileFinalValue {
    private double rank;
    private double quantile;
  }
}
