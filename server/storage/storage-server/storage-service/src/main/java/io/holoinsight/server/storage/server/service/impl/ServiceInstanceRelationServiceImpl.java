/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.server.service.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.engine.model.ServiceInstanceRelationDO;
import io.holoinsight.server.storage.engine.storage.ServiceInstanceRelationStorage;
import io.holoinsight.server.storage.server.service.ServiceInstanceRelationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

public class ServiceInstanceRelationServiceImpl implements ServiceInstanceRelationService {

  @Autowired
  protected ServiceInstanceRelationStorage serviceInstanceRelationStorage;

  @Override
  public void insert(List<ServiceInstanceRelationDO> relationList) throws IOException {
    serviceInstanceRelationStorage.batchInsert(relationList);
  }
}
