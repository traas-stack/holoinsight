/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.opentelemetry.proto.collector.trace.v1;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * Service that can be used to push spans between one Application instrumented with
 * OpenTelemetry and a collector, or between a collector and a central collector (in this
 * case spans are sent/received to/from multiple Applications).
 * </pre>
 */
@javax.annotation.Generated(value = "by gRPC proto compiler (version 1.42.2)",
    comments = "Source: opentelemetry/proto/collector/trace/v1/trace_service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class TraceServiceGrpc {

  private TraceServiceGrpc() {}

  public static final String SERVICE_NAME = "opentelemetry.proto.collector.trace.v1.TraceService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest, io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse> getExportMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "Export",
      requestType = io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest.class,
      responseType = io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest, io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse> getExportMethod() {
    io.grpc.MethodDescriptor<io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest, io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse> getExportMethod;
    if ((getExportMethod = TraceServiceGrpc.getExportMethod) == null) {
      synchronized (TraceServiceGrpc.class) {
        if ((getExportMethod = TraceServiceGrpc.getExportMethod) == null) {
          TraceServiceGrpc.getExportMethod = getExportMethod =
              io.grpc.MethodDescriptor.<io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest, io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Export"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse
                          .getDefaultInstance()))
                  .setSchemaDescriptor(new TraceServiceMethodDescriptorSupplier("Export")).build();
        }
      }
    }
    return getExportMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static TraceServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TraceServiceStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<TraceServiceStub>() {
          @java.lang.Override
          public TraceServiceStub newStub(io.grpc.Channel channel,
              io.grpc.CallOptions callOptions) {
            return new TraceServiceStub(channel, callOptions);
          }
        };
    return TraceServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static TraceServiceBlockingStub newBlockingStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TraceServiceBlockingStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<TraceServiceBlockingStub>() {
          @java.lang.Override
          public TraceServiceBlockingStub newStub(io.grpc.Channel channel,
              io.grpc.CallOptions callOptions) {
            return new TraceServiceBlockingStub(channel, callOptions);
          }
        };
    return TraceServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static TraceServiceFutureStub newFutureStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TraceServiceFutureStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<TraceServiceFutureStub>() {
          @java.lang.Override
          public TraceServiceFutureStub newStub(io.grpc.Channel channel,
              io.grpc.CallOptions callOptions) {
            return new TraceServiceFutureStub(channel, callOptions);
          }
        };
    return TraceServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * Service that can be used to push spans between one Application instrumented with
   * OpenTelemetry and a collector, or between a collector and a central collector (in this
   * case spans are sent/received to/from multiple Applications).
   * </pre>
   */
  public static abstract class TraceServiceImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * For performance reasons, it is recommended to keep this RPC
     * alive for the entire life of the application.
     * </pre>
     */
    public void export(io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest request,
        io.grpc.stub.StreamObserver<io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getExportMethod(), responseObserver);
    }

    @java.lang.Override
    public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(getExportMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest, io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse>(
                  this, METHODID_EXPORT)))
          .build();
    }
  }

  /**
   * <pre>
   * Service that can be used to push spans between one Application instrumented with
   * OpenTelemetry and a collector, or between a collector and a central collector (in this
   * case spans are sent/received to/from multiple Applications).
   * </pre>
   */
  public static final class TraceServiceStub
      extends io.grpc.stub.AbstractAsyncStub<TraceServiceStub> {
    private TraceServiceStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TraceServiceStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new TraceServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * For performance reasons, it is recommended to keep this RPC
     * alive for the entire life of the application.
     * </pre>
     */
    public void export(io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest request,
        io.grpc.stub.StreamObserver<io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getExportMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   * Service that can be used to push spans between one Application instrumented with
   * OpenTelemetry and a collector, or between a collector and a central collector (in this
   * case spans are sent/received to/from multiple Applications).
   * </pre>
   */
  public static final class TraceServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<TraceServiceBlockingStub> {
    private TraceServiceBlockingStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TraceServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TraceServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * For performance reasons, it is recommended to keep this RPC
     * alive for the entire life of the application.
     * </pre>
     */
    public io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse export(
        io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getExportMethod(),
          getCallOptions(), request);
    }
  }

  /**
   * <pre>
   * Service that can be used to push spans between one Application instrumented with
   * OpenTelemetry and a collector, or between a collector and a central collector (in this
   * case spans are sent/received to/from multiple Applications).
   * </pre>
   */
  public static final class TraceServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<TraceServiceFutureStub> {
    private TraceServiceFutureStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TraceServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TraceServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * For performance reasons, it is recommended to keep this RPC
     * alive for the entire life of the application.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse> export(
        io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest request) {
      return io.grpc.stub.ClientCalls
          .futureUnaryCall(getChannel().newCall(getExportMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_EXPORT = 0;

  private static final class MethodHandlers<Req, Resp>
      implements io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final TraceServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(TraceServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_EXPORT:
          serviceImpl.export(
              (io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest) request,
              (io.grpc.stub.StreamObserver<io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse>) responseObserver);
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

  private static abstract class TraceServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier,
      io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    TraceServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return io.opentelemetry.proto.collector.trace.v1.TraceServiceProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("TraceService");
    }
  }

  private static final class TraceServiceFileDescriptorSupplier
      extends TraceServiceBaseDescriptorSupplier {
    TraceServiceFileDescriptorSupplier() {}
  }

  private static final class TraceServiceMethodDescriptorSupplier extends
      TraceServiceBaseDescriptorSupplier implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    TraceServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (TraceServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new TraceServiceFileDescriptorSupplier())
              .addMethod(getExportMethod()).build();
        }
      }
    }
    return result;
  }
}
