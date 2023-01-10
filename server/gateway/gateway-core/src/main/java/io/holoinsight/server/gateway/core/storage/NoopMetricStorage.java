/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.storage;

import java.util.List;

import io.holoinsight.server.common.auth.AuthInfo;
import io.holoinsight.server.gateway.grpc.Point;
import io.holoinsight.server.gateway.grpc.WriteMetricsRequestV1;
import io.holoinsight.server.gateway.grpc.WriteMetricsRequestV4;
import prometheus.Prometheus;
import reactor.core.publisher.Mono;

/**
 * <p>
 * created at 2022/12/2
 *
 * @author sw1136562366
 */
public class NoopMetricStorage implements MetricStorage {
  /** {@inheritDoc} */
  @Override
  public Mono<Void> write(AuthInfo authInfo, WriteMetricsRequestV1 request) {
    return Mono.empty();
  }

  /** {@inheritDoc} */
  @Override
  public Mono<Void> write(AuthInfo authInfo, WriteMetricsRequestV4 request) {
    return Mono.empty();
  }

  /** {@inheritDoc} */
  @Override
  public Mono<Void> write(AuthInfo authInfo, Prometheus.WriteRequest writeRequest) {
    return Mono.empty();
  }

  /** {@inheritDoc} */
  @Override
  public Mono<Void> write(AuthInfo authInfo, List<Point> points) {
    return Mono.empty();
  }

  /** {@inheritDoc} */
  @Override
  public void bar1() {
    //
  }
}
