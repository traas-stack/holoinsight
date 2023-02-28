/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.server.service.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.engine.model.NetworkAddressMappingDO;
import io.holoinsight.server.storage.engine.storage.NetworkAddressMappingStorage;
import io.holoinsight.server.storage.server.service.NetworkAddressMappingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

public class NetworkAddressMappingServiceImpl implements NetworkAddressMappingService {

  @Autowired
  protected NetworkAddressMappingStorage networkAddressMappingStorage;

  @Override
  public void insert(List<NetworkAddressMappingDO> addressMappingList) throws IOException {
    networkAddressMappingStorage.batchInsert(addressMappingList);
  }

  @Override
  public List<NetworkAddressMappingDO> loadByTime(long timeBucketInMinute) throws IOException {
    return networkAddressMappingStorage.loadByTime(timeBucketInMinute);
  }
}
