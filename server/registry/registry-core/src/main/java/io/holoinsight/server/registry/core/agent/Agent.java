/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.agent;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * created at 2022/2/28
 *
 * @author zzhb101
 */
@Getter
@Setter
@ToString
public class Agent {
  public static final String MODE_SIDECAR = "sidecar";
  public static final String MODE_DAEMONSET = "daemonset";
  public static final String MODE_CENTRAL = "central";
  public static final String MODE_CLUSTERAGENT = "clusteragent";
  private String id;
  private String tenant;
  private AgentJson json;
  private Date lastHeartbeat;
}
