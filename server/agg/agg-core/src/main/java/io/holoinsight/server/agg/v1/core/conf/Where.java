/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core.conf;

import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import lombok.Data;

/**
 * <p>
 * created at 2023/9/22
 *
 * @author xzchaoo
 */
@Data
public class Where {
  /**
   * <code>
   *     {
   *         "app": ["app1", "app2"],
   *         "!env": ["PRE", "GRAY"]
   *     }
   * </code>
   */
  @Nullable
  private Map<String, Set<String>> simple;
}
