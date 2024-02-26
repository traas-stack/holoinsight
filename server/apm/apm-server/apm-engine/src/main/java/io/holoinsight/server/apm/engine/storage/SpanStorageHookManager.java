/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.storage;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import io.holoinsight.server.apm.engine.model.ServiceErrorDO;
import io.holoinsight.server.apm.engine.model.ServiceRelationDO;
import io.holoinsight.server.apm.engine.model.SpanDO;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * created at 2024/2/5
 *
 * @author xzchaoo
 */
@Slf4j
public class SpanStorageHookManager {
  @Autowired(required = false)
  private List<SpanStorageHook> hooks = new ArrayList<>();

  public void beforeStorage(List<SpanDO> spans) {
    if (spans == null || spans.isEmpty()) {
      return;
    }

    for (SpanStorageHook hook : hooks) {
      try {
        hook.beforeStorage(spans);
      } catch (Exception e) {
        log.error("SpanStorageHook error", e);
      }
    }
  }

  public void beforeStorageServiceRelation(List<ServiceRelationDO> list) {
    if (list == null || list.isEmpty()) {
      return;
    }

    for (SpanStorageHook hook : hooks) {
      try {
        hook.beforeStorageServiceRelation(list);
      } catch (Exception e) {
        log.error("SpanStorageHook error", e);
      }
    }
  }

  public void beforeStorageServiceError(List<ServiceErrorDO> list) {
    if (list == null || list.isEmpty()) {
      return;
    }

    for (SpanStorageHook hook : hooks) {
      try {
        hook.beforeStorageServiceError(list);
      } catch (Exception e) {
        log.error("SpanStorageHook error", e);
      }
    }
  }
}
