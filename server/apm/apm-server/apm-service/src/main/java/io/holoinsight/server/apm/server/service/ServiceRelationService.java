/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.server.service;

import io.holoinsight.server.apm.engine.model.ServiceRelationDO;

import java.util.List;


public interface ServiceRelationService {

  void insert(final List<ServiceRelationDO> relationList) throws Exception;

}
