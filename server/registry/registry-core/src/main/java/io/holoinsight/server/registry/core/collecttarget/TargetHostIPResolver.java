/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.collecttarget;

import io.holoinsight.server.registry.core.template.CollectTemplate;
import lombok.Data;

/**
 * 
 * <p>
 * created at 2023/5/11
 *
 * @author xzchaoo
 */
public interface TargetHostIPResolver {
  /**
   * Try to resolve the host ip of target. Returns null if not found.
   * 
   * @param t
   * @param target
   * @return
   */
  Result getHostIP(CollectTemplate t, Target target);

  enum Type {
    AGENT_ID, HOST_IP
  }

  @Data
  class Result {
    private final Type type;
    private final String value;
  }
}
