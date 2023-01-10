/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.collecttarget;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Collect Combination
 * <p>
 * 表示一个 template 与 dim 的组合, 在 gaearegistry 里本来叫做 CollectTarget, 感觉这个名字不对于是改了
 * <p>
 * created at 2022/3/1
 *
 * @author zzhb101
 */
@Getter
@Setter
public class CollectTarget {
  private CollectTargetKey key;

  // private long templateId;
  // private long dimId;
  // 有没有办法压成int64?
  // 在配置所属的agent
  // TODO 怎么理解这个值, 它在现有的模式下可能就是值 agentId
  private String refAgent;

  private long dimModifiedTime;
  private long dimVersion;

  public CollectTarget(CollectTargetKey key) {
    this.key = key;
  }

  public CollectTarget() {}

  public void reset() {
    key = null;
    refAgent = null;
  }

  @JsonIgnore
  public long getTemplateId() {
    return key.getTemplateId();
  }

  @JsonIgnore
  public String getDimId() {
    return key.getDimId();
  }
}
