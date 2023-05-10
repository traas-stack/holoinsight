/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.collecttarget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import io.holoinsight.server.registry.core.agent.MapSetUtils;
import com.google.common.collect.Maps;

/**
 * <p>
 * created at 2022/3/1
 *
 * @author zzhb101
 */
@Component
public class CollectTargetStorage {
  private final Map<CollectTargetKey, CollectTarget> byKey = new ConcurrentHashMap<>();

  private final Map<String, Set<CollectTargetKey>> byAgent = new ConcurrentHashMap<>();
  private final Map<Long, Set<CollectTargetKey>> byTemplate = new ConcurrentHashMap<>();

  public CollectTarget get(CollectTargetKey key) {
    return byKey.get(key);
  }

  public Set<CollectTargetKey> getKeysByAgent(String agent) {
    Set<CollectTargetKey> keys = byAgent.get(agent);
    if (keys != null) {
      return new HashSet<>(keys);
    } else {
      return new HashSet<>();
    }
  }

  public List<CollectTarget> getByAgent(String agent) {
    Set<CollectTargetKey> keys = byAgent.get(agent);
    if (keys == null) {
      return new ArrayList<>();
    }
    List<CollectTarget> list = new ArrayList<>(keys.size());
    for (CollectTargetKey key : keys) {
      CollectTarget ct = byKey.get(key);
      if (ct != null) {
        list.add(ct);
      }
    }
    return list;
  }

  public int countByTemplate(long id) {
    Set<CollectTargetKey> set = byTemplate.get(id);
    if (set == null) {
      return 0;
    }
    return set.size();
  }

  public Map<String, Integer> countByEveryAgent() {
    Map<String, Integer> ret = Maps.newHashMapWithExpectedSize(byAgent.size());
    for (Map.Entry<String, Set<CollectTargetKey>> e : byAgent.entrySet()) {
      ret.put(e.getKey(), e.getValue().size());
    }
    return ret;
  }

  public Set<CollectTargetKey> getKeysByTemplateId(long templateId) {
    return byTemplate.get(templateId);
  }

  /**
   * 这里返回的是一个可修改的hashmap
   *
   * @param templateId
   * @return
   */
  public Map<CollectTargetKey, CollectTarget> getMapByTemplateId(long templateId) {
    Set<CollectTargetKey> keys = byTemplate.get(templateId);
    if (keys == null) {
      return new HashMap<>();
    }
    Map<CollectTargetKey, CollectTarget> map = Maps.newHashMapWithExpectedSize(keys.size());
    for (CollectTargetKey key : keys) {
      CollectTarget t = byKey.get(key);
      if (t != null) {
        map.put(key, t);
      }
    }
    return map;
  }

  public List<CollectTarget> getByTemplateId(long templateId) {
    Set<CollectTargetKey> keys = byTemplate.get(templateId);
    if (keys == null) {
      return new ArrayList<>();
    }
    List<CollectTarget> list = new ArrayList<>(keys.size());
    for (CollectTargetKey key : keys) {
      CollectTarget t = byKey.get(key);
      if (t != null) {
        list.add(t);
      }
    }
    return list;
  }

  /**
   * 批量放一些combs, 这些combs属于同一个templateId
   *
   * @param templateId
   * @param combs
   */
  public void batchPut(long templateId, List<CollectTarget> combs) {
    Set<CollectTargetKey> templateKeys =
        byTemplate.computeIfAbsent(templateId, i -> MapSetUtils.newCHS());
    for (CollectTarget t : combs) {
      CollectTargetKey key = t.getKey();

      this.delete(key);

      byKey.put(key, t);
      templateKeys.add(key);
      MapSetUtils.addMapSetKey(byAgent, t.getRefAgent(), key);
    }
  }

  /**
   * 批量放一些combs
   *
   * @param combs
   */
  // public void batchPut(List<CollectTarget> combs) {
  // for (CollectTarget t : combs) {
  // CollectTargetKey key = t.getKey();
  // CollectTarget old = byKey.put(key, t);
  // if (old != null) {
  // if (old.getRefAgent().equals(t.getRefAgent())) {
  // MapSetUtils.deleteMapSetKey(byAgent, old.getRefAgent(), key);
  // MapSetUtils.addMapSetKey(byAgent, t.getRefAgent(), key);
  // }
  // // 这个没必要操作, 因为此时它一定已经存在
  // // addMapSetKey(byTemplate, t.getTemplateId(), key);
  // } else {
  // MapSetUtils.addMapSetKey(byTemplate, t.getTemplateId(), key);
  // MapSetUtils.addMapSetKey(byAgent, t.getRefAgent(), key);
  // }
  // }
  // }

  /**
   * 按 templateId 删除, 返回删掉的数量
   *
   * @param templateId
   * @return
   */
  public int deleteByTemplateId(long templateId) {
    Set<CollectTargetKey> keys = byTemplate.remove(templateId);
    if (keys != null) {
      for (CollectTargetKey key : keys) {
        CollectTarget t = byKey.remove(key);
        if (t != null) {
          MapSetUtils.deleteMapSetKey(byAgent, t.getRefAgent(), key);
        }
      }
      return keys.size();
    }
    return 0;
  }

  public void batchDelete(Collection<CollectTargetKey> keys) {
    keys.forEach(this::delete);
  }

  public void batchDelete(long templateId, Collection<CollectTargetKey> keys) {
    Set<CollectTargetKey> templateKeys = byTemplate.get(templateId);
    if (templateKeys == null) {
      return;
    }
    for (CollectTargetKey key : keys) {
      CollectTarget t = byKey.remove(key);
      if (t == null) {
        continue;
      }
      templateKeys.remove(key);

      MapSetUtils.deleteMapSetKey(byAgent, t.getRefAgent(), key);
    }
  }

  public void delete(CollectTargetKey key) {
    CollectTarget t = byKey.remove(key);
    if (t == null) {
      return;
    }

    MapSetUtils.deleteMapSetKey(byTemplate, t.getTemplateId(), key);
    MapSetUtils.deleteMapSetKey(byAgent, t.getRefAgent(), key);
  }

  /**
   * 上层调用该方法时必须保证此时没有其他线程在调用其他的put方法, 否则可能会出问题 清理无用的 byAgents
   */
  public void cleanBadData() {
    byAgent.entrySet().removeIf(e -> e.getValue().isEmpty());
  }

  public boolean hasTemplateId(long templateId) {
    return byTemplate.containsKey(templateId);
  }

  public Collection<CollectTarget> list() {
    return byKey.values();
  }

  public int countByAgent(String agent) {
    Set<CollectTargetKey> set = byAgent.get(agent);
    return set != null ? set.size() : 0;
  }

  public int size() {
    return byKey.size();
  }
}
