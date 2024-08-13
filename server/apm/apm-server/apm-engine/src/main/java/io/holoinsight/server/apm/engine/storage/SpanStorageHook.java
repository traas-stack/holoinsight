/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.storage;

import java.util.List;

import io.holoinsight.server.apm.engine.model.ServiceErrorDO;
import io.holoinsight.server.apm.engine.model.ServiceRelationDO;
import io.holoinsight.server.apm.engine.model.SpanDO;

/**
 * <p>
 * created at 2024/2/5
 *
 * @author xzchaoo
 */
public interface SpanStorageHook {
  void beforeStorage(List<SpanDO> spans);

  void beforeStorageServiceRelation(List<ServiceRelationDO> list);

  void beforeStorageServiceError(List<ServiceErrorDO> list);
}
