/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.dispatcher;

import org.springframework.boot.context.properties.bind.Binder;

import com.xzchaoo.commons.basic.config.spring.AbstractConfig;

import lombok.Data;
import lombok.Getter;

/**
 * <p>
 * created at 2023/9/18
 *
 * @author xzchaoo
 */
@Getter
public class AggConfig extends AbstractConfig {
  private volatile Basic basic = new Basic();

  @Override
  protected void refresh(Binder binder) {
    binder.bind("agg", Basic.class).ifBound(x -> basic = x);
  }

  @Data
  public static class Basic {
  }
}
