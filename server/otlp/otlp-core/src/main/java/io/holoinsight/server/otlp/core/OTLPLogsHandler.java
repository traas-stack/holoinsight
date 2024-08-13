/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.otlp.core;

import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceRequest;
import io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceResponse;

/**
 * <p>
 * created at 2023/11/2
 *
 * @author xzchaoo
 */
public interface OTLPLogsHandler {
  void export(ExportLogsServiceRequest request, StreamObserver<ExportLogsServiceResponse> o);

  ExportLogsServiceResponse export(ExportLogsServiceRequest request);
}
