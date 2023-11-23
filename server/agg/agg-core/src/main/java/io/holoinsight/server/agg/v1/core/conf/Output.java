/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core.conf;

import java.util.List;

import lombok.Data;

/**
 * <p>
 * created at 2023/9/22
 *
 * @author xzchaoo
 */
@Data
public class Output {
  private List<OutputItem> items;

  /**
   * If preview is true, the incomplete result will be emitted.
   *
   * @see Window#getPreviewEmitInterval()
   */
  private boolean preview = false;

  public Output() {}
}
