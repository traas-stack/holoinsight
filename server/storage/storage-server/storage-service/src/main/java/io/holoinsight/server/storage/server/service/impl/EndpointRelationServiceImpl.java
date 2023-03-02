/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.server.service.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.engine.elasticsearch.model.EndpointRelationEsDO;
import io.holoinsight.server.storage.engine.EndpointRelationStorage;
import io.holoinsight.server.storage.server.service.EndpointRelationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Service
@ConditionalOnFeature("trace")
public class EndpointRelationServiceImpl implements EndpointRelationService {

  @Resource
  @Qualifier("endpointRelationEsServiceImpl")
  private EndpointRelationStorage endpointRelationEsService;

  @Override
  public void insert(List<EndpointRelationEsDO> relationList) throws IOException {
    endpointRelationEsService.batchInsert(relationList);
  }
}
