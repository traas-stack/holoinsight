/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.storage;

public interface ReadableStorage {

  /**
   * The field name used to identify the time series, usually for statistics by time, or for pruning
   * optimization
   *
   * @return
   */
  String timeSeriesField();
}
