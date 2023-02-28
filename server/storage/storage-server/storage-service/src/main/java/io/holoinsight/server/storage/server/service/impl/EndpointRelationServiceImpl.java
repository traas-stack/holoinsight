/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.server.service.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.engine.model.EndpointRelationDO;
import io.holoinsight.server.storage.engine.storage.EndpointRelationStorage;
import io.holoinsight.server.storage.server.service.EndpointRelationService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Service
@ConditionalOnFeature("trace")
public class EndpointRelationServiceImpl implements EndpointRelationService {

  @Resource
  @Qualifier("endpointRelationEsStorage")
  private EndpointRelationStorage endpointRelationEsService;

  @Override
  public void insert(List<EndpointRelationDO> relationList) throws IOException {
    endpointRelationEsService.batchInsert(relationList);
  }
}
