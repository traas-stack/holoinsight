/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.proto.alarm;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.42.2)",
    comments = "Source: notify.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class AlarmNotifyServiceGrpc {

  private AlarmNotifyServiceGrpc() {}

  public static final String SERVICE_NAME = "AlarmNotifyService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyRequest,
      io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyResponse> getSendAlarmNotifyMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SendAlarmNotify",
      requestType = io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyRequest.class,
      responseType = io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyRequest,
      io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyResponse> getSendAlarmNotifyMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyRequest, io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyResponse> getSendAlarmNotifyMethod;
    if ((getSendAlarmNotifyMethod = AlarmNotifyServiceGrpc.getSendAlarmNotifyMethod) == null) {
      synchronized (AlarmNotifyServiceGrpc.class) {
        if ((getSendAlarmNotifyMethod = AlarmNotifyServiceGrpc.getSendAlarmNotifyMethod) == null) {
          AlarmNotifyServiceGrpc.getSendAlarmNotifyMethod = getSendAlarmNotifyMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyRequest, io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SendAlarmNotify"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AlarmNotifyServiceMethodDescriptorSupplier("SendAlarmNotify"))
              .build();
        }
      }
    }
    return getSendAlarmNotifyMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyRequestV2,
      io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyResponse> getSendAlarmNotifyV2Method;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SendAlarmNotifyV2",
      requestType = io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyRequestV2.class,
      responseType = io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyRequestV2,
      io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyResponse> getSendAlarmNotifyV2Method() {
    io.grpc.MethodDescriptor<io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyRequestV2, io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyResponse> getSendAlarmNotifyV2Method;
    if ((getSendAlarmNotifyV2Method = AlarmNotifyServiceGrpc.getSendAlarmNotifyV2Method) == null) {
      synchronized (AlarmNotifyServiceGrpc.class) {
        if ((getSendAlarmNotifyV2Method = AlarmNotifyServiceGrpc.getSendAlarmNotifyV2Method) == null) {
          AlarmNotifyServiceGrpc.getSendAlarmNotifyV2Method = getSendAlarmNotifyV2Method =
              io.grpc.MethodDescriptor.<io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyRequestV2, io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SendAlarmNotifyV2"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyRequestV2.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AlarmNotifyServiceMethodDescriptorSupplier("SendAlarmNotifyV2"))
              .build();
        }
      }
    }
    return getSendAlarmNotifyV2Method;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static AlarmNotifyServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AlarmNotifyServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AlarmNotifyServiceStub>() {
        @Override
        public AlarmNotifyServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AlarmNotifyServiceStub(channel, callOptions);
        }
      };
    return AlarmNotifyServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static AlarmNotifyServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AlarmNotifyServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AlarmNotifyServiceBlockingStub>() {
        @Override
        public AlarmNotifyServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AlarmNotifyServiceBlockingStub(channel, callOptions);
        }
      };
    return AlarmNotifyServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static AlarmNotifyServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AlarmNotifyServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AlarmNotifyServiceFutureStub>() {
        @Override
        public AlarmNotifyServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AlarmNotifyServiceFutureStub(channel, callOptions);
        }
      };
    return AlarmNotifyServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class AlarmNotifyServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void sendAlarmNotify(io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSendAlarmNotifyMethod(), responseObserver);
    }

    /**
     */
    public void sendAlarmNotifyV2(io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyRequestV2 request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSendAlarmNotifyV2Method(), responseObserver);
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getSendAlarmNotifyMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyRequest,
                io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyResponse>(
                  this, METHODID_SEND_ALARM_NOTIFY)))
          .addMethod(
            getSendAlarmNotifyV2Method(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyRequestV2,
                io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyResponse>(
                  this, METHODID_SEND_ALARM_NOTIFY_V2)))
          .build();
    }
  }

  /**
   */
  public static final class AlarmNotifyServiceStub extends io.grpc.stub.AbstractAsyncStub<AlarmNotifyServiceStub> {
    private AlarmNotifyServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected AlarmNotifyServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AlarmNotifyServiceStub(channel, callOptions);
    }

    /**
     */
    public void sendAlarmNotify(io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSendAlarmNotifyMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void sendAlarmNotifyV2(io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyRequestV2 request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSendAlarmNotifyV2Method(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class AlarmNotifyServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<AlarmNotifyServiceBlockingStub> {
    private AlarmNotifyServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected AlarmNotifyServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AlarmNotifyServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyResponse sendAlarmNotify(io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSendAlarmNotifyMethod(), getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyResponse sendAlarmNotifyV2(io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyRequestV2 request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSendAlarmNotifyV2Method(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class AlarmNotifyServiceFutureStub extends io.grpc.stub.AbstractFutureStub<AlarmNotifyServiceFutureStub> {
    private AlarmNotifyServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected AlarmNotifyServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AlarmNotifyServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyResponse> sendAlarmNotify(
        io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSendAlarmNotifyMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyResponse> sendAlarmNotifyV2(
        io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyRequestV2 request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSendAlarmNotifyV2Method(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SEND_ALARM_NOTIFY = 0;
  private static final int METHODID_SEND_ALARM_NOTIFY_V2 = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AlarmNotifyServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(AlarmNotifyServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SEND_ALARM_NOTIFY:
          serviceImpl.sendAlarmNotify((io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyResponse>) responseObserver);
          break;
        case METHODID_SEND_ALARM_NOTIFY_V2:
          serviceImpl.sendAlarmNotifyV2((io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyRequestV2) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.home.proto.alarm.Notify.AlarmNotifyResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class AlarmNotifyServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    AlarmNotifyServiceBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return io.holoinsight.server.home.proto.alarm.Notify.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("AlarmNotifyService");
    }
  }

  private static final class AlarmNotifyServiceFileDescriptorSupplier
      extends AlarmNotifyServiceBaseDescriptorSupplier {
    AlarmNotifyServiceFileDescriptorSupplier() {}
  }

  private static final class AlarmNotifyServiceMethodDescriptorSupplier
      extends AlarmNotifyServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    AlarmNotifyServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (AlarmNotifyServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new AlarmNotifyServiceFileDescriptorSupplier())
              .addMethod(getSendAlarmNotifyMethod())
              .addMethod(getSendAlarmNotifyV2Method())
              .build();
        }
      }
    }
    return result;
  }
}
