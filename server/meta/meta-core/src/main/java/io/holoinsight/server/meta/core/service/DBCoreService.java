/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.core.service;

import io.holoinsight.server.common.Pair;
import io.holoinsight.server.meta.common.model.QueryExample;

import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: DBCoreService.java, v 0.1 2022年05月26日 3:05 下午 jinsong.yjs Exp $
 */
public interface DBCoreService {

  void startBuildIndex();

  Pair<Integer, Integer> insertOrUpdate(String tableName, List<Map<String, Object>> rows);

  List<Map<String, Object>> queryByTable(String tableName);

  List<Map<String, Object>> queryByTable(String tableName, List<String> rowKeys);

  List<Map<String, Object>> queryByPks(String tableName, List<String> pkValList);

  List<Map<String, Object>> queryByExample(String tableName, QueryExample queryExample);

  List<Map<String, Object>> fuzzyByExample(String tableName, QueryExample queryExample);

  long deleteByExample(String tableName, QueryExample queryExample);

  long deleteByRowMap(String tableName, List<Map<String, Object>> rows);

  long batchDeleteByPk(String tableName, List<String> default_pks);
}
