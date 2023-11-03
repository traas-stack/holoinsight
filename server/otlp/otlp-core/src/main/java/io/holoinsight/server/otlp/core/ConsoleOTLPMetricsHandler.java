/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.otlp.core;

import com.google.protobuf.util.JsonFormat;

import io.grpc.stub.StreamObserver;
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest;
import io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * For test
 * <p>
 * created at 2023/11/3
 *
 * @author xzchaoo
 */
@Slf4j
public class ConsoleOTLPMetricsHandler implements OTLPMetricsHandler {
  @Override
  public void export(ExportMetricsServiceRequest request,
      StreamObserver<ExportMetricsServiceResponse> o) {
    o.onNext(export(request));
  }

  @SneakyThrows
  @Override
  public ExportMetricsServiceResponse export(ExportMetricsServiceRequest request) {
    String str = JsonFormat.printer().omittingInsignificantWhitespace().print(request);
    log.info("[otlp] [metrics] export {}", str);
    return ExportMetricsServiceResponse.getDefaultInstance();
  }
}
