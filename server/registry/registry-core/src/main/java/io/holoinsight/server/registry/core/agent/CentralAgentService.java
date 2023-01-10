/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.agent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import lombok.Data;
import lombok.Getter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xzchaoo.commons.basic.consistenthash.DefaultConsistentHash;
import com.xzchaoo.commons.basic.consistenthash.NodeFunction;

/**
 * <p>
 * created at 2022/6/21
 *
 * @author zzhb101
 */
@Service
public class CentralAgentService {
  private static final int VNODE_COUNT = 16;
  private static final NodeFunction<Agent> NF = new NodeFunction<Agent>() {
    @Override
    public int compare(Agent a, Agent b) {
      return a.getJson().getHostname().compareTo(b.getJson().getHostname());
    }

    @Override
    public int hash(Agent a) {
      return a.getJson().getHostname().hashCode();
    }
  };

  @Autowired
  private AgentStorage agentStorage;
  private volatile State state = new State();

  public void refresh() {
    State newState = new State();
    Date expireTime = new Date(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(10));

    for (Agent a : agentStorage.readonlyLoop()) {
      if (a.getLastHeartbeat().after(expireTime)
          && Agent.MODE_CENTRAL.equals(a.getJson().getMode())) {
        String cluster = a.getJson().getLabels().get("central.name");
        newState.clusters.computeIfAbsent(cluster, ClusterState::new).add(a);
      }
    }
    newState.build();
    this.state = newState;
  }

  public State getState() {
    // TODO state 由master维护, 写入DB, 其他机器找master同步
    return state;
  }

  @Data
  public static class State {
    private Map<String, ClusterState> clusters = new HashMap<>();

    public void build() {
      Date expireTime = new Date(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(10));
      for (ClusterState c : clusters.values()) {
        c.agents.values().removeIf(
            a -> a.getLastHeartbeat().before(expireTime) || a.getJson().getHostname() == null);
        c.build();
      }
    }
  }

  @Getter
  public static class ClusterState {
    private final String name;
    private final Map<String, Agent> agents = new HashMap<>();
    private DefaultConsistentHash<Agent> ring;

    ClusterState(String name) {
      this.name = name;
    }

    void add(Agent a) {
      // hostname 一样的只保留一个最新的
      Agent byHostname = agents.get(a.getJson().getHostname());
      // 或者取心跳更新者
      if (byHostname == null || a.getLastHeartbeat().after(byHostname.getLastHeartbeat())) {
        agents.put(a.getJson().getHostname(), a);
      }
    }

    void build() {
      ArrayList<Agent> agents = new ArrayList<>(this.agents.values());
      agents.sort(Comparator.comparing(e -> e.getJson().getHostname()));
      ring = new DefaultConsistentHash<>(VNODE_COUNT, NF, agents);
    }
  }

}
