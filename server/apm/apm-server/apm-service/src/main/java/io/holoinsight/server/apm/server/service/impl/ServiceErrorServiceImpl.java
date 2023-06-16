/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.server.service.impl;

import io.holoinsight.server.apm.engine.model.ServiceErrorDO;
import io.holoinsight.server.apm.engine.storage.ServiceErrorStorage;
import io.holoinsight.server.apm.server.service.ServiceErrorService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class ServiceErrorServiceImpl implements ServiceErrorService {

  @Autowired
  protected ServiceErrorStorage serviceErrorStorage;

  @Override
  public void insert(List<ServiceErrorDO> serviceErrorEsDOList) throws Exception {
    serviceErrorStorage.insert(serviceErrorEsDOList);
  }

  @Override
  public List<Map<String, String>> getServiceErrorList(String tenant, String serviceName,
      long startTime, long endTime, Map<String, String> termParams) throws Exception {
    return serviceErrorStorage.getServiceErrorList(tenant, serviceName, startTime, endTime,
        termParams);
  }

  @Override
  public List<Map<String, String>> getServiceErrorDetail(String tenant, String serviceName,
      long startTime, long endTime, Map<String, String> termParams) throws Exception {
    return serviceErrorStorage.getServiceErrorDetail(tenant, serviceName, startTime, endTime,
        termParams);
  }

}
