/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.groovy;

import com.xzchaoo.commons.basic.config.spring.AbstractConfig;

import org.springframework.boot.context.properties.bind.Binder;

import java.util.UUID;

import lombok.Data;
import lombok.Getter;

/**
 * <p>
 * created at 2022/3/4
 *
 * @author xzchaoo
 */
@Getter
public class GroovyConfig extends AbstractConfig {
  private volatile Basic basic = new Basic();

  @Override
  protected void refresh(Binder binder) {
    // 不用 groovy.basic
    binder.bind("groovy", Basic.class).ifBound(x -> basic = x);
  }

  @Data
  public static class Basic {
    private String token = UUID.randomUUID().toString();
  }
}
