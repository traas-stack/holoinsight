/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.holoinsight.server.common.NetUtils;
import io.holoinsight.server.registry.core.utils.ApiResp;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * created at 2022/2/28
 *
 * @author zzhb101
 */
@RestController
@RequestMapping("/internal/api/registry/agent")
public class AgentWebController {
  @Autowired
  private AgentStorage agentStorage;

  @Autowired
  private AgentService agentService;

  /**
   * list all agents
   *
   * @param tenant
   * @return
   */
  @GetMapping("/list")
  public Object list(@RequestParam(value = "tenant", required = false) String tenant) {
    List<Agent> ret = new ArrayList<>();
    if (StringUtils.isNotEmpty(tenant)) {
      for (Agent a : agentStorage.readonlyLoop()) {
        if (tenant.equals(a.getTenant())) {
          ret.add(a);
        }
      }
    } else {
      ret = new ArrayList<>(agentStorage.list());
    }
    return ApiResp.success(ret);
  }

  /**
   * list all agents connecting to current registry instance
   *
   * @param tenant
   * @return
   */
  @GetMapping("/listConnecting")
  public Object listConnecting(@RequestParam(value = "tenant", required = false) String tenant) {
    List<Agent> ret = new ArrayList<>();
    for (Agent a : agentStorage.readonlyLoop()) {
      if (StringUtils.isNotEmpty(tenant)) {
        if (!tenant.equals(a.getTenant())) {
          continue;
        }
      }
      ConnectionInfo ci = a.getJson().getConnectionInfo();
      if (ci == null) {
        continue;
      }
      if (!ci.getRegistry().equals(NetUtils.getLocalIp())) {
        continue;
      }
      ret.add(a);
    }
    return ApiResp.success(ret);
  }

  @GetMapping("/get")
  public Object get(@RequestParam("a") String a) {
    return ApiResp.resource(agentService.getDebugInfo(a));
  }

  @GetMapping("/countByTenant")
  public Object countByTenant() {
    Map<String, Map<String, int[]>> m = new HashMap<>();
    for (Agent a : agentStorage.readonlyLoop()) {

      Map<String, int[]> tm = m.computeIfAbsent(a.getTenant(), i -> new TreeMap<>());
      tm.computeIfAbsent("_ALL", i -> new int[] {0})[0]++;
      tm.computeIfAbsent(a.getJson().getVersion(), i -> new int[] {0})[0]++;
    }

    // @Data
    // class Temp {
    // final int online;
    // final int total;
    // }

    return m;
  }

  @GetMapping("/markDeleteExpiredAgents")
  public Object markDeleteExpiredAgents() {
    agentService.markDeleteExpiredAgents();
    return "OK";
  }

}
