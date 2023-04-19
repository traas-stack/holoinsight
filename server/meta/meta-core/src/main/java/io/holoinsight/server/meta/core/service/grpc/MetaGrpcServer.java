/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.core.service.grpc;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;
import io.holoinsight.server.common.hook.CommonHooksManager;
import io.holoinsight.server.common.threadpool.CommonThreadPools;
import io.holoinsight.server.meta.common.util.ConstPool;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * TODO meta 是内部服务, 合并部署时不用暴露
 * <p>
 * created at 2022/12/6
 *
 * @author jsy1001de
 */
@Slf4j
@Component
public class MetaGrpcServer {
  @Autowired
  private DataServiceGrpcImpl dataServiceGrpcImpl;
  @Autowired
  private CommonThreadPools commonThreadPools;
  private Server dataServer;
  @Autowired
  private CommonHooksManager commonHooksManager;

  @PostConstruct
  public void start() throws IOException {
    ServerBuilder<?> b = ServerBuilder.forPort(ConstPool.GRPC_PORT_DATA) //
        .executor(commonThreadPools.getRpcServer()) //
        .maxInboundMessageSize(64 * 1024 * 1024);

    ServerServiceDefinition ssd = dataServiceGrpcImpl.bindService();
    b.addService(ssd);
    commonHooksManager.triggerPublishGrpcHooks(b, ssd);

    dataServer = b.build(); //
    dataServer.start();

    log.info("[meta] start data grpc server at port {}", ConstPool.GRPC_PORT_DATA);
  }

  @PreDestroy
  public void stop() throws InterruptedException {
    dataServer.shutdown();
    dataServer.awaitTermination(10, TimeUnit.SECONDS);
  }
}
