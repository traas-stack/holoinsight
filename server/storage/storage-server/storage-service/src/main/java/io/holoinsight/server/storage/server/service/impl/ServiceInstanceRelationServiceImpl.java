/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.server.service.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.engine.elasticsearch.model.ServiceInstanceRelationEsDO;
import io.holoinsight.server.storage.engine.ServiceInstanceRelationStorage;
import io.holoinsight.server.storage.server.service.ServiceInstanceRelationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@ConditionalOnFeature("trace")
public class ServiceInstanceRelationServiceImpl implements ServiceInstanceRelationService {

  @Autowired
  private ServiceInstanceRelationStorage serviceInstanceRelationEsService;

  @Override
  public void insert(List<ServiceInstanceRelationEsDO> relationList) throws IOException {
    serviceInstanceRelationEsService.batchInsert(relationList);
  }
}
