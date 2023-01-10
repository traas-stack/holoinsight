/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.facade.service;

import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: TableService.java, v 0.1 2022年03月07日 4:56 下午 jinsong.yjs Exp $
 */
public interface TableClientService {

  void createTable(String tableName);

  void deleteTable(String tableName);

  void createIndex(String tableName, String indexKey, Boolean asc);

  void deleteIndex(String tableName, String indexKey);

  List<Object> getIndexInfo(String tableName);


}
