/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.web.impl;

import com.google.common.base.Strings;
import io.holoinsight.server.apm.common.model.query.QueryComponentRequest;
import io.holoinsight.server.apm.common.model.query.SlowSql;
import io.holoinsight.server.apm.server.service.SlowSqlService;
import io.holoinsight.server.apm.web.SlowSqlApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Slf4j
public class SlowSqlApiController implements SlowSqlApi {

  @Autowired
  private SlowSqlService slowSqlService;

  @Override
  public ResponseEntity<List<SlowSql>> querySlowSqlList(QueryComponentRequest request)
      throws Exception {
    String tenant = request.getTenant();

    if (Strings.isNullOrEmpty(tenant)) {
      throw new IllegalArgumentException("The condition must contains tenant.");
    }

    List<SlowSql> slowSqlList =
        slowSqlService.getSlowSqlList(tenant, request.getServiceName(), request.getAddress(),
            request.getStartTime(), request.getEndTime(), request.getTermParams());
    return ResponseEntity.ok(slowSqlList);
  }
}
