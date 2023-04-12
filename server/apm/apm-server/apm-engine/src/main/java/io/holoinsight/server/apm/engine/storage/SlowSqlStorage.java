/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.storage;

import io.holoinsight.server.apm.common.model.query.SlowSql;
import io.holoinsight.server.apm.engine.model.SlowSqlDO;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public interface SlowSqlStorage extends WritableStorage<SlowSqlDO>, ReadableStorage {

  List<SlowSql> getSlowSqlList(String tenant, String serviceName, String dbAddress, long startTime,
      long endTime, Map<String, String> termParams) throws IOException;

}
