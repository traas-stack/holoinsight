/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common;

import io.grpc.Context;
import io.grpc.Metadata;
import io.grpc.ServerStreamTracer;
import io.grpc.Status;

/**
 * <ul>
 * <li>rpcHandleStartTime-rpcHeaderRecvTime: including body recv time + threadpool queueing
 * time</li>
 * <li>rpcHandleEndTime should be set by user manually, it not set, it defaults to streamClosed
 * time</li>
 * <li>rpcHandleEndTime-rpcHandleStartTime: rpc biz time</li>
 * </ul>
 * <p>
 * created at 2023/5/17
 *
 * @author xzchaoo
 */
public class SlowGrpcTracer extends ServerStreamTracer {
  static final io.grpc.Context.Key<SlowGrpcTracer> KEY =
      Context.key(SlowGrpcTracer.class.getName());

  long rpcHeaderRecvTime;
  long rpcHandleStartTime;
  long rpcHandleEndTime;

  public static void markRpcHandleEnd() {
    SlowGrpcTracer t = KEY.get();
    if (t != null) {
      t.rpcHandleEndTime = System.currentTimeMillis();
    }
  }

  @Override
  public Context filterContext(Context context) {
    rpcHeaderRecvTime = System.currentTimeMillis();
    return context.withValue(KEY, this);
  }

  @Override
  public void streamClosed(Status status) {
    if (rpcHandleEndTime == 0) {
      rpcHandleEndTime = System.currentTimeMillis();
    }
    super.streamClosed(status);
  }

  public long recvCost() {
    return rpcHandleStartTime - rpcHeaderRecvTime;
  }

  public long handleCost() {
    return rpcHandleEndTime - rpcHandleStartTime;
  }

  public static class Factory extends ServerStreamTracer.Factory {
    @Override
    public ServerStreamTracer newServerStreamTracer(String fullMethodName, Metadata headers) {
      return new SlowGrpcTracer();
    }
  }
}
