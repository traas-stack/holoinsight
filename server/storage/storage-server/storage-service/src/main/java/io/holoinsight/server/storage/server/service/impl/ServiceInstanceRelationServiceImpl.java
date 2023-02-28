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

import static io.holoinsight.server.storage.server.Executors.EXECUTOR;

@Service
@ConditionalOnFeature("trace")
public class ServiceInstanceRelationServiceImpl implements ServiceInstanceRelationService {

  @Resource
  @Qualifier("serviceInstanceRelationEsStorage")
  private ServiceInstanceRelationStorage serviceInstanceRelationEsStorage;

  @Resource
  @Qualifier("serviceInstanceRelationTatrisStorage")
  private ServiceInstanceRelationStorage serviceInstanceRelationTatrisStorage;

  @Override
  public void insert(List<ServiceInstanceRelationDO> relationList) throws IOException {
    if (serviceInstanceRelationTatrisStorage != null) {
      EXECUTOR.submit(() -> {
        try {
          serviceInstanceRelationTatrisStorage.batchInsert(relationList);
        } catch (Exception ignored) {
        }
      });
    }
    serviceInstanceRelationEsStorage.batchInsert(relationList);
  }
}
