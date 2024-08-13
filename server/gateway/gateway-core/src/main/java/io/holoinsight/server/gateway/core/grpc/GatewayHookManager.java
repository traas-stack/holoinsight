/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.grpc;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.holoinsight.server.common.auth.AuthInfo;
import io.holoinsight.server.gateway.grpc.Point;
import io.holoinsight.server.gateway.grpc.WriteMetricsRequestV1;
import io.holoinsight.server.gateway.grpc.WriteMetricsRequestV4;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * created at 2023/9/18
 *
 * @author xzchaoo
 */
@Slf4j
@Component
public class GatewayHookManager {
  @Autowired(required = false)
  private List<GatewayHook> gatewayHooks = new ArrayList<>();

  public void writeMetricsV1(AuthInfo authInfo, WriteMetricsRequestV1 request) {
    for (GatewayHook hook : gatewayHooks) {
      try {
        hook.writeMetricsV1(authInfo, request);
      } catch (Exception e) {
        log.error("writeMetricsV1 hook error", e);
      }
    }
  }

  public void writeMetricsV4(AuthInfo authInfo, WriteMetricsRequestV4 request) {
    for (GatewayHook hook : gatewayHooks) {
      try {
        hook.writeMetricsV4(authInfo, request);
      } catch (Exception e) {
        log.error("riteMetricsV4 hook error", e);
      }
    }
  }

  public Point processV1(AuthInfo authInfo, Point p) {
    for (GatewayHook hook : gatewayHooks) {
      try {
        Point p2 = hook.processV1(authInfo, p);
        if (p2 == null) {
          return null;
        }
        p = p2;
      } catch (Exception e) {
        log.error("processV1 hook error", e);
      }
    }
    return p;
  }
}
