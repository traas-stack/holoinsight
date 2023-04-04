/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package io.holoinsight.server.apm.engine.storage;

public interface ReadableStorage {
  /**
   * The field name used to identify the time series, usually for statistics by time, or for pruning
   * optimization
   * 
   * @return
   */
  String timeField();
}
