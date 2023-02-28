/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.server.service.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.engine.model.ServiceRelationDO;
import io.holoinsight.server.storage.engine.storage.ServiceRelationStorage;
import io.holoinsight.server.storage.server.service.ServiceRelationService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Service
@ConditionalOnFeature("trace")
public class ServiceRelationServiceImpl implements ServiceRelationService {

  @Resource
  @Qualifier("serviceRelationEsStorage")
  private ServiceRelationStorage serviceRelationEsService;

  @Override
  public void insert(List<ServiceRelationDO> relationList) throws IOException {
    serviceRelationEsService.batchInsert(relationList);
  }

}
