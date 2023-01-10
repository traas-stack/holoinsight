/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.query.server;

import java.io.IOException;

import javax.annotation.PostConstruct;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;
import io.holoinsight.server.common.hook.CommonHooksManager;
import io.holoinsight.server.common.threadpool.CommonThreadPools;
import io.holoinsight.server.query.server.rpc.QueryGrpcService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * created at 2022/12/1
 *
 * @author xiangwanpeng
 */
@Slf4j
public class QueryGrpcServer {

  @Autowired
  private QueryGrpcService queryGrpcService;

  @Autowired
  private CommonThreadPools commonThreadPools;

  @Autowired
  private CommonHooksManager commonHooksManager;

  @PostConstruct
  public void start() throws IOException {
    int port = 9090;

    ServerBuilder<?> b = ServerBuilder.forPort(port) //
        .executor(commonThreadPools.getRpcServer()) //
        .maxInboundMessageSize(100 * 1024 * 1024);

    ServerServiceDefinition ssd = queryGrpcService.bindService();
    b.addService(ssd);
    commonHooksManager.triggerPublishGrpcHooks(b, ssd);

    Server server = b.build();
    server.start();
    log.info("[query] start grpc server at port {}", port);
  }
}
