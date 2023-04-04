/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.storage;

import io.holoinsight.server.apm.engine.model.NetworkAddressMappingDO;

import java.io.IOException;
import java.util.List;


public interface NetworkAddressMappingStorage
    extends WritableStorage<NetworkAddressMappingDO>, ReadableStorage {

  List<NetworkAddressMappingDO> loadByTime(long timeBucketInMinute) throws IOException;
}
