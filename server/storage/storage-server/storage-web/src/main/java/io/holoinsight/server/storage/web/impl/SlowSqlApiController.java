/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.web.impl;

import com.google.common.base.Strings;
import io.holoinsight.server.storage.common.model.query.QueryComponentRequest;
import io.holoinsight.server.storage.common.model.query.SlowSql;
import io.holoinsight.server.storage.server.service.SlowSqlService;
import io.holoinsight.server.storage.web.SlowSqlApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

@Slf4j
public class SlowSqlApiController implements SlowSqlApi {

    @Autowired
    private SlowSqlService slowSqlService;

    @Override
    public ResponseEntity<List<SlowSql>> querySlowSqlList(QueryComponentRequest request) throws IOException {
        String tenant = request.getTenant();

        if (Strings.isNullOrEmpty(tenant)) {
            throw new IllegalArgumentException("The condition must contains tenant.");
        }

        List<SlowSql> slowSqlList = slowSqlService.getSlowSqlList(tenant, request.getServiceName(), request.getAddress(), request.getStartTime(), request.getEndTime());
        return ResponseEntity.ok(slowSqlList);
    }
}
