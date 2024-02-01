/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;

import com.google.common.annotations.VisibleForTesting;

/**
 * <p>
 * created at 2024/1/10
 *
 * @author xzchaoo
 */
@VisibleForTesting
public class StaticAggMetaService implements AggMetaService {
  private Map<String, List<Map<String, Object>>> metas;

  public StaticAggMetaService(Map<String, List<Map<String, Object>>> metas) {
    this.metas = Objects.requireNonNull(metas);
  }

  @Override
  public List<Map<String, Object>> find(String metaTable, Map<String, Object> condition) {
    List<Map<String, Object>> list = metas.get(metaTable);
    if (CollectionUtils.isEmpty(list)) {
      return Collections.emptyList();
    }

    List<Map<String, Object>> ret = new ArrayList<>();
    metaLoop: for (Map<String, Object> meta : list) {
      for (Map.Entry<String, Object> e : condition.entrySet()) {
        if (!e.getValue().equals(meta.get(e.getKey()))) {
          continue metaLoop;
        }
      }
      ret.add(meta);
    }
    return ret;
  }
}
