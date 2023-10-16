/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor;

import org.springframework.boot.context.properties.bind.Binder;

import com.xzchaoo.commons.basic.config.spring.AbstractConfig;

import lombok.Data;
import lombok.Getter;

/**
 * <p>
 * created at 2023/10/16
 *
 * @author xzchaoo
 */
@Getter
public class AggExecutorConfig extends AbstractConfig {
  private volatile Debug debug = new Debug();

  @Override
  protected void refresh(Binder binder) {
    binder.bind("agg.executor.debug", Debug.class).ifBound(x -> debug = x);
  }

  @Data
  public static class Debug {
    private boolean enabled = false;
  }
}
