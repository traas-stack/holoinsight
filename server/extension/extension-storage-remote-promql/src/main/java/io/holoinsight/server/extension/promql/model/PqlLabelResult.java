/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.extension.promql.model;

import lombok.Data;

/**
 * @author jsy1001de
 * @version 1.0: PqlLabelResult.java, Date: 2024-05-21 Time: 21:06
 */
@Data
public class PqlLabelResult<T> {
  private String status;

  private T data;

}
