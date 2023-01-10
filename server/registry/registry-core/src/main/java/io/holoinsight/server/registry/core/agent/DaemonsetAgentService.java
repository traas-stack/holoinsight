/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.agent;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.holoinsight.server.common.JsonUtils;

/**
 * <p>
 * created at 2022/7/27
 *
 * @author zzhb101
 */
@Component
public class DaemonsetAgentService {
  private static final Logger LOGGER = LoggerFactory.getLogger("AGENT");

  @Autowired
  private AgentStorage agentStorage;
  @Getter
  private volatile State state = new State();

  public void refresh() {
    this.state = new State(1);
  }

  @Getter
  public class State {
    private final Map<DaemonsetAgent.Key, DaemonsetAgent> agents = new HashMap<>();

    State() {}

    State(int ignored) {
      for (Agent a : agentStorage.readonlyLoop()) {
        if (!Agent.MODE_DAEMONSET.equals(a.getJson().getMode())) {
          continue;
        }

        DaemonsetAgent.Key key =
            new DaemonsetAgent.Key(a.getTenant(), a.getJson().getK8s().getHostIP());
        DaemonsetAgent da = new DaemonsetAgent();
        da.setTenant(key.getTenant());
        da.setHostIP(key.getHostIP());
        da.setHostAgentId(a.getId());

        DaemonsetAgent oldDa = agents.get(key);
        if (oldDa == null) {
          // 99% case
          agents.put(key, da);
          continue;
        }

        // 一台机器上出现了2个agents!? 有可能是残留数据, 此时我们选一个最新心跳的agents
        Agent oldA = agentStorage.get(oldDa.getHostAgentId());
        if (oldA == null) {
          agents.put(key, da);
          continue;
        }

        if (a.getLastHeartbeat().after(oldA.getLastHeartbeat())) {
          agents.put(key, da);
          continue;
        }
        LOGGER.warn("[daemonset] key={} has multi agents {} {}", key, JsonUtils.toJson(a),
            JsonUtils.toJson(oldA));
      }
    }
  }
}
