/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.agent;

import lombok.Data;

/**
 * <p>
 * created at 2022/7/20
 *
 * @author zzhb101
 */
@Data
public class DaemonsetAgent {
  private String tenant;
  private String hostIP;
  // TODO 云上环境太脏会有冲突
  private String hostAgentId;

  @Data
  public static class Key {
    private final String tenant;
    private final String hostIP;
  }
}
