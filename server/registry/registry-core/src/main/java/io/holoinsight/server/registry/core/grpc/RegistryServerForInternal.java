/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;
import io.holoinsight.server.common.threadpool.CommonThreadPools;
import io.holoinsight.server.registry.core.RegistryProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * created at 2022/2/28
 *
 * @author zzhb101
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class RegistryServerForInternal {
  private static final Logger LOGGER = LoggerFactory.getLogger(RegistryServerForInternal.class);

  @Autowired
  private RegistryProperties registryProperties;
  private Server server;

  @Autowired(required = false)
  @GrpcForInternal
  private List<BindableService> services2 = new ArrayList<>();

  @Autowired
  private CommonThreadPools commonThreadPools;

  @PostConstruct
  public void start() throws IOException {
    RegistryProperties.ForInternal forInternal = registryProperties.getServer().getForInternal();

    ServerBuilder<?> b =
        ServerBuilder.forPort(forInternal.getPort() + 3).executor(commonThreadPools.getRpcServer());

    for (BindableService bs : services2) {
      ServerServiceDefinition ssd = bs.bindService();
      LOGGER.info("[registryserver] [forinternal] add service {}",
          ssd.getServiceDescriptor().getName());
      b.addService(ssd);
    }

    server = b.build();
    server.start();

    LOGGER.info("[registryserver] [forinternal] start grpc server at 0.0.0.0:{}", //
        forInternal.getPort()); //
  }

  @PreDestroy
  public void stop() {
    server.shutdown();
  }
}
