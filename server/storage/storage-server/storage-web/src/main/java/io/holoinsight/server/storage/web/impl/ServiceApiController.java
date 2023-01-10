/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.web.impl;

import java.io.IOException;
import java.util.List;

import io.holoinsight.server.storage.common.model.query.QueryServiceRequest;
import io.holoinsight.server.storage.common.model.query.Service;
import io.holoinsight.server.storage.server.service.ServiceOverviewService;
import io.holoinsight.server.storage.web.ServiceApi;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.google.common.base.Strings;

@Slf4j
public class ServiceApiController implements ServiceApi {

  @Autowired
  private ServiceOverviewService serviceOverviewService;

  @Override
  public ResponseEntity<List<Service>> queryServiceList(QueryServiceRequest request)
      throws IOException {
    String tenant = request.getTenant();

    if (Strings.isNullOrEmpty(tenant)) {
      throw new IllegalArgumentException("The condition must contains tenant.");
    }

    List<Service> serviceList =
        serviceOverviewService.getServiceList(tenant, request.getStartTime(), request.getEndTime());

    return ResponseEntity.ok(serviceList);
  }

}
