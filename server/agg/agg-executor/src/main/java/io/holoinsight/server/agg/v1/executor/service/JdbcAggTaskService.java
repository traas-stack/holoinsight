/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.service;

import org.springframework.beans.factory.annotation.Autowired;

import io.holoinsight.server.agg.v1.executor.executor.XAggTask;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * created at 2023/9/26
 *
 * @author xzchaoo
 */
@Slf4j
public class JdbcAggTaskService implements IAggTaskService {
  @Autowired
  private AggTaskV1StorageForExecutor aggTaskV1StorageForExecutor;

  @Override
  public XAggTask getAggTask(String aggId) {
    return aggTaskV1StorageForExecutor.get(aggId);
  }
}
