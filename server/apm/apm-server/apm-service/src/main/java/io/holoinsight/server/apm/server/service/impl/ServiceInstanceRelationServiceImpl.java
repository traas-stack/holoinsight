/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.server.service.impl;

import io.holoinsight.server.apm.engine.model.ServiceInstanceRelationDO;
import io.holoinsight.server.apm.engine.storage.ServiceInstanceRelationStorage;
import io.holoinsight.server.apm.server.service.ServiceInstanceRelationService;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ServiceInstanceRelationServiceImpl implements ServiceInstanceRelationService {

  @Autowired
  protected ServiceInstanceRelationStorage serviceInstanceRelationStorage;

  @Override
  public void insert(List<ServiceInstanceRelationDO> relationList) throws Exception {
    serviceInstanceRelationStorage.insert(relationList);
  }
}
