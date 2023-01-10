/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.server.service;

import io.holoinsight.server.storage.engine.elasticsearch.model.NetworkAddressMappingEsDO;

import java.io.IOException;
import java.util.List;


public interface NetworkAddressMappingService {

  void insert(final List<NetworkAddressMappingEsDO> addressMappingList) throws IOException;

  List<NetworkAddressMappingEsDO> loadByTime(long timeBucketInMinute) throws IOException;
}
