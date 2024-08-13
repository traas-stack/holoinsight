/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.dim;

import org.springframework.boot.context.properties.bind.Binder;

import com.xzchaoo.commons.basic.config.spring.AbstractConfig;

import lombok.Data;
import lombok.Getter;

/**
 * <p>
 * created at 2023/8/23
 *
 * @author xzchaoo
 */
@Getter
public class SlsConfig extends AbstractConfig {
  private volatile Basic basic = new Basic();

  @Override
  protected void refresh(Binder binder) {
    binder.bind("sls", Basic.class).ifBound(x -> basic = x);
  }

  @Data
  public static class Basic {
    /**
     * default sls endpoint
     */
    private String endpoint;
    /**
     * default sls ak
     */
    private String ak;
    /**
     * default sls sk
     */
    private String sk;
    private String slsDimTable = "sls_shard";
    private int syncInterval = 60;
  }
}
