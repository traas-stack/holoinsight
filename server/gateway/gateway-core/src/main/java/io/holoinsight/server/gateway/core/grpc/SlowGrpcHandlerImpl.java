/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.grpc;

import java.util.Objects;

import io.grpc.ServerCall;
import io.holoinsight.server.common.SlowGrpcHandler;
import io.holoinsight.server.common.SlowGrpcTracer;
import io.holoinsight.server.common.grpc.SlowGrpcProperties;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * created at 2023/5/17
 *
 * @author xzchaoo
 */
@Slf4j
public class SlowGrpcHandlerImpl implements SlowGrpcHandler {
  private final SlowGrpcProperties properties;

  public SlowGrpcHandlerImpl(SlowGrpcProperties slowGrpcProperties) {
    this.properties = Objects.requireNonNull(slowGrpcProperties);
  }

  @Override
  public void onComplete(ServerCall<?, ?> call, Object req, SlowGrpcTracer t) {
    long recvCost = t.recvCost();
    long handleCost = t.handleCost();

    if (recvCost > properties.getRecvThreshold() || handleCost > properties.getHandleThreshold()) {
      log.info("slow grpc method=[{}] recv=[{}] handle=[{}]", //
          call.getMethodDescriptor().getFullMethodName(), recvCost, handleCost);
    }
  }
}
