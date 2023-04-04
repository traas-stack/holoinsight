/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.server.service.impl;

import io.holoinsight.server.apm.engine.model.NetworkAddressMappingDO;
import io.holoinsight.server.apm.engine.storage.NetworkAddressMappingStorage;
import io.holoinsight.server.apm.server.service.NetworkAddressMappingService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class NetworkAddressMappingServiceImpl implements NetworkAddressMappingService {

  @Autowired
  protected NetworkAddressMappingStorage networkAddressMappingStorage;

  @Override
  public void insert(List<NetworkAddressMappingDO> addressMappingList) throws Exception {
    networkAddressMappingStorage.insert(addressMappingList);
  }

  @Override
  public List<NetworkAddressMappingDO> loadByTime(long startTime) throws Exception {
    return networkAddressMappingStorage.loadByTime(startTime);
  }
}
