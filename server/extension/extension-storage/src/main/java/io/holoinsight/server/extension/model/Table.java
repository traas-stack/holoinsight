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
public class Table {
  private String name;
  private long timestamp;
  private Header header;
  private List<Row> rows;
}
