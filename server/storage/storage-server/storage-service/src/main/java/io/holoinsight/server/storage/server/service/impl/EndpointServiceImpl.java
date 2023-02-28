/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.server.service.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.common.model.query.Endpoint;
import io.holoinsight.server.storage.engine.EndpointStorage;
import io.holoinsight.server.storage.server.service.EndpointService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@ConditionalOnFeature("trace")
public class EndpointServiceImpl implements EndpointService {

  @Autowired
  private EndpointStorage endpointEsService;

  @Override
  public List<Endpoint> getEndpointList(String tenant, String service, long startTime, long endTime)
      throws IOException {
    return endpointEsService.getEndpointList(tenant, service, startTime, endTime);
  }

}
