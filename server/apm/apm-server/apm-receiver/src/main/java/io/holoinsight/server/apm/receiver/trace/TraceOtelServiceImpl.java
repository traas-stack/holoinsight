/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.receiver.trace;

import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;

import org.springframework.beans.factory.annotation.Autowired;

public class TraceOtelServiceImpl extends TraceServiceGrpc.TraceServiceImplBase {

  @Autowired
  private SpanHandler spanHandler;

  @Override
  public void export(ExportTraceServiceRequest request,
      StreamObserver<ExportTraceServiceResponse> responseObserver) {
    spanHandler.handleResourceSpans(request.getResourceSpansList());
    responseObserver.onNext(ExportTraceServiceResponse.newBuilder().build());
    responseObserver.onCompleted();
  }
}
