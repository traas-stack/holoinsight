/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.otlp.core;

import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;

/**
 * <p>
 * created at 2023/11/2
 *
 * @author xzchaoo
 */
public interface OTLPTraceHandler {
  void export(ExportTraceServiceRequest request, StreamObserver<ExportTraceServiceResponse> o);

  ExportTraceServiceResponse export(ExportTraceServiceRequest request);
}
