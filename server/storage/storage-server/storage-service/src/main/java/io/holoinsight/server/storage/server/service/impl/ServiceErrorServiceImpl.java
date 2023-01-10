/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.server.service.impl;

import io.holoinsight.server.storage.engine.elasticsearch.model.ServiceErrorEsDO;
import io.holoinsight.server.storage.engine.elasticsearch.service.ServiceErrorEsService;
import io.holoinsight.server.storage.server.service.ServiceErrorService;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

public class ServiceErrorServiceImpl implements ServiceErrorService {

  @Autowired
  private ServiceErrorEsService serviceErrorEsService;

  @Override
  public void insert(List<ServiceErrorEsDO> serviceErrorEsDOList) throws IOException {
    serviceErrorEsService.batchInsert(serviceErrorEsDOList);
  }

}
