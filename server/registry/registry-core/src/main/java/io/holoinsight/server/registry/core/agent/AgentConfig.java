/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.agent;

import java.time.Duration;

import lombok.Data;
import lombok.Getter;

import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.stereotype.Component;

import com.xzchaoo.commons.basic.config.spring.AbstractConfig;

/**
 * <p>
 * created at 2022/3/9
 *
 * @author zzhb101
 */
@Component
@Getter
public class AgentConfig extends AbstractConfig {
  private volatile Sync sync = new Sync();
  private volatile Maintain maintain = new Maintain();
  private volatile K8s k8s = new K8s();
  private volatile Meta meta = new Meta();

  @Override
  protected void refresh(Binder binder) {
    binder.bind("agent.sync", Sync.class).ifBound(x -> sync = x);
    binder.bind("agent.maintain", Maintain.class).ifBound(x -> maintain = x);
    binder.bind("agent.k8s", K8s.class).ifBound(x -> k8s = x);
    binder.bind("agent.meta", Meta.class).ifBound(x -> meta = x);
  }

  @Data
  public static class Sync {
    /**
     * 增量同步时间
     */
    private Duration interval = Duration.ofSeconds(5);
    /**
     * 增量同步延迟
     */
    private Duration delay = Duration.ofSeconds(5);
    /**
     * 全量同步时间
     */
    private Duration fullInterval = Duration.ofMinutes(10);
  }

  @Data
  public static class Maintain {
    /**
     * Agent maintain task is enabled after startup 'readySeconds'.
     */
    private int readySeconds = 3600;

    private Duration interval = Duration.ofMinutes(10);
    /**
     * 多久时间没有心跳就认为挂掉
     */
    private Duration expire = Duration.ofHours(2);
  }

  @Data
  public static class K8s {
    private boolean registerK8sAgent = false;
  }

  /**
   * 是否同步配置到meta集群
   */
  @Data
  public static class Meta {
    private boolean enabled = true;
  }
}
