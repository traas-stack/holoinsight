/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.agent;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * <p>
 * created at 2022/2/28
 *
 * @author zzhb101
 */
@Component
public class AgentStorage {
  private final Map<String, Agent> byId = new ConcurrentHashMap<>();
  private final Map<String, Set<String>> byTenant = new ConcurrentHashMap<>();

  /**
   * key是agent id, value是与agent正在建联的reg的id(一般时用ip)
   */
  private final Cache<String, String> connecting = Caffeine.newBuilder() //
      .expireAfterWrite(Duration.ofMinutes(10)) //
      .build();//

  public AgentStorage() {}

  public void putConnecting(String agentId, String regId) {
    connecting.put(agentId, regId);
  }

  public String getConnecting(String agentId) {
    return connecting.getIfPresent(agentId);
  }

  public Agent get(String id) {
    return byId.get(id);
  }

  public void put(Agent a) {
    // TODO tenant 会不会改变
    Agent old = byId.put(a.getId(), a);
    if (old != null) {
      if (!old.getTenant().equals(a.getTenant())) {
        MapSetUtils.deleteMapSetKey(byTenant, old.getTenant(), a.getId());
        MapSetUtils.addMapSetKey(byTenant, a.getTenant(), a.getId());
      }
    } else {
      MapSetUtils.addMapSetKey(byTenant, a.getTenant(), a.getId());
    }
  }

  public Agent delete(String id) {
    Agent a = byId.remove(id);
    if (a != null) {
      MapSetUtils.deleteMapSetKey(byTenant, a.getTenant(), a.getId());
    }
    return a;
  }

  public void cleanBadData() {
    byTenant.entrySet().removeIf(e -> e.getValue().isEmpty());
  }

  public int size() {
    return byId.size();
  }

  public Collection<Agent> list() {
    return byId.values();
  }

  public Iterable<Agent> readonlyLoop() {
    return byId.values();
  }

  public Collection<Agent> readonlyList() {
    return byId.values();
  }

  public int keep(Set<String> ids) {
    int del = 0;
    Iterator<Map.Entry<String, Agent>> iter = byId.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry<String, Agent> e = iter.next();
      if (!ids.contains(e.getKey())) {
        ++del;
        Agent a = e.getValue();
        iter.remove();
        MapSetUtils.deleteMapSetKey(byTenant, a.getTenant(), a.getId());
      }
    }
    return del;
  }

  public Map<String, Integer> getConnectionTenantCountMap() {
    Map<String, Integer> m = new HashMap<>();
    for (String agentId : this.connecting.asMap().keySet()) {
      Agent a = this.byId.get(agentId);
      if (a != null) {
        m.put(a.getTenant(), m.getOrDefault(a.getTenant(), 0) + 1);
      }
    }
    return m;
  }

  public Map<String, Integer> getTenantCountMap() {
    Map<String, Integer> m = new HashMap<>();
    this.byId.forEach((id, a) -> {
      m.put(a.getTenant(), m.getOrDefault(a.getTenant(), 0) + 1);
    });
    return m;
  }
}
