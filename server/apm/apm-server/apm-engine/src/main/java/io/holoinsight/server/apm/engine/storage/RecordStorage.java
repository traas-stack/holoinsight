/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.storage;

import io.holoinsight.server.apm.engine.model.RecordDO;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author xiangwanpeng
 * @version : RecordStorage.java, v 0.1 2023年02月28日 16:20 xiangwanpeng Exp $
 */
public interface RecordStorage<T extends RecordDO> {
  void batchInsert(List<T> entities) throws IOException;
}
