/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.grpc;


import io.holoinsight.server.common.auth.AuthInfo;
import io.holoinsight.server.extension.model.Table;
import io.holoinsight.server.gateway.grpc.Point;
import io.holoinsight.server.gateway.grpc.WriteMetricsRequestV1;
import io.holoinsight.server.gateway.grpc.WriteMetricsRequestV4;

/**
 * <p>
 * created at 2023/9/18
 *
 * @author xzchaoo
 */
public interface GatewayHook {
  /**
   * writeMetricsV1 hook
   *
   * @param authInfo
   * @param request
   */
  void writeMetricsV1(AuthInfo authInfo, WriteMetricsRequestV1 request);

  /**
   * writeMetricsV4 hook
   *
   * @param authInfo
   * @param request
   */
  void writeMetricsV4(AuthInfo authInfo, WriteMetricsRequestV4 request);

  void writeDetail(AuthInfo authInfo, Table table);

  boolean supportsDetail(String name);

  default Point processV1(AuthInfo authInfo, Point point) {
    return point;
  }

}
