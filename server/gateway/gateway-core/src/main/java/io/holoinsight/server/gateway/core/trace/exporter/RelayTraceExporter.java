/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.trace.exporter;

import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;

/**
 * <p>
 * RelayTraceExporter class.
 * </p>
 *
 * @author sw1136562366
 */
public class RelayTraceExporter implements TraceExporter {

  @GrpcClient("traceExporterService")
  TraceServiceGrpc.TraceServiceStub serviceStub;

  /** {@inheritDoc} */
  @Override
  public void export(ExportTraceServiceRequest request,
      StreamObserver<ExportTraceServiceResponse> responseObserver) {
    serviceStub.export(request, responseObserver);
  }

}
