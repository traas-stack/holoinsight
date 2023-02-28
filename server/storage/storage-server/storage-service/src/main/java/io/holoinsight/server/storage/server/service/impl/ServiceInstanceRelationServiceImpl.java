/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.server.service.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.engine.model.ServiceInstanceRelationDO;
import io.holoinsight.server.storage.engine.storage.ServiceInstanceRelationStorage;
import io.holoinsight.server.storage.server.service.ServiceInstanceRelationService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Service
@ConditionalOnFeature("trace")
public class ServiceInstanceRelationServiceImpl implements ServiceInstanceRelationService {

  @Resource
  @Qualifier("serviceInstanceRelationEsStorage")
  private ServiceInstanceRelationStorage serviceInstanceRelationEsService;

  @Override
  public void insert(List<ServiceInstanceRelationDO> relationList) throws IOException {
    serviceInstanceRelationEsService.batchInsert(relationList);
  }
}
