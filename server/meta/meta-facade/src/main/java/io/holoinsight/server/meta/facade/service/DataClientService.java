/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.facade.service;

import io.holoinsight.server.meta.common.model.QueryExample;

import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: DataClientService.java, v 0.1 2022年03月08日 11:42 上午 jinsong.yjs Exp $
 */
public interface DataClientService {

  void insertOrUpdate(String tableName, List<Map<String, Object>> rows);

  void delete(String tableName, List<String> uks);

  List<Map<String, Object>> queryAll(String tableName);

  List<Map<String, Object>> queryAll(String tableName, List<String> rowKeys);

  void deleteByExample(String tableName, QueryExample example);

  List<Map<String, Object>> queryByExample(String tableName, QueryExample example);

  List<Map<String, Object>> fuzzyByExample(String tableName, QueryExample example);
}
