/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.server.service.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.common.model.query.ServiceInstance;
import io.holoinsight.server.storage.engine.ServiceInstanceStorage;
import io.holoinsight.server.storage.server.service.ServiceInstanceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Service
@ConditionalOnFeature("trace")
public class ServiceInstanceServiceImpl implements ServiceInstanceService {

  @Resource
  @Qualifier("serviceInstanceEsStorage")
  private ServiceInstanceStorage serviceInstanceEsService;

  @Override
  public List<ServiceInstance> getServiceInstanceList(String tenant, String service, long startTime,
      long endTime) throws IOException {
    return serviceInstanceEsService.getServiceInstanceList(tenant, service, startTime, endTime);
  }
}
