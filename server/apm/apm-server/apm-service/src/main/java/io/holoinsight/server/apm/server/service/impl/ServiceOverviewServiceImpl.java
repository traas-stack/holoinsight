/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.server.service.impl;

import io.holoinsight.server.apm.common.model.query.Service;
import io.holoinsight.server.apm.engine.storage.ServiceOverviewStorage;
import io.holoinsight.server.apm.server.service.ServiceOverviewService;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

public class ServiceOverviewServiceImpl implements ServiceOverviewService {

  @Autowired
  protected ServiceOverviewStorage serviceOverviewStorage;

  @Override
  public List<Service> getServiceList(String tenant, long startTime, long endTime)
      throws Exception {
    return serviceOverviewStorage.getServiceList(tenant, startTime, endTime);
  }

}
