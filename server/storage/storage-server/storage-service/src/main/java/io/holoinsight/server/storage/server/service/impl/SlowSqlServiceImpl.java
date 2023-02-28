/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.server.service.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.common.model.query.SlowSql;
import io.holoinsight.server.storage.engine.model.SlowSqlDO;
import io.holoinsight.server.storage.engine.storage.SlowSqlStorage;
import io.holoinsight.server.storage.server.service.SlowSqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

public class SlowSqlServiceImpl implements SlowSqlService {

  @Autowired
  protected SlowSqlStorage slowSqlStorage;

  @Override
  public void insert(List<SlowSqlDO> slowSqlEsDOList) throws IOException {
    slowSqlStorage.batchInsert(slowSqlEsDOList);
  }

  @Override
  public List<SlowSql> getSlowSqlList(String tenant, String serviceName, String dbAddress,
      long startTime, long endTime) throws IOException {
    return slowSqlStorage.getSlowSqlList(tenant, serviceName, dbAddress, startTime, endTime);
  }

}
