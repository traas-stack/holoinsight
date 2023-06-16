/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.web.impl;

import com.google.common.base.Strings;
import io.holoinsight.server.apm.common.model.query.QueryServiceRequest;
import io.holoinsight.server.apm.common.model.query.Service;
import io.holoinsight.server.apm.server.service.ServiceErrorService;
import io.holoinsight.server.apm.server.service.ServiceOverviewService;
import io.holoinsight.server.apm.web.ServiceApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@Slf4j
public class ServiceApiController implements ServiceApi {

  @Autowired
  private ServiceOverviewService serviceOverviewService;

  @Autowired
  private ServiceErrorService serviceErrorService;

  @Override
  public ResponseEntity<List<Service>> queryServiceList(QueryServiceRequest request)
      throws Exception {
    String tenant = request.getTenant();

    if (Strings.isNullOrEmpty(tenant)) {
      throw new IllegalArgumentException("The condition must contains tenant.");
    }

    List<Service> serviceList = serviceOverviewService.getServiceList(tenant,
        request.getStartTime(), request.getEndTime(), request.getTermParams());

    return ResponseEntity.ok(serviceList);
  }

  @Override
  public ResponseEntity<List<Map<String, String>>> queryServiceErrorList(
      QueryServiceRequest request) throws Exception {
    String tenant = request.getTenant();

    if (Strings.isNullOrEmpty(tenant)) {
      throw new IllegalArgumentException("The condition must contains tenant.");
    }

    List<Map<String, String>> serviceList =
        serviceErrorService.getServiceErrorList(tenant, request.getServiceName(),
            request.getStartTime(), request.getEndTime(), request.getTermParams());

    return ResponseEntity.ok(serviceList);
  }

  @Override
  public ResponseEntity<List<Map<String, String>>> queryServiceErrorDetail(
      QueryServiceRequest request) throws Exception {
    String tenant = request.getTenant();

    if (Strings.isNullOrEmpty(tenant)) {
      throw new IllegalArgumentException("The condition must contains tenant.");
    }

    List<Map<String, String>> serviceList =
        serviceErrorService.getServiceErrorDetail(tenant, request.getServiceName(),
            request.getStartTime(), request.getEndTime(), request.getTermParams());

    return ResponseEntity.ok(serviceList);
  }

}
