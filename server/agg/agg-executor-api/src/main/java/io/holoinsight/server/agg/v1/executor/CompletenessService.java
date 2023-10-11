/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor;

/**
 * <p>
 * created at 2023/9/30
 *
 * @author xzchaoo
 */
public interface CompletenessService {
  ExpectedCompleteness getByCollectTableName(String tableName);

  ExpectedCompleteness getByDimTable(String dimTable);
}
