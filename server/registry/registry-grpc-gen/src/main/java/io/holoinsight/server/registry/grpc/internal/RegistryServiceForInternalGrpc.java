/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.grpc.internal;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 * <pre>
 * registry 自己集群内部通信, 一般是一些 消息&amp;事件&amp;数据查询
 * </pre>
 */
@javax.annotation.Generated(value = "by gRPC proto compiler (version 1.23.0)",
    comments = "Source: registry-for-internal.proto")
public final class RegistryServiceForInternalGrpc {

  private RegistryServiceForInternalGrpc() {}

  public static final String SERVICE_NAME =
      "io.holoinsight.server.registry.grpc.internal.RegistryServiceForInternal";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.common.grpc.GenericDataBatch, com.google.protobuf.Empty> getSendEventsMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "send_events",
      requestType = io.holoinsight.server.common.grpc.GenericDataBatch.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.common.grpc.GenericDataBatch, com.google.protobuf.Empty> getSendEventsMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.common.grpc.GenericDataBatch, com.google.protobuf.Empty> getSendEventsMethod;
    if ((getSendEventsMethod = RegistryServiceForInternalGrpc.getSendEventsMethod) == null) {
      synchronized (RegistryServiceForInternalGrpc.class) {
        if ((getSendEventsMethod = RegistryServiceForInternalGrpc.getSendEventsMethod) == null) {
          RegistryServiceForInternalGrpc.getSendEventsMethod = getSendEventsMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.common.grpc.GenericDataBatch, com.google.protobuf.Empty>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "send_events"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.common.grpc.GenericDataBatch.getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(com.google.protobuf.Empty.getDefaultInstance()))
                  .setSchemaDescriptor(
                      new RegistryServiceForInternalMethodDescriptorSupplier("send_events"))
                  .build();
        }
      }
    }
    return getSendEventsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.internal.BiStreamProxyRequest, io.holoinsight.server.registry.grpc.internal.BiStreamProxyResponse> getBistreamProxyMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "bistream_proxy",
      requestType = io.holoinsight.server.registry.grpc.internal.BiStreamProxyRequest.class,
      responseType = io.holoinsight.server.registry.grpc.internal.BiStreamProxyResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.internal.BiStreamProxyRequest, io.holoinsight.server.registry.grpc.internal.BiStreamProxyResponse> getBistreamProxyMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.internal.BiStreamProxyRequest, io.holoinsight.server.registry.grpc.internal.BiStreamProxyResponse> getBistreamProxyMethod;
    if ((getBistreamProxyMethod = RegistryServiceForInternalGrpc.getBistreamProxyMethod) == null) {
      synchronized (RegistryServiceForInternalGrpc.class) {
        if ((getBistreamProxyMethod =
            RegistryServiceForInternalGrpc.getBistreamProxyMethod) == null) {
          RegistryServiceForInternalGrpc.getBistreamProxyMethod = getBistreamProxyMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.registry.grpc.internal.BiStreamProxyRequest, io.holoinsight.server.registry.grpc.internal.BiStreamProxyResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "bistream_proxy"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.registry.grpc.internal.BiStreamProxyRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.registry.grpc.internal.BiStreamProxyResponse
                          .getDefaultInstance()))
                  .setSchemaDescriptor(
                      new RegistryServiceForInternalMethodDescriptorSupplier("bistream_proxy"))
                  .build();
        }
      }
    }
    return getBistreamProxyMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static RegistryServiceForInternalStub newStub(io.grpc.Channel channel) {
    return new RegistryServiceForInternalStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static RegistryServiceForInternalBlockingStub newBlockingStub(io.grpc.Channel channel) {
    return new RegistryServiceForInternalBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static RegistryServiceForInternalFutureStub newFutureStub(io.grpc.Channel channel) {
    return new RegistryServiceForInternalFutureStub(channel);
  }

  /**
   * <pre>
   * registry 自己集群内部通信, 一般是一些 消息&amp;事件&amp;数据查询
   * </pre>
   */
  public static abstract class RegistryServiceForInternalImplBase
      implements io.grpc.BindableService {

    /**
     * <pre>
     * 批量发送消息
     * </pre>
     */
    public void sendEvents(io.holoinsight.server.common.grpc.GenericDataBatch request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(getSendEventsMethod(), responseObserver);
    }

    /**
     */
    public void bistreamProxy(
        io.holoinsight.server.registry.grpc.internal.BiStreamProxyRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.registry.grpc.internal.BiStreamProxyResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getBistreamProxyMethod(), responseObserver);
    }

    @java.lang.Override
    public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(getSendEventsMethod(), asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.common.grpc.GenericDataBatch, com.google.protobuf.Empty>(
                  this, METHODID_SEND_EVENTS)))
          .addMethod(getBistreamProxyMethod(), asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.registry.grpc.internal.BiStreamProxyRequest, io.holoinsight.server.registry.grpc.internal.BiStreamProxyResponse>(
                  this, METHODID_BISTREAM_PROXY)))
          .build();
    }
  }

  /**
   * <pre>
   * registry 自己集群内部通信, 一般是一些 消息&amp;事件&amp;数据查询
   * </pre>
   */
  public static final class RegistryServiceForInternalStub
      extends io.grpc.stub.AbstractStub<RegistryServiceForInternalStub> {
    private RegistryServiceForInternalStub(io.grpc.Channel channel) {
      super(channel);
    }

    private RegistryServiceForInternalStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RegistryServiceForInternalStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new RegistryServiceForInternalStub(channel, callOptions);
    }

    /**
     * <pre>
     * 批量发送消息
     * </pre>
     */
    public void sendEvents(io.holoinsight.server.common.grpc.GenericDataBatch request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(getChannel().newCall(getSendEventsMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void bistreamProxy(
        io.holoinsight.server.registry.grpc.internal.BiStreamProxyRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.registry.grpc.internal.BiStreamProxyResponse> responseObserver) {
      asyncUnaryCall(getChannel().newCall(getBistreamProxyMethod(), getCallOptions()), request,
          responseObserver);
    }
  }

  /**
   * <pre>
   * registry 自己集群内部通信, 一般是一些 消息&amp;事件&amp;数据查询
   * </pre>
   */
  public static final class RegistryServiceForInternalBlockingStub
      extends io.grpc.stub.AbstractStub<RegistryServiceForInternalBlockingStub> {
    private RegistryServiceForInternalBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private RegistryServiceForInternalBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RegistryServiceForInternalBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new RegistryServiceForInternalBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * 批量发送消息
     * </pre>
     */
    public com.google.protobuf.Empty sendEvents(
        io.holoinsight.server.common.grpc.GenericDataBatch request) {
      return blockingUnaryCall(getChannel(), getSendEventsMethod(), getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.registry.grpc.internal.BiStreamProxyResponse bistreamProxy(
        io.holoinsight.server.registry.grpc.internal.BiStreamProxyRequest request) {
      return blockingUnaryCall(getChannel(), getBistreamProxyMethod(), getCallOptions(), request);
    }
  }

  /**
   * <pre>
   * registry 自己集群内部通信, 一般是一些 消息&amp;事件&amp;数据查询
   * </pre>
   */
  public static final class RegistryServiceForInternalFutureStub
      extends io.grpc.stub.AbstractStub<RegistryServiceForInternalFutureStub> {
    private RegistryServiceForInternalFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private RegistryServiceForInternalFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RegistryServiceForInternalFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new RegistryServiceForInternalFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * 批量发送消息
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> sendEvents(
        io.holoinsight.server.common.grpc.GenericDataBatch request) {
      return futureUnaryCall(getChannel().newCall(getSendEventsMethod(), getCallOptions()),
          request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.registry.grpc.internal.BiStreamProxyResponse> bistreamProxy(
        io.holoinsight.server.registry.grpc.internal.BiStreamProxyRequest request) {
      return futureUnaryCall(getChannel().newCall(getBistreamProxyMethod(), getCallOptions()),
          request);
    }
  }

  private static final int METHODID_SEND_EVENTS = 0;
  private static final int METHODID_BISTREAM_PROXY = 1;

  private static final class MethodHandlers<Req, Resp>
      implements io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final RegistryServiceForInternalImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(RegistryServiceForInternalImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SEND_EVENTS:
          serviceImpl.sendEvents((io.holoinsight.server.common.grpc.GenericDataBatch) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_BISTREAM_PROXY:
          serviceImpl.bistreamProxy(
              (io.holoinsight.server.registry.grpc.internal.BiStreamProxyRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.registry.grpc.internal.BiStreamProxyResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class RegistryServiceForInternalBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier,
      io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    RegistryServiceForInternalBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return io.holoinsight.server.registry.grpc.internal.RegistryForInternalProtos.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("RegistryServiceForInternal");
    }
  }

  private static final class RegistryServiceForInternalFileDescriptorSupplier
      extends RegistryServiceForInternalBaseDescriptorSupplier {
    RegistryServiceForInternalFileDescriptorSupplier() {}
  }

  private static final class RegistryServiceForInternalMethodDescriptorSupplier
      extends RegistryServiceForInternalBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    RegistryServiceForInternalMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (RegistryServiceForInternalGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new RegistryServiceForInternalFileDescriptorSupplier())
              .addMethod(getSendEventsMethod()).addMethod(getBistreamProxyMethod()).build();
        }
      }
    }
    return result;
  }
}
