/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc;

import java.io.InputStream;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.holoinsight.server.registry.core.RegistryProperties;
import io.holoinsight.server.registry.grpc.agent.RegistryServiceForAgentGrpc;

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
 * @author zzhb101
 */
@ConditionalOnEnabledHealthIndicator("registryServerForAgent")
@Component
public class RegistryServerForAgentHealthIndicator extends AbstractHealthIndicator {
  @Autowired
  private RegistryProperties registryProperties;

  @Override
  protected void doHealthCheck(Health.Builder builder) throws Exception {
    RegistryProperties.ForAgent forAgent = registryProperties.getServer().getForAgent();
    int port = forAgent.getPort();

    NettyChannelBuilder b = NettyChannelBuilder.forAddress("127.0.0.1", port);
    if (forAgent.isSslEnabled()) {
      InputStream is = forAgent.getCaCert().getInputStream();
      b.sslContext(GrpcSslContexts.forClient().trustManager(is).build());
      is.close();
    } else {
      b.usePlaintext();
    }
    ManagedChannel channel = b.build();
    try {
      RegistryServiceForAgentGrpc.newBlockingStub(channel).ping(Empty.getDefaultInstance());
      builder.up().withDetail("ssl", forAgent.isSslEnabled()).build();
    } catch (Throwable e) {
      builder.down(e);
      throw e;
    } finally {
      channel.shutdown();
    }
  }
}
