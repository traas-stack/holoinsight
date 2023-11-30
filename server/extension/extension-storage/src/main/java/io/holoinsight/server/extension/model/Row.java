/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.extension.model;

import java.util.List;

import lombok.Data;

/**
 * <p>
 * created at 2023/11/27
 *
 * @author xzchaoo
 */
@Data
public class Row {
  /**
   * If the value is greater than 0, then its priority is higher than {@link Table#getTimestamp()}
   */
  private long timestamp;
  private List<String> tagValues;
  // TODO field type is always Double ???
  private List<Double> fieldValues;
}
