/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.grpc.trace;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * Define a trace segment report service.
 * All language agents or any trace collecting component, could use this service to send span collection to the SkyWalking OAP backend.
 * </pre>
 */
@javax.annotation.Generated(value = "by gRPC proto compiler (version 1.42.2)",
    comments = "Source: trace/Tracing.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class TraceReportServiceGrpc {

  private TraceReportServiceGrpc() {}

  public static final String SERVICE_NAME =
      "io.holoinsight.server.apm.grpc.trace.TraceReportService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.apm.grpc.trace.SegmentObject, io.holoinsight.server.apm.grpc.common.CommonResponse> getCollectMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "collect",
      requestType = io.holoinsight.server.apm.grpc.trace.SegmentObject.class,
      responseType = io.holoinsight.server.apm.grpc.common.CommonResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.apm.grpc.trace.SegmentObject, io.holoinsight.server.apm.grpc.common.CommonResponse> getCollectMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.apm.grpc.trace.SegmentObject, io.holoinsight.server.apm.grpc.common.CommonResponse> getCollectMethod;
    if ((getCollectMethod = TraceReportServiceGrpc.getCollectMethod) == null) {
      synchronized (TraceReportServiceGrpc.class) {
        if ((getCollectMethod = TraceReportServiceGrpc.getCollectMethod) == null) {
          TraceReportServiceGrpc.getCollectMethod = getCollectMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.apm.grpc.trace.SegmentObject, io.holoinsight.server.apm.grpc.common.CommonResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "collect"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.apm.grpc.trace.SegmentObject.getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.apm.grpc.common.CommonResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new TraceReportServiceMethodDescriptorSupplier("collect"))
                  .build();
        }
      }
    }
    return getCollectMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.apm.grpc.trace.SegmentCollection, io.holoinsight.server.apm.grpc.common.CommonResponse> getCollectInSyncMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "collectInSync",
      requestType = io.holoinsight.server.apm.grpc.trace.SegmentCollection.class,
      responseType = io.holoinsight.server.apm.grpc.common.CommonResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.apm.grpc.trace.SegmentCollection, io.holoinsight.server.apm.grpc.common.CommonResponse> getCollectInSyncMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.apm.grpc.trace.SegmentCollection, io.holoinsight.server.apm.grpc.common.CommonResponse> getCollectInSyncMethod;
    if ((getCollectInSyncMethod = TraceReportServiceGrpc.getCollectInSyncMethod) == null) {
      synchronized (TraceReportServiceGrpc.class) {
        if ((getCollectInSyncMethod = TraceReportServiceGrpc.getCollectInSyncMethod) == null) {
          TraceReportServiceGrpc.getCollectInSyncMethod = getCollectInSyncMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.apm.grpc.trace.SegmentCollection, io.holoinsight.server.apm.grpc.common.CommonResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "collectInSync"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.apm.grpc.trace.SegmentCollection.getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.apm.grpc.common.CommonResponse.getDefaultInstance()))
                  .setSchemaDescriptor(
                      new TraceReportServiceMethodDescriptorSupplier("collectInSync"))
                  .build();
        }
      }
    }
    return getCollectInSyncMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static TraceReportServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TraceReportServiceStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<TraceReportServiceStub>() {
          @java.lang.Override
          public TraceReportServiceStub newStub(io.grpc.Channel channel,
              io.grpc.CallOptions callOptions) {
            return new TraceReportServiceStub(channel, callOptions);
          }
        };
    return TraceReportServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static TraceReportServiceBlockingStub newBlockingStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TraceReportServiceBlockingStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<TraceReportServiceBlockingStub>() {
          @java.lang.Override
          public TraceReportServiceBlockingStub newStub(io.grpc.Channel channel,
              io.grpc.CallOptions callOptions) {
            return new TraceReportServiceBlockingStub(channel, callOptions);
          }
        };
    return TraceReportServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static TraceReportServiceFutureStub newFutureStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TraceReportServiceFutureStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<TraceReportServiceFutureStub>() {
          @java.lang.Override
          public TraceReportServiceFutureStub newStub(io.grpc.Channel channel,
              io.grpc.CallOptions callOptions) {
            return new TraceReportServiceFutureStub(channel, callOptions);
          }
        };
    return TraceReportServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * Define a trace segment report service.
   * All language agents or any trace collecting component, could use this service to send span collection to the SkyWalking OAP backend.
   * </pre>
   */
  public static abstract class TraceReportServiceImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * Recommended trace segment report channel.
     * gRPC streaming provides better performance.
     * All language agents should choose this.
     * </pre>
     */
    public io.grpc.stub.StreamObserver<io.holoinsight.server.apm.grpc.trace.SegmentObject> collect(
        io.grpc.stub.StreamObserver<io.holoinsight.server.apm.grpc.common.CommonResponse> responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getCollectMethod(),
          responseObserver);
    }

    /**
     * <pre>
     * An alternative for trace report by using gRPC unary
     * This is provided for some 3rd-party integration, if and only if they prefer the unary mode somehow.
     * The performance of SkyWalking OAP server would be very similar with streaming report,
     * the performance of the network and client side are affected
     * </pre>
     */
    public void collectInSync(io.holoinsight.server.apm.grpc.trace.SegmentCollection request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.apm.grpc.common.CommonResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCollectInSyncMethod(),
          responseObserver);
    }

    @java.lang.Override
    public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(getCollectMethod(), io.grpc.stub.ServerCalls.asyncClientStreamingCall(
              new MethodHandlers<io.holoinsight.server.apm.grpc.trace.SegmentObject, io.holoinsight.server.apm.grpc.common.CommonResponse>(
                  this, METHODID_COLLECT)))
          .addMethod(getCollectInSyncMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.apm.grpc.trace.SegmentCollection, io.holoinsight.server.apm.grpc.common.CommonResponse>(
                  this, METHODID_COLLECT_IN_SYNC)))
          .build();
    }
  }

  /**
   * <pre>
   * Define a trace segment report service.
   * All language agents or any trace collecting component, could use this service to send span collection to the SkyWalking OAP backend.
   * </pre>
   */
  public static final class TraceReportServiceStub
      extends io.grpc.stub.AbstractAsyncStub<TraceReportServiceStub> {
    private TraceReportServiceStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TraceReportServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TraceReportServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Recommended trace segment report channel.
     * gRPC streaming provides better performance.
     * All language agents should choose this.
     * </pre>
     */
    public io.grpc.stub.StreamObserver<io.holoinsight.server.apm.grpc.trace.SegmentObject> collect(
        io.grpc.stub.StreamObserver<io.holoinsight.server.apm.grpc.common.CommonResponse> responseObserver) {
      return io.grpc.stub.ClientCalls.asyncClientStreamingCall(
          getChannel().newCall(getCollectMethod(), getCallOptions()), responseObserver);
    }

    /**
     * <pre>
     * An alternative for trace report by using gRPC unary
     * This is provided for some 3rd-party integration, if and only if they prefer the unary mode somehow.
     * The performance of SkyWalking OAP server would be very similar with streaming report,
     * the performance of the network and client side are affected
     * </pre>
     */
    public void collectInSync(io.holoinsight.server.apm.grpc.trace.SegmentCollection request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.apm.grpc.common.CommonResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCollectInSyncMethod(), getCallOptions()), request,
          responseObserver);
    }
  }

  /**
   * <pre>
   * Define a trace segment report service.
   * All language agents or any trace collecting component, could use this service to send span collection to the SkyWalking OAP backend.
   * </pre>
   */
  public static final class TraceReportServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<TraceReportServiceBlockingStub> {
    private TraceReportServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TraceReportServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TraceReportServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * An alternative for trace report by using gRPC unary
     * This is provided for some 3rd-party integration, if and only if they prefer the unary mode somehow.
     * The performance of SkyWalking OAP server would be very similar with streaming report,
     * the performance of the network and client side are affected
     * </pre>
     */
    public io.holoinsight.server.apm.grpc.common.CommonResponse collectInSync(
        io.holoinsight.server.apm.grpc.trace.SegmentCollection request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getCollectInSyncMethod(),
          getCallOptions(), request);
    }
  }

  /**
   * <pre>
   * Define a trace segment report service.
   * All language agents or any trace collecting component, could use this service to send span collection to the SkyWalking OAP backend.
   * </pre>
   */
  public static final class TraceReportServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<TraceReportServiceFutureStub> {
    private TraceReportServiceFutureStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TraceReportServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TraceReportServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * An alternative for trace report by using gRPC unary
     * This is provided for some 3rd-party integration, if and only if they prefer the unary mode somehow.
     * The performance of SkyWalking OAP server would be very similar with streaming report,
     * the performance of the network and client side are affected
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.apm.grpc.common.CommonResponse> collectInSync(
        io.holoinsight.server.apm.grpc.trace.SegmentCollection request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCollectInSyncMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_COLLECT_IN_SYNC = 0;
  private static final int METHODID_COLLECT = 1;

  private static final class MethodHandlers<Req, Resp>
      implements io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final TraceReportServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(TraceReportServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_COLLECT_IN_SYNC:
          serviceImpl.collectInSync(
              (io.holoinsight.server.apm.grpc.trace.SegmentCollection) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.apm.grpc.common.CommonResponse>) responseObserver);
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
        case METHODID_COLLECT:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.collect(
              (io.grpc.stub.StreamObserver<io.holoinsight.server.apm.grpc.common.CommonResponse>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class TraceReportServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier,
      io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    TraceReportServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return io.holoinsight.server.apm.grpc.trace.Tracing.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("TraceReportService");
    }
  }

  private static final class TraceReportServiceFileDescriptorSupplier
      extends TraceReportServiceBaseDescriptorSupplier {
    TraceReportServiceFileDescriptorSupplier() {}
  }

  private static final class TraceReportServiceMethodDescriptorSupplier
      extends TraceReportServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    TraceReportServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (TraceReportServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new TraceReportServiceFileDescriptorSupplier())
              .addMethod(getCollectMethod()).addMethod(getCollectInSyncMethod()).build();
        }
      }
    }
    return result;
  }
}
