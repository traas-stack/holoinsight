/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.trace.exporter;

import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;

/**
 * <p>
 * created at 2022/11/30
 *
 * @author sw1136562366
 */
public interface TraceExporter {
  /**
   * <p>
   * export.
   * </p>
   */
  void export(ExportTraceServiceRequest request,
      StreamObserver<ExportTraceServiceResponse> responseObserver);
}
