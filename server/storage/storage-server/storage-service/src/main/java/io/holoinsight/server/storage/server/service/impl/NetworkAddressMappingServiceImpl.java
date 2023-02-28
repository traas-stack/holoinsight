/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.server.service.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.engine.model.NetworkAddressMappingDO;
import io.holoinsight.server.storage.engine.storage.NetworkAddressMappingStorage;
import io.holoinsight.server.storage.server.service.NetworkAddressMappingService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Service
@ConditionalOnFeature("trace")
public class NetworkAddressMappingServiceImpl implements NetworkAddressMappingService {

  @Resource
  @Qualifier("networkAddressMappingEsStorage")
  private NetworkAddressMappingStorage networkAddressMappingEsService;

  @Override
  public void insert(List<NetworkAddressMappingDO> addressMappingList) throws IOException {
    networkAddressMappingEsService.batchInsert(addressMappingList);
  }

  @Override
  public List<NetworkAddressMappingDO> loadByTime(long timeBucketInMinute) throws IOException {
    return networkAddressMappingEsService.loadByTime(timeBucketInMinute);
  }
}
