/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.agent;

import java.util.Map;

/**
 * <p>
 * created at 2022/2/28
 *
 * @author zzhb101
 */
public class TenantAgentStorage {
  private String tenant;
  /**
   * 通过 uuid 唯一识别一个agent
   */
  private Map<String, Agent> byUuid;
}
