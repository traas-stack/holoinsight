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
import io.holoinsight.server.common.hook.CommonHooksManager;
import io.holoinsight.server.common.threadpool.CommonThreadPools;
import io.holoinsight.server.registry.core.RegistryProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 * created at 2022/2/28
 *
 * @author zzhb101
 */
@Component
public class RegistryServerForProd {
  private static final Logger LOGGER = LoggerFactory.getLogger(RegistryServerForProd.class);

  @Autowired
  private RegistryProperties registryProperties;
  private Server server;

  @Autowired(required = false)
  @RegistryGrpcForProd
  private List<BindableService> services2 = new ArrayList<>();

  @Autowired
  private CommonThreadPools commonThreadPools;
  @Autowired
  private CommonHooksManager commonHooksManager;

  @PostConstruct
  public void start() throws IOException {
    RegistryProperties.ForProd forProd = registryProperties.getServer().getForProd();

    ServerBuilder<?> b =
        ServerBuilder.forPort(forProd.getPort()).executor(commonThreadPools.getRpcServer());

    for (BindableService bs : services2) {
      ServerServiceDefinition ssd = bs.bindService();
      b.addService(ssd);
      commonHooksManager.triggerPublishGrpcHooks(b, ssd);
    }

    server = b.build();
    server.start();

    LOGGER.info("[registryserver] [forprod] start grpc server at 0.0.0.0:{}", //
        forProd.getPort()); //
  }

  @PreDestroy
  public void stop() {
    server.shutdown();
  }

}
