/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.server.service.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.common.model.query.SlowSql;
import io.holoinsight.server.storage.engine.elasticsearch.model.SlowSqlEsDO;
import io.holoinsight.server.storage.engine.SlowSqlStorage;
import io.holoinsight.server.storage.server.service.SlowSqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Service
@ConditionalOnFeature("trace")
public class SlowSqlServiceImpl implements SlowSqlService {

  @Resource
  @Qualifier("slowSqlEsStorage")
  private SlowSqlStorage slowSqlEsService;

  @Override
  public void insert(List<SlowSqlEsDO> slowSqlEsDOList) throws IOException {
    slowSqlEsService.batchInsert(slowSqlEsDOList);
  }

  @Override
  public List<SlowSql> getSlowSqlList(String tenant, String serviceName, String dbAddress,
      long startTime, long endTime) throws IOException {
    return slowSqlEsService.getSlowSqlList(tenant, serviceName, dbAddress, startTime, endTime);
  }

}
