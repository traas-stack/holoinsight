/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common;

import org.apache.commons.lang3.StringUtils;

import com.xzchaoo.commons.stat.StatAccumulator;
import com.xzchaoo.commons.stat.StringsKey;

import io.grpc.Context;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.ServerStreamTracer;
import io.grpc.Status;
import lombok.Getter;
import lombok.Setter;

/**
 * Grpc traffic tracer
 * <p>
 * created at 2022/3/18
 *
 * @author sw1136562366
 */
public class TrafficTracer extends ServerStreamTracer {
  /** Constant <code>KEY</code> */
  public static final Context.Key<TrafficTracer> KEY = Context.key("TrafficTracer");
  private static final StatAccumulator<StringsKey> H_GRPC_TRAFFIC =
      MetricsUtils.SM.create("H_GRPC_TRAFFIC");
  @Getter
  private long inboundWireSize;
  @Getter
  private long inboundUncompressedSize;
  private MethodDescriptor<?, ?> methodDescriptor;
  @Setter
  private String tenant = "unknown";
  private long outboundWireSize;
  private long outboundUncompressedSize;

  /** {@inheritDoc} */
  @Override
  public Context filterContext(Context context) {
    return context.withValue(KEY, this);
  }

  @Override
  public void serverCallStarted(ServerCallInfo<?, ?> callInfo) {
    super.serverCallStarted(callInfo);
    if (callInfo.getMethodDescriptor().getType() == MethodDescriptor.MethodType.UNARY) {
      methodDescriptor = callInfo.getMethodDescriptor();
    }
  }

  @Override
  public void streamClosed(Status status) {
    super.streamClosed(status);
    if (methodDescriptor != null) {
      H_GRPC_TRAFFIC.add(StringsKey.of( //
          tenant, //
          methodDescriptor.getServiceName(), //
          StringUtils.substringAfterLast(methodDescriptor.getServiceName(), '.'), //
          methodDescriptor.getBareMethodName(), //
          status.getCode().name() //
      ), //
          new long[] {1, //
              inboundWireSize, //
              inboundUncompressedSize, //
              outboundWireSize, //
              outboundUncompressedSize,}); //
    }
  }

  @Override
  public void outboundWireSize(long bytes) {
    outboundWireSize += bytes;
  }

  @Override
  public void outboundUncompressedSize(long bytes) {
    outboundUncompressedSize += bytes;
  }

  @Override
  public void inboundWireSize(long bytes) {
    this.inboundWireSize += bytes;
  }

  @Override
  public void inboundUncompressedSize(long bytes) {
    this.inboundUncompressedSize += bytes;
  }

  /**
   * Factor for {@link TrafficTracer}
   */
  public static class Factory extends ServerStreamTracer.Factory {
    @Override
    public ServerStreamTracer newServerStreamTracer(String fullMethodName, Metadata headers) {
      return new TrafficTracer();
    }
  }
}
