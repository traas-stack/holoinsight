/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.server.service;

import io.holoinsight.server.apm.engine.model.ServiceInstanceRelationDO;

import java.util.List;


public interface ServiceInstanceRelationService {

  void insert(final List<ServiceInstanceRelationDO> relationList) throws Exception;

}
