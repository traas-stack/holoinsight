/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.trace.exporter;

import io.grpc.stub.StreamObserver;
import io.holoinsight.server.apm.receiver.trace.SpanHandler;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * When the gateway and apm are deployed together, the beans are used directly
 * <p>
 * created at 2022/11/30
 *
 * @author xzchaoo
 */
public class LocalTraceExporter implements TraceExporter {
  @Autowired
  private SpanHandler spanHandler;

  @Override
  public void export(ExportTraceServiceRequest request,
      StreamObserver<ExportTraceServiceResponse> o) {
    spanHandler.handleResourceSpans(request.getResourceSpansList());
    o.onNext(ExportTraceServiceResponse.getDefaultInstance());
    o.onCompleted();
  }
}
