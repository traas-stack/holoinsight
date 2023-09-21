/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.template;

import java.time.Duration;

import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.stereotype.Component;

import com.xzchaoo.commons.basic.config.spring.AbstractConfig;

import lombok.Data;
import lombok.Getter;

/**
 * <p>
 * created at 2022/3/1
 *
 * @author zzhb101
 */
@Component
@Getter
public class TemplateConfig extends AbstractConfig {
  private volatile Sync sync = new Sync();
  private volatile Build build = new Build();

  @Override
  protected void refresh(Binder binder) {
    binder.bind("template.sync", Sync.class).ifBound(x -> sync = x);
    binder.bind("template.build", Build.class).ifBound(x -> build = x);
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
    private Duration fullInterval = Duration.ofMinutes(1);
  }

  @Data
  public static class Build {
    private Duration interval = Duration.ofMinutes(1);
    // Timeout for build template
    private Duration timeout = Duration.ofSeconds(30);
  }
}
