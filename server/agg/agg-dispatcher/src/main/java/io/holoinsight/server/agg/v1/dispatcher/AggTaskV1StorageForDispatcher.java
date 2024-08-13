/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.dispatcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import io.holoinsight.server.agg.v1.core.conf.AggTask;
import io.holoinsight.server.agg.v1.core.conf.FromConfigs;
import io.holoinsight.server.agg.v1.core.conf.FromMetrics;
import lombok.Getter;

/**
 * <p>
 * created at 2023/9/26
 *
 * @author xzchaoo
 */
public class AggTaskV1StorageForDispatcher {
  @Getter
  private volatile State state = new State();

  public AggTask get(String aggId) {
    return state.aggTasks.get(aggId);
  }

  public List<AggTask> getByMetric(String metric) {
    List<AggTask> ret = state.byMetric.get(metric);
    if (ret == null) {
      ret = Collections.emptyList();
    }
    return ret;
  }

  public List<AggTask> getByTableName(String tableName) {
    List<AggTask> ret = state.byTableName.get(tableName);
    if (ret == null) {
      ret = Collections.emptyList();
    }
    return ret;
  }

  public void replace(Map<String, AggTask> m) {
    this.state = new State(m);
  }

  public void replace(List<AggTask> list) {
    this.state = new State(list);
  }

  public int size() {
    return state.aggTasks.size();
  }


  public static class State {
    public final Map<String, AggTask> aggTasks;
    public final Map<String, List<AggTask>> byMetric = new HashMap<>();
    public final Map<String, List<AggTask>> byTableName = new HashMap<>();

    private State() {
      aggTasks = new HashMap<>();
    }

    public State(Map<String, AggTask> aggTasks) {
      this.aggTasks = aggTasks;
      for (AggTask rule : aggTasks.values()) {
        FromMetrics fm = rule.getFrom().getMetrics();
        if (fm != null && fm.getMetrics() != null) {
          for (String metric : fm.getMetrics()) {
            byMetric.computeIfAbsent(metric, i -> new ArrayList<>()).add(rule);
          }
        }
        FromConfigs fc = rule.getFrom().getConfigs();
        if (fc != null && fc.getTableNames() != null) {
          for (String tableName : fc.getTableNames()) {
            byTableName.computeIfAbsent(tableName, i -> new ArrayList<>()).add(rule);
          }
        }
      }
    }

    public State(Collection<AggTask> aggTasks) {
      this.aggTasks = Maps.newHashMapWithExpectedSize(aggTasks.size());
      for (AggTask rule : aggTasks) {
        this.aggTasks.put(rule.getAggId(), rule);
        FromMetrics fm = rule.getFrom().getMetrics();
        if (fm != null && fm.getMetrics() != null) {
          for (String metric : fm.getMetrics()) {
            byMetric.computeIfAbsent(metric, i -> new ArrayList<>()).add(rule);
          }
        }

        FromConfigs fc = rule.getFrom().getConfigs();
        if (fc != null && fc.getTableNames() != null) {
          for (String tableName : fc.getTableNames()) {
            byTableName.computeIfAbsent(tableName, i -> new ArrayList<>()).add(rule);
          }
        }
      }
    }
  }
}
