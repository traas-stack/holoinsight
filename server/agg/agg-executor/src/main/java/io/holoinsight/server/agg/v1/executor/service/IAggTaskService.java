/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.service;

import io.holoinsight.server.agg.v1.executor.executor.XAggTask;

/**
 * <p>
 * created at 2023/9/26
 *
 * @author xzchaoo
 */
public interface IAggTaskService {
  XAggTask getAggTask(String aggId);
}
