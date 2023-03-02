/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.server.service;

import io.holoinsight.server.storage.engine.model.EndpointRelationDO;

import java.io.IOException;
import java.util.List;


public interface EndpointRelationService {

  void insert(final List<EndpointRelationDO> relationList) throws IOException;

}
