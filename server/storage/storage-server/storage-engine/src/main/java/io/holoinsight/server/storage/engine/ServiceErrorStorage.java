/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine;

import io.holoinsight.server.storage.engine.elasticsearch.model.ServiceErrorEsDO;

import java.io.IOException;
import java.util.List;


public interface ServiceErrorStorage {

  void batchInsert(final List<ServiceErrorEsDO> serviceErrorEsDOList) throws IOException;

}
