/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.server.service;

import io.holoinsight.server.apm.engine.model.EndpointRelationDO;

import java.util.List;


public interface EndpointRelationService {

  void insert(final List<EndpointRelationDO> relationList) throws Exception;

}
