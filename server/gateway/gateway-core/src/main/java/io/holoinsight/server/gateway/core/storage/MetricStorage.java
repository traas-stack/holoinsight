/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.storage;

import io.holoinsight.server.gateway.grpc.Point;
import io.holoinsight.server.gateway.grpc.WriteMetricsRequestV1;
import io.holoinsight.server.gateway.grpc.WriteMetricsRequestV4;

import io.holoinsight.server.common.auth.AuthInfo;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * <p>
 * created at 2022/2/25
 *
 * @author sw1136562366
 */
public interface MetricStorage {
  // TODO 为了性能 直接WriteMetricsRequestV1写到storage上, 避免2次转换
  /**
   * <p>
   * write.
   * </p>
   */
  Mono<Void> write(AuthInfo authInfo, WriteMetricsRequestV1 request);

  /**
   * <p>
   * write.
   * </p>
   */
  Mono<Void> write(AuthInfo authInfo, WriteMetricsRequestV4 request);

  /**
   * <p>
   * write.
   * </p>
   */
  Mono<Void> write(AuthInfo authInfo, prometheus.Prometheus.WriteRequest writeRequest);

  /**
   * <p>
   * write.
   * </p>
   */
  Mono<Void> write(AuthInfo authInfo, List<Point> points);

  /**
   * <p>
   * bar1.
   * </p>
   */
  void bar1();
}
