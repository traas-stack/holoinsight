/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.agent;

import lombok.Data;

/**
 * <p>
 * created at 2022/7/19
 *
 * @author zzhb101
 */
@Data
public class AgentK8sConfig {
  private String hostIP;
  private String namespace;
  private String pod;
  private String nodeHostname;
}
