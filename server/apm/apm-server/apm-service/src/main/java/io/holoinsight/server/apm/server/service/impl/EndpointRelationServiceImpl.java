/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.server.service.impl;

import io.holoinsight.server.apm.engine.model.EndpointRelationDO;
import io.holoinsight.server.apm.engine.storage.EndpointRelationStorage;
import io.holoinsight.server.apm.server.service.EndpointRelationService;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class EndpointRelationServiceImpl implements EndpointRelationService {

  @Autowired
  protected EndpointRelationStorage endpointRelationStorage;


  @Override
  public void insert(List<EndpointRelationDO> relationList) throws Exception {
    endpointRelationStorage.insert(relationList);
  }
}
