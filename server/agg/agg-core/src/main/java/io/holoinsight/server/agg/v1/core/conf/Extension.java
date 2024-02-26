/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core.conf;

import lombok.Data;

/**
 * Agg Task Extension
 * <p>
 * created at 2024/2/20
 *
 * @author xzchaoo
 */
@Data
public class Extension {
  /**
   * Whether to enter debug mode
   */
  private boolean debug;
}
