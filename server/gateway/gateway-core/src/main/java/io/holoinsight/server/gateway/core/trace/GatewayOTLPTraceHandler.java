/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.trace;

import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;

import io.grpc.stub.StreamObserver;
import io.holoinsight.server.gateway.core.trace.exporter.TraceExporter;
import io.holoinsight.server.otlp.core.OTLPTraceHandler;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse;

/**
 * <p>
 * created at 2023/11/3
 *
 * @author xzchaoo
 */
public class GatewayOTLPTraceHandler implements OTLPTraceHandler {
  @Autowired
  private TraceExporter exporter;

  @Override
  public void export(ExportTraceServiceRequest request,
      StreamObserver<ExportTraceServiceResponse> o) {
    exporter.export(request, o);
  }

  @Override
  public ExportTraceServiceResponse export(ExportTraceServiceRequest request) {
    CountDownLatch cdl = new CountDownLatch(1);
    Throwable[] error = new Throwable[1];
    ExportTraceServiceResponse[] resp = new ExportTraceServiceResponse[1];
    // Adapt to TraceExporter
    exporter.export(request, new StreamObserver<ExportTraceServiceResponse>() {
      @Override
      public void onNext(ExportTraceServiceResponse value) {
        resp[0] = value;
        cdl.countDown();
      }

      @Override
      public void onError(Throwable t) {
        error[0] = t;
        cdl.countDown();
      }

      @Override
      public void onCompleted() {
        cdl.countDown();
      }
    });

    if (error[0] != null) {
      throw new RuntimeException(error[0]);
    }
    return resp[0];
  }
}
