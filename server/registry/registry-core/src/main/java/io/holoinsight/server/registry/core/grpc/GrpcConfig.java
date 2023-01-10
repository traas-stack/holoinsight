/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc;

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
public class GrpcConfig extends AbstractConfig {
  private volatile Agent agent = new Agent();

  @Override
  protected void refresh(Binder binder) {
    binder.bind("grpc.agent", Agent.class).ifBound(x -> agent = x);
  }

  @Data
  public static class Agent {
    /**
     * 是否启动压缩
     */
    private boolean compressionEnabled = true;
    private int heartbeatInterval = 10;
    private int reconnectInterval = 12;
    private int syncConfigInterval = 60;
  }
}
