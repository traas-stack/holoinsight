/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.web.impl;

import io.holoinsight.server.storage.web.ServiceInstanceApi;
import io.holoinsight.server.storage.common.model.query.QueryServiceInstanceRequest;
import io.holoinsight.server.storage.common.model.query.ServiceInstance;
import io.holoinsight.server.storage.server.service.ServiceInstanceService;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

@Slf4j
public class ServiceInstanceApiController implements ServiceInstanceApi {

  @Autowired
  private ServiceInstanceService serviceInstanceService;

  @Override
  public ResponseEntity<List<ServiceInstance>> queryServiceInstanceList(
      QueryServiceInstanceRequest request) throws IOException {
    String tenant = request.getTenant();
    String service = request.getServiceName();

    if (Strings.isNullOrEmpty(tenant) || Strings.isNullOrEmpty(service)) {
      throw new IllegalArgumentException("The condition must contains tenant and service.");
    }

    List<ServiceInstance> serviceInstanceList = serviceInstanceService
        .getServiceInstanceList(tenant, service, request.getStartTime(), request.getEndTime());

    return ResponseEntity.ok(serviceInstanceList);
  }

}
