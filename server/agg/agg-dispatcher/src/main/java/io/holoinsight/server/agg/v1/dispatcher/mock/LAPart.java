/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.dispatcher.mock;

import lombok.Data;

/**
 * <p>
 * created at 2024/2/26
 *
 * @author xzchaoo
 */
@Data
class LAPart {
  private String content;
  private boolean source;
  private boolean important;
  private int count;

  public LAPart() {}

  public LAPart(String content, boolean source, boolean important, int count) {
    this.content = content;
    this.source = source;
    this.important = important;
    this.count = count;
  }
}
