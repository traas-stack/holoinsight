/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.server.service.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.engine.ServiceOverviewStorage;
import io.holoinsight.server.storage.server.service.ServiceOverviewService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@ConditionalOnFeature("trace")
public class ServiceOverviewServiceImpl implements ServiceOverviewService {

  @Autowired
  private ServiceOverviewStorage serviceOverviewEsService;

  @Override
  public List<io.holoinsight.server.storage.common.model.query.Service> getServiceList(
      String tenant, long startTime, long endTime) throws IOException {
    return serviceOverviewEsService.getServiceList(tenant, startTime, endTime);
  }

}
