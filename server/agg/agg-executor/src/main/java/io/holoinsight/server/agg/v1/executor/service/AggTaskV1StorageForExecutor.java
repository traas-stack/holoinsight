/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.service;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.holoinsight.server.agg.v1.executor.executor.XAggTask;

/**
 * <p>
 * created at 2023/9/26
 *
 * @author xzchaoo
 */
public class AggTaskV1StorageForExecutor {
  private final Map<Long, XAggTask> byId = new ConcurrentHashMap<>();
  private final Map<String, XAggTask> byAggId = new ConcurrentHashMap<>();

  public XAggTask get(long id) {
    return byId.get(id);
  }

  public XAggTask get(String aggId) {
    return byAggId.get(aggId);
  }

  public XAggTask remove(String aggId) {
    XAggTask t = byAggId.remove(aggId);
    if (t != null) {
      byId.remove(t.getInner().getId());
    }
    return t;
  }

  public void put(XAggTask aggTask) {
    byId.put(aggTask.getInner().getId(), aggTask);
    byAggId.put(aggTask.getInner().getAggId(), aggTask);
  }

  public void setEpoch(long epoch) {
    for (XAggTask value : byAggId.values()) {
      value.epoch = epoch;
    }
  }

  public int size() {
    return byAggId.size();
  }

  public int removeByEpoch(long epoch) {
    Iterator<XAggTask> iter = byAggId.values().iterator();
    int count = 0;
    while (iter.hasNext()) {
      XAggTask r = iter.next();
      if (r.epoch != epoch) {
        iter.remove();
        byId.remove(r.getInner().getId());
        count++;
      }
    }
    return count;
  }
}
