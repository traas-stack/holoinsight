/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.dispatcher.mock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * created at 2024/2/26
 *
 * @author xzchaoo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class SourceWord {
  private String source;
  private int count;
}
