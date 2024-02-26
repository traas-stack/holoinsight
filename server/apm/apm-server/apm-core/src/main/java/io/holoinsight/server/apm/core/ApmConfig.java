/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.core;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.context.properties.bind.Binder;

import com.xzchaoo.commons.basic.config.spring.AbstractConfig;

import lombok.Data;
import lombok.Getter;

/**
 * <p>
 * created at 2024/2/26
 *
 * @author xzchaoo
 */
@Getter
public class ApmConfig extends AbstractConfig {
  private volatile Materialize materialize = new Materialize();

  @Override
  protected void refresh(Binder binder) {
    binder.bind("apm.materialize", Materialize.class).ifBound(x -> materialize = x);
  }

  @Data
  public static class Materialize {
    private Set<String> blacklist = new HashSet<>();
  }
}
