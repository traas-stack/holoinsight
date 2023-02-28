/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine.storage;

import io.holoinsight.server.storage.engine.model.ServiceErrorDO;

import java.io.IOException;
import java.util.List;


public interface ServiceErrorStorage {

  void batchInsert(final List<ServiceErrorDO> serviceErrorEsDOList) throws IOException;

}
