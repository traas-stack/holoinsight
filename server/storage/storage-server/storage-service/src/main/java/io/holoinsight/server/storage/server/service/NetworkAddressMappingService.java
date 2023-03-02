/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.server.service;

import io.holoinsight.server.storage.engine.model.NetworkAddressMappingDO;

import java.io.IOException;
import java.util.List;


public interface NetworkAddressMappingService {

  void insert(final List<NetworkAddressMappingDO> addressMappingList) throws IOException;

  List<NetworkAddressMappingDO> loadByTime(long timeBucketInMinute) throws IOException;
}
