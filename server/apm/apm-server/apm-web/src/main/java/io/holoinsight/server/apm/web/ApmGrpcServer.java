/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.web;

import java.io.IOException;

import javax.annotation.PostConstruct;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.holoinsight.server.common.threadpool.CommonThreadPools;
import io.holoinsight.server.apm.receiver.trace.TraceOtelServiceImpl;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * created at 2022/11/30
 *
 * @author jiwliu
 */
@Slf4j
public class ApmGrpcServer {
  @Autowired
  private TraceOtelServiceImpl traceOtelService;
  @Autowired
  private CommonThreadPools commonThreadPools;

  @PostConstruct
  public void start() throws IOException {
    int port = 12801;
    // TODO 线程池
    Server server = ServerBuilder.forPort(port) //
        .executor(commonThreadPools.getRpcServer()).addService(traceOtelService) //
        .maxInboundMessageSize(100 * 1024 * 1024) //
        .build(); //
    server.start();
    log.info("[storage] start grpc server at port {}", port);
  }
}
