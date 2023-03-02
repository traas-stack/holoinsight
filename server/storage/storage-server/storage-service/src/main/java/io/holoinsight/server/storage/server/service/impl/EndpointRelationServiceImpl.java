/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.server.service.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.engine.model.EndpointRelationDO;
import io.holoinsight.server.storage.engine.storage.EndpointRelationStorage;
import io.holoinsight.server.storage.server.service.EndpointRelationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

public class EndpointRelationServiceImpl implements EndpointRelationService {

  @Autowired
  protected EndpointRelationStorage endpointRelationStorage;


  @Override
  public void insert(List<EndpointRelationDO> relationList) throws IOException {
    endpointRelationStorage.batchInsert(relationList);
  }
}
