/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common;

import io.grpc.ServerCall;

/**
 * Slow grpc chandler
 * <p>
 * created at 2023/5/17
 *
 * @author xzchaoo
 */
@FunctionalInterface
public interface SlowGrpcHandler {
  void onComplete(ServerCall<?, ?> call, Object req, SlowGrpcTracer t);
}
