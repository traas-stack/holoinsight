/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.dispatcher;

import org.springframework.beans.factory.annotation.Autowired;

import io.holoinsight.server.common.auth.AuthInfo;
import io.holoinsight.server.extension.model.Table;
import io.holoinsight.server.gateway.core.grpc.GatewayHook;
import io.holoinsight.server.gateway.grpc.WriteMetricsRequestV1;
import io.holoinsight.server.gateway.grpc.WriteMetricsRequestV4;

/**
 * <p>
 * created at 2023/9/18
 *
 * @author xzchaoo
 */
public class AggGatewayHook implements GatewayHook {
  @Autowired
  private AggDispatcher aggDispatcher;

  @Override
  public void writeMetricsV1(AuthInfo authInfo, WriteMetricsRequestV1 request) {
    aggDispatcher.dispatch(authInfo, request);
  }

  @Override
  public void writeMetricsV4(AuthInfo authInfo, WriteMetricsRequestV4 request) {
    aggDispatcher.dispatch(authInfo, request);
  }

  @Override
  public void writeDetail(AuthInfo authInfo, Table table) {
    aggDispatcher.dispatchDetailData(authInfo, table);
  }

  @Override
  public boolean supportsDetail(String name) {
    return aggDispatcher.supportsDetail(name);
  }
}
