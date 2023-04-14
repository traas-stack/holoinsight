package io.holoinsight.server.demo.server.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.42.2)",
    comments = "Source: demo-server.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class DemoServiceGrpc {

  private DemoServiceGrpc() {}

  public static final String SERVICE_NAME = "io.holoinsight.server.demo.server.DemoService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.demo.server.grpc.FooRequest,
      io.holoinsight.server.demo.server.grpc.FooResponse> getFooMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "foo",
      requestType = io.holoinsight.server.demo.server.grpc.FooRequest.class,
      responseType = io.holoinsight.server.demo.server.grpc.FooResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.demo.server.grpc.FooRequest,
      io.holoinsight.server.demo.server.grpc.FooResponse> getFooMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.demo.server.grpc.FooRequest, io.holoinsight.server.demo.server.grpc.FooResponse> getFooMethod;
    if ((getFooMethod = DemoServiceGrpc.getFooMethod) == null) {
      synchronized (DemoServiceGrpc.class) {
        if ((getFooMethod = DemoServiceGrpc.getFooMethod) == null) {
          DemoServiceGrpc.getFooMethod = getFooMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.demo.server.grpc.FooRequest, io.holoinsight.server.demo.server.grpc.FooResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "foo"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.holoinsight.server.demo.server.grpc.FooRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.holoinsight.server.demo.server.grpc.FooResponse.getDefaultInstance()))
              .setSchemaDescriptor(new DemoServiceMethodDescriptorSupplier("foo"))
              .build();
        }
      }
    }
    return getFooMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.demo.server.grpc.BarRequest,
      io.holoinsight.server.demo.server.grpc.BarResponse> getBarMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "bar",
      requestType = io.holoinsight.server.demo.server.grpc.BarRequest.class,
      responseType = io.holoinsight.server.demo.server.grpc.BarResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.demo.server.grpc.BarRequest,
      io.holoinsight.server.demo.server.grpc.BarResponse> getBarMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.demo.server.grpc.BarRequest, io.holoinsight.server.demo.server.grpc.BarResponse> getBarMethod;
    if ((getBarMethod = DemoServiceGrpc.getBarMethod) == null) {
      synchronized (DemoServiceGrpc.class) {
        if ((getBarMethod = DemoServiceGrpc.getBarMethod) == null) {
          DemoServiceGrpc.getBarMethod = getBarMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.demo.server.grpc.BarRequest, io.holoinsight.server.demo.server.grpc.BarResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "bar"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.holoinsight.server.demo.server.grpc.BarRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.holoinsight.server.demo.server.grpc.BarResponse.getDefaultInstance()))
              .setSchemaDescriptor(new DemoServiceMethodDescriptorSupplier("bar"))
              .build();
        }
      }
    }
    return getBarMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static DemoServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DemoServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<DemoServiceStub>() {
        @java.lang.Override
        public DemoServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new DemoServiceStub(channel, callOptions);
        }
      };
    return DemoServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static DemoServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DemoServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<DemoServiceBlockingStub>() {
        @java.lang.Override
        public DemoServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new DemoServiceBlockingStub(channel, callOptions);
        }
      };
    return DemoServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static DemoServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DemoServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<DemoServiceFutureStub>() {
        @java.lang.Override
        public DemoServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new DemoServiceFutureStub(channel, callOptions);
        }
      };
    return DemoServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class DemoServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void foo(io.holoinsight.server.demo.server.grpc.FooRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.demo.server.grpc.FooResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getFooMethod(), responseObserver);
    }

    /**
     */
    public void bar(io.holoinsight.server.demo.server.grpc.BarRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.demo.server.grpc.BarResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getBarMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getFooMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                io.holoinsight.server.demo.server.grpc.FooRequest,
                io.holoinsight.server.demo.server.grpc.FooResponse>(
                  this, METHODID_FOO)))
          .addMethod(
            getBarMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                io.holoinsight.server.demo.server.grpc.BarRequest,
                io.holoinsight.server.demo.server.grpc.BarResponse>(
                  this, METHODID_BAR)))
          .build();
    }
  }

  /**
   */
  public static final class DemoServiceStub extends io.grpc.stub.AbstractAsyncStub<DemoServiceStub> {
    private DemoServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DemoServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new DemoServiceStub(channel, callOptions);
    }

    /**
     */
    public void foo(io.holoinsight.server.demo.server.grpc.FooRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.demo.server.grpc.FooResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getFooMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void bar(io.holoinsight.server.demo.server.grpc.BarRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.demo.server.grpc.BarResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getBarMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class DemoServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<DemoServiceBlockingStub> {
    private DemoServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DemoServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new DemoServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public io.holoinsight.server.demo.server.grpc.FooResponse foo(io.holoinsight.server.demo.server.grpc.FooRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getFooMethod(), getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.demo.server.grpc.BarResponse bar(io.holoinsight.server.demo.server.grpc.BarRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getBarMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class DemoServiceFutureStub extends io.grpc.stub.AbstractFutureStub<DemoServiceFutureStub> {
    private DemoServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DemoServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new DemoServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.demo.server.grpc.FooResponse> foo(
        io.holoinsight.server.demo.server.grpc.FooRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getFooMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.demo.server.grpc.BarResponse> bar(
        io.holoinsight.server.demo.server.grpc.BarRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getBarMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_FOO = 0;
  private static final int METHODID_BAR = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final DemoServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(DemoServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_FOO:
          serviceImpl.foo((io.holoinsight.server.demo.server.grpc.FooRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.demo.server.grpc.FooResponse>) responseObserver);
          break;
        case METHODID_BAR:
          serviceImpl.bar((io.holoinsight.server.demo.server.grpc.BarRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.demo.server.grpc.BarResponse>) responseObserver);
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

  private static abstract class DemoServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    DemoServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return io.holoinsight.server.demo.server.grpc.DemoServerProtos.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("DemoService");
    }
  }

  private static final class DemoServiceFileDescriptorSupplier
      extends DemoServiceBaseDescriptorSupplier {
    DemoServiceFileDescriptorSupplier() {}
  }

  private static final class DemoServiceMethodDescriptorSupplier
      extends DemoServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    DemoServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (DemoServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new DemoServiceFileDescriptorSupplier())
              .addMethod(getFooMethod())
              .addMethod(getBarMethod())
              .build();
        }
      }
    }
    return result;
  }
}
