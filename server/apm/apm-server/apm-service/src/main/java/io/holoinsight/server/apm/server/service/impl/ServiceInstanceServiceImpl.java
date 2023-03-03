/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.server.service.impl;

import io.holoinsight.server.apm.common.model.query.ServiceInstance;
import io.holoinsight.server.apm.engine.storage.ServiceInstanceStorage;
import io.holoinsight.server.apm.server.service.ServiceInstanceService;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

public class ServiceInstanceServiceImpl implements ServiceInstanceService {

  @Autowired
  protected ServiceInstanceStorage serviceInstanceStorage;

  @Override
  public List<ServiceInstance> getServiceInstanceList(String tenant, String service, long startTime,
      long endTime) throws IOException {
    return serviceInstanceStorage.getServiceInstanceList(tenant, service, startTime, endTime);
  }
}
