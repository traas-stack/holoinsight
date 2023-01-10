/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.collecttarget;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import io.holoinsight.server.registry.core.agent.Agent;

/**
 * <p>
 * created at 2022/4/19
 *
 * @author zzhb101
 */
@Data
public class CollectTargetDebugInfo {
  private String tenant;
  private Template template = new Template();
  private Dim dim = new Dim();
  private Agent agent = new Agent();
  private List<String> cmds = new ArrayList<>();

  public CollectTargetDebugInfo addCmd(String format, Object... args) {
    cmds.add(String.format(format, args));
    return this;
  }

  @Data
  public static class Template {
    private long id;
    private String tableName;
    private Object collectRange;
    private int targets;
  }

  @Data
  public static class Dim {
    private String id;
    private Object raw;
  }
  //
  // @Data
  // public static class Agent {
  // private String agentId;
  // }
}
