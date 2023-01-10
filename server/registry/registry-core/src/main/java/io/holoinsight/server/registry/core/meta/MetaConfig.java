/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.meta;

import lombok.Data;
import lombok.Getter;

import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.stereotype.Component;

import com.xzchaoo.commons.basic.config.spring.AbstractConfig;

/**
 * <p>
 * created at 2022/11/1
 *
 * @author zzhb101
 */
@Component
@Getter
public class MetaConfig extends AbstractConfig {
  private volatile Basic basic = new Basic();

  @Override
  protected void refresh(Binder binder) {
    binder.bind("meta", Basic.class).ifBound(x -> basic = x);
  }

  @Data
  public static class Basic {
    private boolean verbose = false;
  }
}
