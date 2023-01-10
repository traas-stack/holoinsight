/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine.elasticsearch.service;

import io.holoinsight.server.storage.engine.elasticsearch.model.ServiceRelationEsDO;

import java.io.IOException;
import java.util.List;

public interface ServiceRelationEsService {

  void batchInsert(final List<ServiceRelationEsDO> relationList) throws IOException;

}
