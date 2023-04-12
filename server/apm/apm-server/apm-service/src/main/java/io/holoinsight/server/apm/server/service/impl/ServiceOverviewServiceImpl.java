/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.server.service.impl;

import io.holoinsight.server.apm.common.model.query.Service;
import io.holoinsight.server.apm.engine.storage.ServiceOverviewStorage;
import io.holoinsight.server.apm.server.service.ServiceOverviewService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class ServiceOverviewServiceImpl implements ServiceOverviewService {

  @Autowired
  protected ServiceOverviewStorage serviceOverviewStorage;

  @Override
  public List<Service> getServiceList(String tenant, long startTime, long endTime,
      Map<String, String> termParams) throws Exception {
    return serviceOverviewStorage.getServiceList(tenant, startTime, endTime, termParams);
  }

}
