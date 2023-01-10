/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.grpc;

import java.io.InputStream;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.holoinsight.server.gateway.grpc.GatewayServiceGrpc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

import com.google.protobuf.Empty;

/**
 * <p>
 * created at 2022/2/28
 *
 * @author sw1136562366
 */
@ConditionalOnEnabledHealthIndicator("gatewayServerForAgent")
@Component
public class GatewayServerForAgentHealthIndicator extends AbstractHealthIndicator {

  @Autowired
  private GatewayProperties gatewayProperties;

  /** {@inheritDoc} */
  @Override
  protected void doHealthCheck(Health.Builder builder) throws Exception {
    GatewayProperties.Grpc grpc = gatewayProperties.getServer().getGrpc();
    int port = grpc.getPort();
    NettyChannelBuilder b = NettyChannelBuilder.forAddress("127.0.0.1", port);
    if (grpc.isSslEnabled()) {
      InputStream cert = grpc.getCaCert().getInputStream();
      b.sslContext(GrpcSslContexts.forClient().trustManager(cert).build());
      cert.close();
    } else {
      b.usePlaintext();
    }
    ManagedChannel channel = b.build();
    try {
      GatewayServiceGrpc.newBlockingStub(channel).ping(Empty.getDefaultInstance());
      builder.up().withDetail("ssl", grpc.isSslEnabled()).build();
    } catch (Throwable e) {
      builder.down(e);
      throw e;
    } finally {
      channel.shutdown();
    }
  }
}
