/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.storage;

import io.holoinsight.server.apm.engine.model.RecordDO;

import java.io.IOException;
import java.util.List;

public interface WritableStorage<T extends RecordDO> {

  void insert(List<T> entities) throws IOException;

  /**
   * The index name generated according to a specific entity, usually consisting of the model name
   * with a time suffix
   * 
   * @param entity
   * @return
   */
  String writeIndexName(T entity);
}
