/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.common.grpc.springboot;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;

/**
 * <p>
 * created at 2022/12/1
 *
 * @author xzchaoo
 */
public class GrpcServer {
  private int port;
  private List<BindableService> services;
  private Server server;

  public void start() throws IOException {
    NettyServerBuilder b =
        NettyServerBuilder.forPort(port).maxInboundMessageSize(100 * 1024 * 1024);
    server = b.build();
    for (BindableService bs : services) {
      b.addService(bs);
    }
    server.start();
  }

  public void stop() throws InterruptedException {
    server.shutdown();
    server.awaitTermination(10, TimeUnit.SECONDS);
  }
}
