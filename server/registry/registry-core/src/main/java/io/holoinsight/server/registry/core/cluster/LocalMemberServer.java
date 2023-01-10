/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.cluster;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.holoinsight.server.registry.core.grpc.RegistryServiceForInternalImpl;

/**
 * <p>
 * created at 2022/4/17
 *
 * @author zzhb101
 */
public class LocalMemberServer {
  private static final Logger LOGGER = LoggerFactory.getLogger(LocalMemberServer.class);

  private final int port;
  private Server server;

  public LocalMemberServer(int port, RegistryServiceForInternalImpl service, Executor executor) {
    this.port = port;
    this.server = ServerBuilder.forPort(port) //
        .addService(service) //
        .executor(executor) //
        .build(); //
  }

  public void start() throws IOException {
    server.start();
  }

  public void stop() {
    server.shutdownNow();
    try {
      server.awaitTermination(10, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      LOGGER.warn("await server termination interrupted", e);
    }
  }
}
