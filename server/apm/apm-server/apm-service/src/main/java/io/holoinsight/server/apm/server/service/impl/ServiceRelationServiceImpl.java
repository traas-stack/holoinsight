/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.server.service.impl;

import io.holoinsight.server.apm.engine.model.ServiceRelationDO;
import io.holoinsight.server.apm.engine.storage.ServiceRelationStorage;
import io.holoinsight.server.apm.server.service.ServiceRelationService;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ServiceRelationServiceImpl implements ServiceRelationService {

  @Autowired
  protected ServiceRelationStorage serviceRelationStorage;


  @Override
  public void insert(List<ServiceRelationDO> relationList) throws Exception {
    serviceRelationStorage.insert(relationList);
  }

}
