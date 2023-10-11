/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor;

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * <p>
 * created at 2023/9/30
 *
 * @author xzchaoo
 */
@Data
public class ExpectedCompleteness {
  private List<Map<String, Object>> expectedTargets;
}
