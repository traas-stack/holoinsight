/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common;

import java.util.Objects;

import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

/**
 * Slow grpc server interceptor
 * <p>
 * created at 2023/5/17
 *
 * @author xzchaoo
 */
public class SlowGrpcServerInterceptor implements ServerInterceptor {
  private final SlowGrpcHandler handler;

  public SlowGrpcServerInterceptor(SlowGrpcHandler handler) {
    this.handler = Objects.requireNonNull(handler);
  }

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call,
      Metadata headers, ServerCallHandler<ReqT, RespT> next) {
    if (call.getMethodDescriptor().getType() != MethodDescriptor.MethodType.UNARY) {
      return next.startCall(call, headers);
    }
    return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(
        next.startCall(call, headers)) {

      private Object req;

      @Override
      public void onMessage(ReqT message) {
        this.req = message;
        super.onMessage(message);
      }

      @Override
      public void onHalfClose() {
        SlowGrpcTracer.KEY.get().rpcHandleStartTime = System.currentTimeMillis();
        super.onHalfClose();
      }

      @Override
      public void onComplete() {
        SlowGrpcTracer t = SlowGrpcTracer.KEY.get();
        if (t != null) {
          handler.onComplete(call, req, t);
        }
        super.onComplete();
      }
    };
  }
}
