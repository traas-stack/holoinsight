/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine;

import io.holoinsight.server.storage.engine.elasticsearch.model.NetworkAddressMappingEsDO;

import java.io.IOException;
import java.util.List;


public interface NetworkAddressMappingStorage {

  void batchInsert(final List<NetworkAddressMappingEsDO> addressMappingList) throws IOException;

  List<NetworkAddressMappingEsDO> loadByTime(long timeBucketInMinute) throws IOException;
}
