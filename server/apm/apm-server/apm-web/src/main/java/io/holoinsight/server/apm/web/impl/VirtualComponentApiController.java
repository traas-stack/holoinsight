/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.web.impl;

import com.google.common.base.Strings;
import io.holoinsight.server.apm.common.model.query.QueryComponentRequest;
import io.holoinsight.server.apm.common.model.query.VirtualComponent;
import io.holoinsight.server.apm.server.service.VirtualComponentService;
import io.holoinsight.server.apm.web.VirtualComponentApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Slf4j
public class VirtualComponentApiController implements VirtualComponentApi {

  @Autowired
  private VirtualComponentService virtualComponentService;

  @Override
  public ResponseEntity<List<VirtualComponent>> queryDbList(QueryComponentRequest request)
      throws Exception {
    String tenant = request.getTenant();
    String service = request.getServiceName();

    if (Strings.isNullOrEmpty(tenant) || Strings.isNullOrEmpty(service)) {
      throw new IllegalArgumentException("The condition must contains tenant and service.");
    }

    List<VirtualComponent> dbList = virtualComponentService.getDbList(tenant, service,
        request.getStartTime(), request.getEndTime(), request.getTermParams());
    return ResponseEntity.ok(dbList);
  }

  @Override
  public ResponseEntity<List<VirtualComponent>> queryCacheList(QueryComponentRequest request)
      throws Exception {
    String tenant = request.getTenant();
    String service = request.getServiceName();

    if (Strings.isNullOrEmpty(tenant) || Strings.isNullOrEmpty(service)) {
      throw new IllegalArgumentException("The condition must contains tenant and service.");
    }

    List<VirtualComponent> cacheList = virtualComponentService.getCacheList(tenant, service,
        request.getStartTime(), request.getEndTime(), request.getTermParams());
    return ResponseEntity.ok(cacheList);
  }

  @Override
  public ResponseEntity<List<VirtualComponent>> queryMQList(QueryComponentRequest request)
      throws Exception {
    String tenant = request.getTenant();
    String service = request.getServiceName();

    if (Strings.isNullOrEmpty(tenant) || Strings.isNullOrEmpty(service)) {
      throw new IllegalArgumentException("The condition must contains tenant and service.");
    }

    List<VirtualComponent> mqList = virtualComponentService.getMQList(tenant, service,
        request.getStartTime(), request.getEndTime(), request.getTermParams());
    return ResponseEntity.ok(mqList);
  }

  @Override
  public ResponseEntity<List<String>> queryComponentTraceIds(QueryComponentRequest request)
      throws Exception {
    String tenant = request.getTenant();
    String address = request.getAddress();

    if (Strings.isNullOrEmpty(tenant) || Strings.isNullOrEmpty(address)) {
      throw new IllegalArgumentException("The condition must contains tenant and address.");
    }

    List<String> traceIds =
        virtualComponentService.getTraceIds(tenant, request.getServiceName(), request.getAddress(),
            request.getStartTime(), request.getEndTime(), request.getTermParams());

    return ResponseEntity.ok(traceIds);
  }

}
