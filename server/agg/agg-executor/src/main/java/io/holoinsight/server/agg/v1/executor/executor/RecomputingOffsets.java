/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import io.holoinsight.server.agg.v1.executor.state.OffsetInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <p>
 * created at 2023/9/25
 *
 * @author xzchaoo
 */
@AllArgsConstructor
@Data
public class RecomputingOffsets {

  /**
   * Last saved partition consumer offset info
   */
  final OffsetInfo last;

  /**
   * Recomputing starting point offset info
   */
  final OffsetInfo start;
}
