/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.storage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author jiwliu
 * @version : Model.java, v 0.1 2022年10月11日 19:14 xiangwanpeng Exp $
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Model {
  private String name;
  private List<ModelColumn> columns;
  private boolean isTimeSeries;
  private long ttl;
}
