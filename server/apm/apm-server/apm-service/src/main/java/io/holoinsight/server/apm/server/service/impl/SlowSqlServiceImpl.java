/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.server.service.impl;

import io.holoinsight.server.apm.common.model.query.SlowSql;
import io.holoinsight.server.apm.engine.model.SlowSqlDO;
import io.holoinsight.server.apm.engine.storage.SlowSqlStorage;
import io.holoinsight.server.apm.server.service.SlowSqlService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class SlowSqlServiceImpl implements SlowSqlService {

  @Autowired
  protected SlowSqlStorage slowSqlStorage;

  @Override
  public void insert(List<SlowSqlDO> slowSqlEsDOList) throws Exception {
    slowSqlStorage.insert(slowSqlEsDOList);
  }

  @Override
  public List<SlowSql> getSlowSqlList(String tenant, String serviceName, String dbAddress,
      long startTime, long endTime, Map<String, String> termParams) throws Exception {
    return slowSqlStorage.getSlowSqlList(tenant, serviceName, dbAddress, startTime, endTime,
        termParams);
  }

}
