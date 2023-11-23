/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import io.holoinsight.server.agg.v1.core.data.DataAccessor;

/**
 * <p>
 * created at 2023/9/23
 *
 * @author xzchaoo
 */
public interface XWhere {
  XWhere ALWAYS_TRUE = in -> true;

  // Where inner();

  boolean test(DataAccessor da);
}
