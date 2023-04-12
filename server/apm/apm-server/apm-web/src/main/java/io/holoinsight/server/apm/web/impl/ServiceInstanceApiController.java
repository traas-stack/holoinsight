/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.web.impl;

import com.google.common.base.Strings;
import io.holoinsight.server.apm.common.model.query.QueryServiceInstanceRequest;
import io.holoinsight.server.apm.common.model.query.ServiceInstance;
import io.holoinsight.server.apm.server.service.ServiceInstanceService;
import io.holoinsight.server.apm.web.ServiceInstanceApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Slf4j
public class ServiceInstanceApiController implements ServiceInstanceApi {

  @Autowired
  private ServiceInstanceService serviceInstanceService;

  @Override
  public ResponseEntity<List<ServiceInstance>> queryServiceInstanceList(
      QueryServiceInstanceRequest request) throws Exception {
    String tenant = request.getTenant();
    String service = request.getServiceName();

    if (Strings.isNullOrEmpty(tenant) || Strings.isNullOrEmpty(service)) {
      throw new IllegalArgumentException("The condition must contains tenant and service.");
    }

    List<ServiceInstance> serviceInstanceList = serviceInstanceService.getServiceInstanceList(
        tenant, service, request.getStartTime(), request.getEndTime(), request.getTermParams());

    return ResponseEntity.ok(serviceInstanceList);
  }

}
