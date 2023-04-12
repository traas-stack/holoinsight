/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.web.impl;

import com.google.common.base.Strings;
import io.holoinsight.server.apm.common.model.query.Endpoint;
import io.holoinsight.server.apm.common.model.query.QueryEndpointRequest;
import io.holoinsight.server.apm.server.service.EndpointService;
import io.holoinsight.server.apm.web.EndpointApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Slf4j
public class EndpointApiController implements EndpointApi {

  @Autowired
  private EndpointService endpointService;

  @Override
  public ResponseEntity<List<Endpoint>> queryEndpointList(QueryEndpointRequest request)
      throws Exception {
    String tenant = request.getTenant();
    String service = request.getServiceName();

    if (Strings.isNullOrEmpty(tenant) || Strings.isNullOrEmpty(service)) {
      throw new IllegalArgumentException("The condition must contains tenant and service.");
    }

    List<Endpoint> endpointList = endpointService.getEndpointList(tenant, service,
        request.getStartTime(), request.getEndTime(), request.getTermParams());

    return ResponseEntity.ok(endpointList);
  }
}
