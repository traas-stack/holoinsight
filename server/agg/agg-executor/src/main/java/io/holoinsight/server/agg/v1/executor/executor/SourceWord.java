/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import lombok.Data;

/**
 * <p>
 * created at 2024/2/26
 *
 * @author xzchaoo
 */
@Data
class SourceWord {
  private String source;
  private int count;
}
