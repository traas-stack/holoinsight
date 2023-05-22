/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.grpc;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerServiceDefinition;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.holoinsight.server.common.SlowGrpcServerInterceptor;
import io.holoinsight.server.common.SlowGrpcTracer;
import io.holoinsight.server.common.TrafficTracer;
import io.holoinsight.server.common.grpc.SlowGrpcProperties;
import io.holoinsight.server.common.hook.CommonHooksManager;
import io.holoinsight.server.common.threadpool.CommonThreadPools;

/**
 * <p>
 * created at 2022/2/25
 *
 * @author sw1136562366
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Component
public class GatewayServerForAgent implements SmartLifecycle {
  private static final Logger LOGGER = LoggerFactory.getLogger(GatewayServerForAgent.class);
  private Server server;
  @Autowired
  private GatewayProperties gatewayProperties;
  @Autowired(required = false)
  @GatewayGrpcForAgent
  private List<BindableService> services2 = new ArrayList<>();
  @Autowired
  private CommonThreadPools commonThreadPools;
  @Autowired
  private CommonHooksManager commonHooksManager;
  @Autowired
  private SlowGrpcProperties slowGrpcProperties;

  /** {@inheritDoc} */
  @Override
  public void start() {
    try {
      GatewayProperties.Grpc grpc = gatewayProperties.getServer().getGrpc();
      int port = grpc.getPort();
      NettyServerBuilder b = NettyServerBuilder.forPort(port) //
          .executor(commonThreadPools.getRpcServer()) //
          .maxInboundMessageSize(64 * 1024 * 1024); //

      if (slowGrpcProperties.isEnabled()) {
        b.intercept(new SlowGrpcServerInterceptor(new SlowGrpcHandlerImpl(slowGrpcProperties)));
        b.addStreamTracerFactory(new SlowGrpcTracer.Factory());
      }

      if (grpc.isSslEnabled()) {
        InputStream cert = grpc.getServerCert().getInputStream();
        InputStream key = grpc.getServerKey().getInputStream();
        b.useTransportSecurity(cert, key);
        cert.close();
        key.close();
      }

      for (BindableService bs : services2) {
        ServerServiceDefinition ssd = bs.bindService();
        b.addService(ssd);
        commonHooksManager.triggerPublishGrpcHooks(b, ssd);
      }
      b.addStreamTracerFactory(new TrafficTracer.Factory());

      server = b.build();

      server.start();

      LOGGER.info("gateway for agent grpc server listen at 0.0.0.0:{} with ssl {}", //
          port, //
          grpc.isSslEnabled() ? "enabled" : "disabled"); //
    } catch (IOException e) {
      throw new IllegalStateException("fail to start grpc server", e);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void stop() {
    server.shutdown();
    try {
      server.awaitTermination(10, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("wait for grpc server shutdown interrupted", e);
    }
    server = null;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isRunning() {
    return server != null;
  }

  /**
   * <p>
   * restart.
   * </p>
   */
  public synchronized void restart(int delayStart) throws InterruptedException {
    Server server = this.server;
    if (server != null) {
      server.shutdown();
      server.awaitTermination(1, TimeUnit.MINUTES);
      LOGGER.info("[grpcserver] shutdown");
    }
    if (delayStart > 10) {
      delayStart = 10;
    }
    if (delayStart >= 0) {
      TimeUnit.SECONDS.sleep(delayStart);
    }
    start();
  }
}
