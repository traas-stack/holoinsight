/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import java.util.ArrayList;

import io.holoinsight.server.agg.v1.executor.CompletenessService;
import io.holoinsight.server.agg.v1.executor.ExpectedCompleteness;

/**
 * <p>
 * created at 2023/9/30
 *
 * @author xzchaoo
 */
public class MockCompletenessService implements CompletenessService {

  @Override
  public ExpectedCompleteness getByCollectTableName(String tableName) {
    ExpectedCompleteness ec = new ExpectedCompleteness();
    ec.setExpectedTargets(new ArrayList<>());
    return ec;
  }

  @Override
  public ExpectedCompleteness getByDimTable(String dimTable) {
    ExpectedCompleteness ec = new ExpectedCompleteness();
    ec.setExpectedTargets(new ArrayList<>());
    return ec;
  }
}
