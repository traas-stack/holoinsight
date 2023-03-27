/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.web.impl;

import com.google.common.base.Strings;
import io.holoinsight.server.apm.common.model.query.QueryServiceRequest;
import io.holoinsight.server.apm.common.model.query.Service;
import io.holoinsight.server.apm.server.service.ServiceOverviewService;
import io.holoinsight.server.apm.web.ServiceApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Slf4j
public class ServiceApiController implements ServiceApi {

  @Autowired
  private ServiceOverviewService serviceOverviewService;

  @Override
  public ResponseEntity<List<Service>> queryServiceList(QueryServiceRequest request)
      throws Exception {
    String tenant = request.getTenant();

    if (Strings.isNullOrEmpty(tenant)) {
      throw new IllegalArgumentException("The condition must contains tenant.");
    }

    List<Service> serviceList =
        serviceOverviewService.getServiceList(tenant, request.getStartTime(), request.getEndTime());

    return ResponseEntity.ok(serviceList);
  }

}
