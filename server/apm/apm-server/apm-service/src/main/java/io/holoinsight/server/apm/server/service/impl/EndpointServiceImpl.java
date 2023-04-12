/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.server.service.impl;

import io.holoinsight.server.apm.common.model.query.Endpoint;
import io.holoinsight.server.apm.engine.storage.EndpointStorage;
import io.holoinsight.server.apm.server.service.EndpointService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class EndpointServiceImpl implements EndpointService {

  @Autowired
  protected EndpointStorage endpointStorage;

  @Override
  public List<Endpoint> getEndpointList(String tenant, String service, long startTime, long endTime,
      Map<String, String> termParams) throws Exception {
    return endpointStorage.getEndpointList(tenant, service, startTime, endTime, termParams);
  }
}
