/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.otlp.server;

import org.springframework.boot.context.properties.bind.Binder;

import com.xzchaoo.commons.basic.config.spring.AbstractConfig;

import lombok.Data;
import lombok.Getter;

/**
 * <p>
 * created at 2024/4/8
 *
 * @author xzchaoo
 */
@Getter
public class OtlpConfig extends AbstractConfig {
  private volatile Trace trace = new Trace();

  @Override
  protected void refresh(Binder binder) {
    binder.bind("otlp.trace", Trace.class).ifBound(x -> trace = x);
  }

  @Data
  public static class Trace {
    /**
     * If false, the processing of trace data will be skipped.
     */
    private boolean enabled = true;
  }

}
