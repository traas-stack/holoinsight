/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.template;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import io.holoinsight.server.registry.core.agent.MapSetUtils;

/**
 * 相关代码必须确保对 TemplateStorage 的修改是一写多读的. 写的地方仅允许发生在 CollectTemplateSyncer 里
 * <p>
 * created at 2022/2/28
 *
 * @author zzhb101
 */
@Component
public class TemplateStorage {
  // TODO 这几个索引会用到吗, 不会的话就删了

  /**
   * by id 的索引
   */
  private final ConcurrentHashMap<Long, CollectTemplate> byId = new ConcurrentHashMap<>();

  /**
   * by tableName 的索引
   */
  private final ConcurrentHashMap<String, Set<Long>> byTableName = new ConcurrentHashMap<>();

  public CollectTemplate get(long id) {
    return byId.get(id);
  }

  public Map<Long, CollectTemplate> getAsMap() {
    return new HashMap<>(byId);
  }

  public Collection<CollectTemplate> internalGetAllTemplates() {
    return byId.values();
  }

  public Set<Long> get(String tableName) {
    return byTableName.get(tableName);
  }

  public CollectTemplate delete(long id) {
    CollectTemplate t = byId.remove(id);
    if (t != null) {
      MapSetUtils.deleteMapSetKey(byTableName, t.getTableName(), id);
    }
    return t;
  }

  public void put(CollectTemplate t) {
    byId.put(t.getId(), t);
    MapSetUtils.addMapSetKey(byTableName, t.getTableName(), t.getId());
  }

  public void cleanBadData() {
    byTableName.values().removeIf(Set::isEmpty);
  }

  public Iterable<CollectTemplate> readonlyLoop() {
    return byId.values();
  }

  public int size() {
    return byId.size();
  }

  public Map<String, Integer> getTenantCountMap() {
    Map<String, Integer> m = new HashMap<>();
    this.byId.forEach((id, t) -> {
      m.put(t.getTenant(), m.getOrDefault(t.getTenant(), 0) + 1);
    });
    return m;
  }
}
