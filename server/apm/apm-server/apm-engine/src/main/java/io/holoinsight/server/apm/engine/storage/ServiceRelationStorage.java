/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.storage;

import io.holoinsight.server.apm.engine.model.ServiceRelationDO;

import java.io.IOException;
import java.util.List;

public interface ServiceRelationStorage {

  void batchInsert(final List<ServiceRelationDO> relationList) throws IOException;

}
