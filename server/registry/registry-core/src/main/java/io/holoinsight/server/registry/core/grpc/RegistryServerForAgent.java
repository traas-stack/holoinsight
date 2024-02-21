/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;
import io.holoinsight.server.common.TrafficTracer;
import io.holoinsight.server.common.hook.CommonHooksManager;
import io.holoinsight.server.registry.core.RegistryProperties;

/**
 * <p>
 * Registry server for agent.
 * 
 * created at 2022/2/28
 *
 * @author zzhb101
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Component
public class RegistryServerForAgent {
  private static final Logger LOGGER = LoggerFactory.getLogger(RegistryServerForAgent.class);
  /**
   * Default 'maxInboundMessageSize' of registry grpc server. Set to 64MB.
   */
  private static final int DEFAULT_MAX_INBOUND_MESSAGE_SIZE = 64 * 1024 * 1024;

  @Autowired
  private RegistryProperties registryProperties;
  private Server server;
  @Autowired(required = false)
  @RegistryGrpcForAgent
  private List<ServerServiceDefinition> services1 = new ArrayList<>();
  @Autowired(required = false)
  @RegistryGrpcForAgent
  private List<BindableService> services2 = new ArrayList<>();
  private ThreadPoolExecutor executor;
  @Autowired
  private CommonHooksManager commonHooksManager;

  public void start() throws IOException {
    RegistryProperties.ForAgent forAgent = registryProperties.getServer().getForAgent();

    ServerBuilder<?> b = ServerBuilder.forPort(forAgent.getPort())
        .maxInboundMessageSize(DEFAULT_MAX_INBOUND_MESSAGE_SIZE);
    // b.intercept(new CompetibleServerInterceptor());

    if (forAgent.isSslEnabled()) {
      if (forAgent.getServerCert() == null || !forAgent.getServerCert().exists()) {
        throw new IllegalStateException(
            "file not found serverCert=[" + forAgent.getServerCert() + "]");
      }
      if (forAgent.getServerKey() == null || !forAgent.getServerKey().exists()) {
        throw new IllegalStateException(
            "file not found serverKey=[" + forAgent.getServerKey() + "]");
      }
      InputStream certIs = forAgent.getServerCert().getInputStream();
      InputStream keyIs = forAgent.getServerKey().getInputStream();
      b.useTransportSecurity(certIs, keyIs);
      certIs.close();
      keyIs.close();
    }
    for (ServerServiceDefinition ssd : services1) {
      LOGGER.info("[registryserver] [foragent] add service {}",
          ssd.getServiceDescriptor().getName());
      b.addService(ssd);
    }
    for (BindableService bs : services2) {
      ServerServiceDefinition ssd = bs.bindService();
      LOGGER.info("[registryserver] [foragent] add service {}",
          ssd.getServiceDescriptor().getName());
      b.addService(ssd);
      commonHooksManager.triggerPublishGrpcHooks(b, ssd);
    }

    // 专用连接池
    int size = Runtime.getRuntime().availableProcessors() * 2;
    executor = new ThreadPoolExecutor(size, //
        size, //
        0, TimeUnit.MINUTES, //
        new ArrayBlockingQueue<>(65536), //
        new ThreadFactoryBuilder().setNameFormat("grpc-for-agent-%d").build(), //
        new ThreadPoolExecutor.AbortPolicy());

    b.executor(executor);
    b.addStreamTracerFactory(new TrafficTracer.Factory());
    server = b.build();
    server.start();

    LOGGER.info("[registryserver] [foragent] start grpc server at 0.0.0.0:{} with ssl {}", //
        forAgent.getPort(), //
        forAgent.isSslEnabled() ? "enabled" : "disabled"); //
  }

  @PreDestroy
  public void stop() {
    ThreadPoolExecutor executor = this.executor;
    this.executor = null;
    if (executor != null) {
      executor.shutdown();
    }
    Server server = this.server;
    this.server = null;
    if (server != null) {
      server.shutdown();
    }
  }

}
