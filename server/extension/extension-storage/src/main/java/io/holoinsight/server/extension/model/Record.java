/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.extension.model;

import java.util.Map;

import lombok.Data;

/**
 * <p>
 * created at 2024/2/23
 *
 * @author xzchaoo
 */
@Data
public class Record {
  private long timestamp;
  private String name;
  private Map<String, String> tags;
  // value must be number of string
  private Map<String, Object> fields;
}
