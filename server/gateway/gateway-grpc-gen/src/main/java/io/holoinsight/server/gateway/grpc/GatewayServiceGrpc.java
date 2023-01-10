/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(value = "by gRPC proto compiler (version 1.42.2)",
    comments = "Source: gateway-for-agent.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class GatewayServiceGrpc {

  private GatewayServiceGrpc() {}

  public static final String SERVICE_NAME = "io.holoinsight.server.gateway.grpc.GatewayService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.google.protobuf.Empty, com.google.protobuf.Empty> getPingMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "ping",
      requestType = com.google.protobuf.Empty.class, responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.protobuf.Empty, com.google.protobuf.Empty> getPingMethod() {
    io.grpc.MethodDescriptor<com.google.protobuf.Empty, com.google.protobuf.Empty> getPingMethod;
    if ((getPingMethod = GatewayServiceGrpc.getPingMethod) == null) {
      synchronized (GatewayServiceGrpc.class) {
        if ((getPingMethod = GatewayServiceGrpc.getPingMethod) == null) {
          GatewayServiceGrpc.getPingMethod = getPingMethod =
              io.grpc.MethodDescriptor.<com.google.protobuf.Empty, com.google.protobuf.Empty>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ping"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(com.google.protobuf.Empty.getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(com.google.protobuf.Empty.getDefaultInstance()))
                  .setSchemaDescriptor(new GatewayServiceMethodDescriptorSupplier("ping")).build();
        }
      }
    }
    return getPingMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.gateway.grpc.GetControlConfigsRequest, io.holoinsight.server.gateway.grpc.GetControlConfigsResponse> getGetControlConfigsMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "get_control_configs",
      requestType = io.holoinsight.server.gateway.grpc.GetControlConfigsRequest.class,
      responseType = io.holoinsight.server.gateway.grpc.GetControlConfigsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.gateway.grpc.GetControlConfigsRequest, io.holoinsight.server.gateway.grpc.GetControlConfigsResponse> getGetControlConfigsMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.gateway.grpc.GetControlConfigsRequest, io.holoinsight.server.gateway.grpc.GetControlConfigsResponse> getGetControlConfigsMethod;
    if ((getGetControlConfigsMethod = GatewayServiceGrpc.getGetControlConfigsMethod) == null) {
      synchronized (GatewayServiceGrpc.class) {
        if ((getGetControlConfigsMethod = GatewayServiceGrpc.getGetControlConfigsMethod) == null) {
          GatewayServiceGrpc.getGetControlConfigsMethod = getGetControlConfigsMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.gateway.grpc.GetControlConfigsRequest, io.holoinsight.server.gateway.grpc.GetControlConfigsResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "get_control_configs"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.gateway.grpc.GetControlConfigsRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.gateway.grpc.GetControlConfigsResponse
                          .getDefaultInstance()))
                  .setSchemaDescriptor(
                      new GatewayServiceMethodDescriptorSupplier("get_control_configs"))
                  .build();
        }
      }
    }
    return getGetControlConfigsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.gateway.grpc.WriteMetricsRequestV1, io.holoinsight.server.gateway.grpc.WriteMetricsResponse> getWriteMetricsV1Method;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "write_metrics_v1",
      requestType = io.holoinsight.server.gateway.grpc.WriteMetricsRequestV1.class,
      responseType = io.holoinsight.server.gateway.grpc.WriteMetricsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.gateway.grpc.WriteMetricsRequestV1, io.holoinsight.server.gateway.grpc.WriteMetricsResponse> getWriteMetricsV1Method() {
    io.grpc.MethodDescriptor<io.holoinsight.server.gateway.grpc.WriteMetricsRequestV1, io.holoinsight.server.gateway.grpc.WriteMetricsResponse> getWriteMetricsV1Method;
    if ((getWriteMetricsV1Method = GatewayServiceGrpc.getWriteMetricsV1Method) == null) {
      synchronized (GatewayServiceGrpc.class) {
        if ((getWriteMetricsV1Method = GatewayServiceGrpc.getWriteMetricsV1Method) == null) {
          GatewayServiceGrpc.getWriteMetricsV1Method = getWriteMetricsV1Method =
              io.grpc.MethodDescriptor.<io.holoinsight.server.gateway.grpc.WriteMetricsRequestV1, io.holoinsight.server.gateway.grpc.WriteMetricsResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "write_metrics_v1"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.gateway.grpc.WriteMetricsRequestV1
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.gateway.grpc.WriteMetricsResponse.getDefaultInstance()))
                  .setSchemaDescriptor(
                      new GatewayServiceMethodDescriptorSupplier("write_metrics_v1"))
                  .build();
        }
      }
    }
    return getWriteMetricsV1Method;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.gateway.grpc.WriteMetricsRequestV2, io.holoinsight.server.gateway.grpc.WriteMetricsResponse> getWriteMetricsV2Method;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "write_metrics_v2",
      requestType = io.holoinsight.server.gateway.grpc.WriteMetricsRequestV2.class,
      responseType = io.holoinsight.server.gateway.grpc.WriteMetricsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.gateway.grpc.WriteMetricsRequestV2, io.holoinsight.server.gateway.grpc.WriteMetricsResponse> getWriteMetricsV2Method() {
    io.grpc.MethodDescriptor<io.holoinsight.server.gateway.grpc.WriteMetricsRequestV2, io.holoinsight.server.gateway.grpc.WriteMetricsResponse> getWriteMetricsV2Method;
    if ((getWriteMetricsV2Method = GatewayServiceGrpc.getWriteMetricsV2Method) == null) {
      synchronized (GatewayServiceGrpc.class) {
        if ((getWriteMetricsV2Method = GatewayServiceGrpc.getWriteMetricsV2Method) == null) {
          GatewayServiceGrpc.getWriteMetricsV2Method = getWriteMetricsV2Method =
              io.grpc.MethodDescriptor.<io.holoinsight.server.gateway.grpc.WriteMetricsRequestV2, io.holoinsight.server.gateway.grpc.WriteMetricsResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "write_metrics_v2"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.gateway.grpc.WriteMetricsRequestV2
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.gateway.grpc.WriteMetricsResponse.getDefaultInstance()))
                  .setSchemaDescriptor(
                      new GatewayServiceMethodDescriptorSupplier("write_metrics_v2"))
                  .build();
        }
      }
    }
    return getWriteMetricsV2Method;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.gateway.grpc.WriteMetricsRequestV3, io.holoinsight.server.gateway.grpc.WriteMetricsResponse> getWriteMetricsV3Method;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "write_metrics_v3",
      requestType = io.holoinsight.server.gateway.grpc.WriteMetricsRequestV3.class,
      responseType = io.holoinsight.server.gateway.grpc.WriteMetricsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.gateway.grpc.WriteMetricsRequestV3, io.holoinsight.server.gateway.grpc.WriteMetricsResponse> getWriteMetricsV3Method() {
    io.grpc.MethodDescriptor<io.holoinsight.server.gateway.grpc.WriteMetricsRequestV3, io.holoinsight.server.gateway.grpc.WriteMetricsResponse> getWriteMetricsV3Method;
    if ((getWriteMetricsV3Method = GatewayServiceGrpc.getWriteMetricsV3Method) == null) {
      synchronized (GatewayServiceGrpc.class) {
        if ((getWriteMetricsV3Method = GatewayServiceGrpc.getWriteMetricsV3Method) == null) {
          GatewayServiceGrpc.getWriteMetricsV3Method = getWriteMetricsV3Method =
              io.grpc.MethodDescriptor.<io.holoinsight.server.gateway.grpc.WriteMetricsRequestV3, io.holoinsight.server.gateway.grpc.WriteMetricsResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "write_metrics_v3"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.gateway.grpc.WriteMetricsRequestV3
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.gateway.grpc.WriteMetricsResponse.getDefaultInstance()))
                  .setSchemaDescriptor(
                      new GatewayServiceMethodDescriptorSupplier("write_metrics_v3"))
                  .build();
        }
      }
    }
    return getWriteMetricsV3Method;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.gateway.grpc.WriteMetricsRequestV4, io.holoinsight.server.gateway.grpc.WriteMetricsResponse> getWriteMetricsV4Method;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "write_metrics_v4",
      requestType = io.holoinsight.server.gateway.grpc.WriteMetricsRequestV4.class,
      responseType = io.holoinsight.server.gateway.grpc.WriteMetricsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.gateway.grpc.WriteMetricsRequestV4, io.holoinsight.server.gateway.grpc.WriteMetricsResponse> getWriteMetricsV4Method() {
    io.grpc.MethodDescriptor<io.holoinsight.server.gateway.grpc.WriteMetricsRequestV4, io.holoinsight.server.gateway.grpc.WriteMetricsResponse> getWriteMetricsV4Method;
    if ((getWriteMetricsV4Method = GatewayServiceGrpc.getWriteMetricsV4Method) == null) {
      synchronized (GatewayServiceGrpc.class) {
        if ((getWriteMetricsV4Method = GatewayServiceGrpc.getWriteMetricsV4Method) == null) {
          GatewayServiceGrpc.getWriteMetricsV4Method = getWriteMetricsV4Method =
              io.grpc.MethodDescriptor.<io.holoinsight.server.gateway.grpc.WriteMetricsRequestV4, io.holoinsight.server.gateway.grpc.WriteMetricsResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "write_metrics_v4"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.gateway.grpc.WriteMetricsRequestV4
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.gateway.grpc.WriteMetricsResponse.getDefaultInstance()))
                  .setSchemaDescriptor(
                      new GatewayServiceMethodDescriptorSupplier("write_metrics_v4"))
                  .build();
        }
      }
    }
    return getWriteMetricsV4Method;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static GatewayServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GatewayServiceStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<GatewayServiceStub>() {
          @java.lang.Override
          public GatewayServiceStub newStub(io.grpc.Channel channel,
              io.grpc.CallOptions callOptions) {
            return new GatewayServiceStub(channel, callOptions);
          }
        };
    return GatewayServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static GatewayServiceBlockingStub newBlockingStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GatewayServiceBlockingStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<GatewayServiceBlockingStub>() {
          @java.lang.Override
          public GatewayServiceBlockingStub newStub(io.grpc.Channel channel,
              io.grpc.CallOptions callOptions) {
            return new GatewayServiceBlockingStub(channel, callOptions);
          }
        };
    return GatewayServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static GatewayServiceFutureStub newFutureStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GatewayServiceFutureStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<GatewayServiceFutureStub>() {
          @java.lang.Override
          public GatewayServiceFutureStub newStub(io.grpc.Channel channel,
              io.grpc.CallOptions callOptions) {
            return new GatewayServiceFutureStub(channel, callOptions);
          }
        };
    return GatewayServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class GatewayServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void ping(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getPingMethod(), responseObserver);
    }

    /**
     * <pre>
     * 查询控制参数
     * </pre>
     */
    public void getControlConfigs(
        io.holoinsight.server.gateway.grpc.GetControlConfigsRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.gateway.grpc.GetControlConfigsResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetControlConfigsMethod(),
          responseObserver);
    }

    /**
     * <pre>
     * deprecated
     * </pre>
     */
    public void writeMetricsV1(io.holoinsight.server.gateway.grpc.WriteMetricsRequestV1 request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.gateway.grpc.WriteMetricsResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getWriteMetricsV1Method(),
          responseObserver);
    }

    /**
     * <pre>
     * deprecated
     * </pre>
     */
    public void writeMetricsV2(io.holoinsight.server.gateway.grpc.WriteMetricsRequestV2 request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.gateway.grpc.WriteMetricsResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getWriteMetricsV2Method(),
          responseObserver);
    }

    /**
     * <pre>
     * deprecated
     * </pre>
     */
    public void writeMetricsV3(io.holoinsight.server.gateway.grpc.WriteMetricsRequestV3 request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.gateway.grpc.WriteMetricsResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getWriteMetricsV3Method(),
          responseObserver);
    }

    /**
     */
    public void writeMetricsV4(io.holoinsight.server.gateway.grpc.WriteMetricsRequestV4 request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.gateway.grpc.WriteMetricsResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getWriteMetricsV4Method(),
          responseObserver);
    }

    @java.lang.Override
    public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(getPingMethod(),
              io.grpc.stub.ServerCalls.asyncUnaryCall(
                  new MethodHandlers<com.google.protobuf.Empty, com.google.protobuf.Empty>(this,
                      METHODID_PING)))
          .addMethod(getGetControlConfigsMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.gateway.grpc.GetControlConfigsRequest, io.holoinsight.server.gateway.grpc.GetControlConfigsResponse>(
                  this, METHODID_GET_CONTROL_CONFIGS)))
          .addMethod(getWriteMetricsV1Method(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.gateway.grpc.WriteMetricsRequestV1, io.holoinsight.server.gateway.grpc.WriteMetricsResponse>(
                  this, METHODID_WRITE_METRICS_V1)))
          .addMethod(getWriteMetricsV2Method(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.gateway.grpc.WriteMetricsRequestV2, io.holoinsight.server.gateway.grpc.WriteMetricsResponse>(
                  this, METHODID_WRITE_METRICS_V2)))
          .addMethod(getWriteMetricsV3Method(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.gateway.grpc.WriteMetricsRequestV3, io.holoinsight.server.gateway.grpc.WriteMetricsResponse>(
                  this, METHODID_WRITE_METRICS_V3)))
          .addMethod(getWriteMetricsV4Method(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.gateway.grpc.WriteMetricsRequestV4, io.holoinsight.server.gateway.grpc.WriteMetricsResponse>(
                  this, METHODID_WRITE_METRICS_V4)))
          .build();
    }
  }

  /**
   */
  public static final class GatewayServiceStub
      extends io.grpc.stub.AbstractAsyncStub<GatewayServiceStub> {
    private GatewayServiceStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GatewayServiceStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GatewayServiceStub(channel, callOptions);
    }

    /**
     */
    public void ping(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getPingMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * 查询控制参数
     * </pre>
     */
    public void getControlConfigs(
        io.holoinsight.server.gateway.grpc.GetControlConfigsRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.gateway.grpc.GetControlConfigsResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetControlConfigsMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     * <pre>
     * deprecated
     * </pre>
     */
    public void writeMetricsV1(io.holoinsight.server.gateway.grpc.WriteMetricsRequestV1 request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.gateway.grpc.WriteMetricsResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getWriteMetricsV1Method(), getCallOptions()), request,
          responseObserver);
    }

    /**
     * <pre>
     * deprecated
     * </pre>
     */
    public void writeMetricsV2(io.holoinsight.server.gateway.grpc.WriteMetricsRequestV2 request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.gateway.grpc.WriteMetricsResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getWriteMetricsV2Method(), getCallOptions()), request,
          responseObserver);
    }

    /**
     * <pre>
     * deprecated
     * </pre>
     */
    public void writeMetricsV3(io.holoinsight.server.gateway.grpc.WriteMetricsRequestV3 request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.gateway.grpc.WriteMetricsResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getWriteMetricsV3Method(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void writeMetricsV4(io.holoinsight.server.gateway.grpc.WriteMetricsRequestV4 request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.gateway.grpc.WriteMetricsResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getWriteMetricsV4Method(), getCallOptions()), request,
          responseObserver);
    }
  }

  /**
   */
  public static final class GatewayServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<GatewayServiceBlockingStub> {
    private GatewayServiceBlockingStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GatewayServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GatewayServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.google.protobuf.Empty ping(com.google.protobuf.Empty request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getPingMethod(),
          getCallOptions(), request);
    }

    /**
     * <pre>
     * 查询控制参数
     * </pre>
     */
    public io.holoinsight.server.gateway.grpc.GetControlConfigsResponse getControlConfigs(
        io.holoinsight.server.gateway.grpc.GetControlConfigsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getGetControlConfigsMethod(),
          getCallOptions(), request);
    }

    /**
     * <pre>
     * deprecated
     * </pre>
     */
    public io.holoinsight.server.gateway.grpc.WriteMetricsResponse writeMetricsV1(
        io.holoinsight.server.gateway.grpc.WriteMetricsRequestV1 request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getWriteMetricsV1Method(),
          getCallOptions(), request);
    }

    /**
     * <pre>
     * deprecated
     * </pre>
     */
    public io.holoinsight.server.gateway.grpc.WriteMetricsResponse writeMetricsV2(
        io.holoinsight.server.gateway.grpc.WriteMetricsRequestV2 request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getWriteMetricsV2Method(),
          getCallOptions(), request);
    }

    /**
     * <pre>
     * deprecated
     * </pre>
     */
    public io.holoinsight.server.gateway.grpc.WriteMetricsResponse writeMetricsV3(
        io.holoinsight.server.gateway.grpc.WriteMetricsRequestV3 request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getWriteMetricsV3Method(),
          getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.gateway.grpc.WriteMetricsResponse writeMetricsV4(
        io.holoinsight.server.gateway.grpc.WriteMetricsRequestV4 request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getWriteMetricsV4Method(),
          getCallOptions(), request);
    }
  }

  /**
   */
  public static final class GatewayServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<GatewayServiceFutureStub> {
    private GatewayServiceFutureStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GatewayServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GatewayServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> ping(
        com.google.protobuf.Empty request) {
      return io.grpc.stub.ClientCalls
          .futureUnaryCall(getChannel().newCall(getPingMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * 查询控制参数
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.gateway.grpc.GetControlConfigsResponse> getControlConfigs(
        io.holoinsight.server.gateway.grpc.GetControlConfigsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetControlConfigsMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * deprecated
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.gateway.grpc.WriteMetricsResponse> writeMetricsV1(
        io.holoinsight.server.gateway.grpc.WriteMetricsRequestV1 request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getWriteMetricsV1Method(), getCallOptions()), request);
    }

    /**
     * <pre>
     * deprecated
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.gateway.grpc.WriteMetricsResponse> writeMetricsV2(
        io.holoinsight.server.gateway.grpc.WriteMetricsRequestV2 request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getWriteMetricsV2Method(), getCallOptions()), request);
    }

    /**
     * <pre>
     * deprecated
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.gateway.grpc.WriteMetricsResponse> writeMetricsV3(
        io.holoinsight.server.gateway.grpc.WriteMetricsRequestV3 request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getWriteMetricsV3Method(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.gateway.grpc.WriteMetricsResponse> writeMetricsV4(
        io.holoinsight.server.gateway.grpc.WriteMetricsRequestV4 request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getWriteMetricsV4Method(), getCallOptions()), request);
    }
  }

  private static final int METHODID_PING = 0;
  private static final int METHODID_GET_CONTROL_CONFIGS = 1;
  private static final int METHODID_WRITE_METRICS_V1 = 2;
  private static final int METHODID_WRITE_METRICS_V2 = 3;
  private static final int METHODID_WRITE_METRICS_V3 = 4;
  private static final int METHODID_WRITE_METRICS_V4 = 5;

  private static final class MethodHandlers<Req, Resp>
      implements io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final GatewayServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(GatewayServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_PING:
          serviceImpl.ping((com.google.protobuf.Empty) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_GET_CONTROL_CONFIGS:
          serviceImpl.getControlConfigs(
              (io.holoinsight.server.gateway.grpc.GetControlConfigsRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.gateway.grpc.GetControlConfigsResponse>) responseObserver);
          break;
        case METHODID_WRITE_METRICS_V1:
          serviceImpl.writeMetricsV1(
              (io.holoinsight.server.gateway.grpc.WriteMetricsRequestV1) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.gateway.grpc.WriteMetricsResponse>) responseObserver);
          break;
        case METHODID_WRITE_METRICS_V2:
          serviceImpl.writeMetricsV2(
              (io.holoinsight.server.gateway.grpc.WriteMetricsRequestV2) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.gateway.grpc.WriteMetricsResponse>) responseObserver);
          break;
        case METHODID_WRITE_METRICS_V3:
          serviceImpl.writeMetricsV3(
              (io.holoinsight.server.gateway.grpc.WriteMetricsRequestV3) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.gateway.grpc.WriteMetricsResponse>) responseObserver);
          break;
        case METHODID_WRITE_METRICS_V4:
          serviceImpl.writeMetricsV4(
              (io.holoinsight.server.gateway.grpc.WriteMetricsRequestV4) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.gateway.grpc.WriteMetricsResponse>) responseObserver);
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

  private static abstract class GatewayServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier,
      io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    GatewayServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return io.holoinsight.server.gateway.grpc.GatewayProtos.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("GatewayService");
    }
  }

  private static final class GatewayServiceFileDescriptorSupplier
      extends GatewayServiceBaseDescriptorSupplier {
    GatewayServiceFileDescriptorSupplier() {}
  }

  private static final class GatewayServiceMethodDescriptorSupplier
      extends GatewayServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    GatewayServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (GatewayServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new GatewayServiceFileDescriptorSupplier())
              .addMethod(getPingMethod()).addMethod(getGetControlConfigsMethod())
              .addMethod(getWriteMetricsV1Method()).addMethod(getWriteMetricsV2Method())
              .addMethod(getWriteMetricsV3Method()).addMethod(getWriteMetricsV4Method()).build();
        }
      }
    }
    return result;
  }
}
