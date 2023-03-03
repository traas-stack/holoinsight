/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.server.service.impl;

import io.holoinsight.server.apm.engine.model.ServiceErrorDO;
import io.holoinsight.server.apm.engine.storage.ServiceErrorStorage;
import io.holoinsight.server.apm.server.service.ServiceErrorService;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

public class ServiceErrorServiceImpl implements ServiceErrorService {

  @Autowired
  protected ServiceErrorStorage serviceErrorStorage;

  @Override
  public void insert(List<ServiceErrorDO> serviceErrorEsDOList) throws IOException {
    serviceErrorStorage.batchInsert(serviceErrorEsDOList);
  }

}
