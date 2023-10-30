/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core.conf;

import javax.annotation.Nonnull;

import lombok.Data;

/**
 * <p>
 * created at 2023/9/21
 *
 * @author xzchaoo
 */
@Data
public class Window {
  public static final String TYPE_SLIDING = "SLIDING";
  @Nonnull
  private String type;
  private long interval;

  /**
   * emit interval for preview data
   */
  private long previewEmitInterval = 60000L;

  public Window() {}

  public static Window sliding(long interval) {
    if (interval <= 0) {
      throw new IllegalArgumentException("window interval <= 0");
    }
    Window w = new Window();
    w.setType(TYPE_SLIDING);
    w.setInterval(interval);
    return w;
  }
}
