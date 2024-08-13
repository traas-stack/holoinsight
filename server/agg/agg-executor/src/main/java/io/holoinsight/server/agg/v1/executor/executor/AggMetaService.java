/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * created at 2024/1/10
 *
 * @author xzchaoo
 */
public interface AggMetaService {
  /**
   * 
   * @param metaTable
   * @param condition
   * @return
   */
  List<Map<String, Object>> find(String metaTable, Map<String, Object> condition);
}
