/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.trace;

import java.io.IOException;

import javax.annotation.PostConstruct;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.holoinsight.server.common.threadpool.CommonThreadPools;
import io.holoinsight.server.gateway.core.trace.receiver.opentelemetry.TraceServiceImpl;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * <p>created at 2022/12/1
 *
 * @author sw1136562366
 */
@Slf4j
public class GatewayTraceGrpcServer {

    @Autowired
    @Qualifier("gateway_traceServiceImpl")
    private TraceServiceImpl traceService;

    @Autowired
    private CommonThreadPools commonThreadPools;

    /**
     * <p>start.</p>
     */
    @PostConstruct
    public void start() throws IOException {
        int port = 11800;
        // TODO 线程池
        Server server = ServerBuilder.forPort(port) //
            .executor(commonThreadPools.getRpcServer())
            .addService(traceService) //
            .maxInboundMessageSize(100 * 1024 * 1024) //
            .build(); //
        server.start();
        log.info("[gateway] start grpc server at port {}", port);
    }
}
