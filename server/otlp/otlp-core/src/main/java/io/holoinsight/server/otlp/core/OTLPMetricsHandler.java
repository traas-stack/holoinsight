/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.otlp.core;

import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest;
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceResponse;

/**
 * <p>
 * created at 2023/11/2
 *
 * @author xzchaoo
 */
public interface OTLPMetricsHandler {
  void export(ExportMetricsServiceRequest request, StreamObserver<ExportMetricsServiceResponse> o);

  ExportMetricsServiceResponse export(ExportMetricsServiceRequest request);
}
