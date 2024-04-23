/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.common.grpc.springboot;

import java.io.IOException;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.SmartLifecycle;

/**
 * <p>
 * created at 2022/12/1
 *
 * @author xzchaoo
 */
@Slf4j
public class GrpcServerLifecycle implements SmartLifecycle {
  private final List<GrpcServer> servers;
  private boolean running;

  public GrpcServerLifecycle(List<GrpcServer> servers) {
    this.servers = servers;
  }

  @Override
  public void start() {
    running = true;

    for (GrpcServer server : servers) {
      try {
        server.start();
      } catch (IOException e) {
        throw new IllegalStateException("fail to start grpc server", e);
      }
    }
  }

  @Override
  public void stop() {
    running = false;
    for (GrpcServer server : servers) {
      try {
        server.stop();
      } catch (InterruptedException e) {
        log.error("fail to stop grpc server gracefully", e);
      }
    }
  }

  @Override
  public boolean isRunning() {
    return running;
  }
}
