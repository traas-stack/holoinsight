/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.server.service;

import io.holoinsight.server.apm.engine.model.NetworkAddressMappingDO;

import java.util.List;


public interface NetworkAddressMappingService {

  void insert(final List<NetworkAddressMappingDO> addressMappingList) throws Exception;

  List<NetworkAddressMappingDO> loadByTime(long timeBucketInMinute) throws Exception;
}
