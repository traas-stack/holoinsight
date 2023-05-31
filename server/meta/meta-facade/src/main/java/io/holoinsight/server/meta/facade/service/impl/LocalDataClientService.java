/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.facade.service.impl;

import java.util.List;
import java.util.Map;

import io.holoinsight.server.meta.common.model.QueryExample;
import io.holoinsight.server.meta.facade.service.DataClientService;

/**
 * TODO 实现本地调用避免grpc
 * <p>
 * created at 2022/12/6
 *
 * @author jsy1001de
 */
public class LocalDataClientService implements DataClientService {

  @Override
  public void insertOrUpdate(String tableName, List<Map<String, Object>> rows) {}

  @Override
  public void delete(String tableName, List<String> uks) {

  }

  @Override
  public List<Map<String, Object>> queryAll(String tableName) {
    return null;
  }

  @Override
  public List<Map<String, Object>> queryAll(String tableName, List<String> rowKeys) {
    return null;
  }

  @Override
  public void deleteByExample(String tableName, QueryExample example) {

  }

  @Override
  public List<Map<String, Object>> queryByExample(String tableName, QueryExample example) {
    return null;
  }

  @Override
  public List<Map<String, Object>> fuzzyByExample(String tableName, QueryExample example) {
    return null;
  }
}
