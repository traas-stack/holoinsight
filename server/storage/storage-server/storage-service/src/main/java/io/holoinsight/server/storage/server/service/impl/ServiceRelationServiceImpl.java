/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.server.service.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.engine.model.ServiceRelationDO;
import io.holoinsight.server.storage.engine.storage.ServiceRelationStorage;
import io.holoinsight.server.storage.server.service.ServiceRelationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

public class ServiceRelationServiceImpl implements ServiceRelationService {

  @Autowired
  protected ServiceRelationStorage serviceRelationStorage;


  @Override
  public void insert(List<ServiceRelationDO> relationList) throws IOException {
    serviceRelationStorage.batchInsert(relationList);
  }

}
