/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.trace.receiver.opentelemetry;

import io.grpc.stub.StreamObserver;
import io.holoinsight.server.gateway.core.trace.exporter.TraceExporter;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * TraceServiceImpl class.
 * </p>
 *
 * @author sw1136562366
 */
public class TraceServiceImpl extends TraceServiceGrpc.TraceServiceImplBase {

  @Autowired
  private TraceExporter exporter;

  /** {@inheritDoc} */
  @Override
  public void export(ExportTraceServiceRequest request,
      StreamObserver<ExportTraceServiceResponse> responseObserver) {
    exporter.export(request, responseObserver);
  }

}
