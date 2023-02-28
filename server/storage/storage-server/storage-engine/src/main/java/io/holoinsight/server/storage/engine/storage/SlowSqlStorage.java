/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine.storage;

import io.holoinsight.server.storage.common.model.query.SlowSql;
import io.holoinsight.server.storage.engine.model.SlowSqlDO;

import java.io.IOException;
import java.util.List;


public interface SlowSqlStorage {

  void batchInsert(final List<SlowSqlDO> slowSqlEsDOList) throws IOException;

  List<SlowSql> getSlowSqlList(String tenant, String serviceName, String dbAddress, long startTime,
      long endTime) throws IOException;

}
