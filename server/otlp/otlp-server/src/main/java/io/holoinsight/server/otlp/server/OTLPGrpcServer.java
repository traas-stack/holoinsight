/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.otlp.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import io.holoinsight.server.common.threadpool.CommonThreadPools;
import io.holoinsight.server.otlp.core.OTLPLogsHandler;
import io.holoinsight.server.otlp.core.OTLPMetricsHandler;
import io.holoinsight.server.otlp.core.OTLPTraceHandler;
import io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceRequest;
import io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceResponse;
import io.opentelemetry.proto.collector.logs.v1.LogsServiceGrpc;
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest;
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceResponse;
import io.opentelemetry.proto.collector.metrics.v1.MetricsServiceGrpc;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * created at 2023/11/2
 *
 * @author xzchaoo
 */
@Slf4j
public class OTLPGrpcServer {
  @Autowired(required = false)
  private OTLPMetricsHandler otlpMetricsHandler;
  @Autowired(required = false)
  private OTLPTraceHandler otlpTraceHandler;
  @Autowired(required = false)
  private OTLPLogsHandler otlpLogsHandler;
  private List<Server> servers = new ArrayList<>();
  @Autowired
  private CommonThreadPools commonThreadPools;

  @PostConstruct
  public void start() throws IOException {
    servers.add(start(11800));
    // official grpc port
    // servers.add(start(4317));
  }

  private Server start(int port) throws IOException {
    ServerBuilder<?> sb = ServerBuilder.forPort(port) //
        .executor(commonThreadPools.getRpcServer()) //
        .maxInboundMessageSize(100 * 1024 * 1024); //

    if (otlpMetricsHandler != null) {
      sb.addService(new MetricsServiceGrpc.MetricsServiceImplBase() {
        @Override
        public void export(ExportMetricsServiceRequest r,
            StreamObserver<ExportMetricsServiceResponse> o) {
          otlpMetricsHandler.export(r, o);
        }
      });
    }

    if (otlpTraceHandler != null) {
      sb.addService(new TraceServiceGrpc.TraceServiceImplBase() {
        @Override
        public void export(ExportTraceServiceRequest request,
            StreamObserver<ExportTraceServiceResponse> o) {
          otlpTraceHandler.export(request, o);
        }
      });
    }

    if (otlpLogsHandler != null) {
      sb.addService(new LogsServiceGrpc.LogsServiceImplBase() {
        @Override
        public void export(ExportLogsServiceRequest request,
            StreamObserver<ExportLogsServiceResponse> responseObserver) {
          otlpLogsHandler.export(request, responseObserver);
        }
      });
    }

    Server server = sb.build();
    server.start();
    log.info("[otlp] start grpc server at port {}, metrics=[{}] traces=[{}] logs=[{}]", //
        port, //
        otlpMetricsHandler != null ? "enabled" : "disabled", //
        otlpTraceHandler != null ? "enabled" : "disabled", //
        otlpLogsHandler != null ? "enabled" : "disabled");
    return server;
  }

  @PreDestroy
  public void stop() {
    for (Server server : servers) {
      server.shutdown();
    }
  }
}
