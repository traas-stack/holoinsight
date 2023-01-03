/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.server.service;

import io.holoinsight.server.storage.common.model.query.SlowSql;
import io.holoinsight.server.storage.engine.elasticsearch.model.SlowSqlEsDO;

import java.io.IOException;
import java.util.List;


public interface SlowSqlService {

    void insert(final List<SlowSqlEsDO> slowSqlEsDOList) throws IOException;

    List<SlowSql> getSlowSqlList(String tenant, String serviceName, String dbAddress, long startTime, long endTime) throws IOException;
}