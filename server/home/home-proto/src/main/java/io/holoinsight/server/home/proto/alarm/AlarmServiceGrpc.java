/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.proto.alarm;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(value = "by gRPC proto compiler (version 1.42.2)",
    comments = "Source: AlarmService.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class AlarmServiceGrpc {

  private AlarmServiceGrpc() {}

  public static final String SERVICE_NAME = "AlarmService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest, io.holoinsight.server.home.proto.base.DataBaseResponse> getQueryByIdMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "QueryById",
      requestType = io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest.class,
      responseType = io.holoinsight.server.home.proto.base.DataBaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest, io.holoinsight.server.home.proto.base.DataBaseResponse> getQueryByIdMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest, io.holoinsight.server.home.proto.base.DataBaseResponse> getQueryByIdMethod;
    if ((getQueryByIdMethod = AlarmServiceGrpc.getQueryByIdMethod) == null) {
      synchronized (AlarmServiceGrpc.class) {
        if ((getQueryByIdMethod = AlarmServiceGrpc.getQueryByIdMethod) == null) {
          AlarmServiceGrpc.getQueryByIdMethod = getQueryByIdMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest, io.holoinsight.server.home.proto.base.DataBaseResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "QueryById"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.home.proto.base.DataBaseResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new AlarmServiceMethodDescriptorSupplier("QueryById"))
                  .build();
        }
      }
    }
    return getQueryByIdMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest, io.holoinsight.server.home.proto.base.DataBaseResponse> getDeleteByIdMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "DeleteById",
      requestType = io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest.class,
      responseType = io.holoinsight.server.home.proto.base.DataBaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest, io.holoinsight.server.home.proto.base.DataBaseResponse> getDeleteByIdMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest, io.holoinsight.server.home.proto.base.DataBaseResponse> getDeleteByIdMethod;
    if ((getDeleteByIdMethod = AlarmServiceGrpc.getDeleteByIdMethod) == null) {
      synchronized (AlarmServiceGrpc.class) {
        if ((getDeleteByIdMethod = AlarmServiceGrpc.getDeleteByIdMethod) == null) {
          AlarmServiceGrpc.getDeleteByIdMethod = getDeleteByIdMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest, io.holoinsight.server.home.proto.base.DataBaseResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DeleteById"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.home.proto.base.DataBaseResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new AlarmServiceMethodDescriptorSupplier("DeleteById"))
                  .build();
        }
      }
    }
    return getDeleteByIdMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest, io.holoinsight.server.home.proto.base.DataBaseResponse> getShutdownByIdMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "ShutdownById",
      requestType = io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest.class,
      responseType = io.holoinsight.server.home.proto.base.DataBaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest, io.holoinsight.server.home.proto.base.DataBaseResponse> getShutdownByIdMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest, io.holoinsight.server.home.proto.base.DataBaseResponse> getShutdownByIdMethod;
    if ((getShutdownByIdMethod = AlarmServiceGrpc.getShutdownByIdMethod) == null) {
      synchronized (AlarmServiceGrpc.class) {
        if ((getShutdownByIdMethod = AlarmServiceGrpc.getShutdownByIdMethod) == null) {
          AlarmServiceGrpc.getShutdownByIdMethod = getShutdownByIdMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest, io.holoinsight.server.home.proto.base.DataBaseResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ShutdownById"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.home.proto.base.DataBaseResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new AlarmServiceMethodDescriptorSupplier("ShutdownById"))
                  .build();
        }
      }
    }
    return getShutdownByIdMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.home.proto.alarm.InsertOrUpdateRequest, io.holoinsight.server.home.proto.base.DataBaseResponse> getCreateMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "Create",
      requestType = io.holoinsight.server.home.proto.alarm.InsertOrUpdateRequest.class,
      responseType = io.holoinsight.server.home.proto.base.DataBaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.home.proto.alarm.InsertOrUpdateRequest, io.holoinsight.server.home.proto.base.DataBaseResponse> getCreateMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.home.proto.alarm.InsertOrUpdateRequest, io.holoinsight.server.home.proto.base.DataBaseResponse> getCreateMethod;
    if ((getCreateMethod = AlarmServiceGrpc.getCreateMethod) == null) {
      synchronized (AlarmServiceGrpc.class) {
        if ((getCreateMethod = AlarmServiceGrpc.getCreateMethod) == null) {
          AlarmServiceGrpc.getCreateMethod = getCreateMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.home.proto.alarm.InsertOrUpdateRequest, io.holoinsight.server.home.proto.base.DataBaseResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Create"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.home.proto.alarm.InsertOrUpdateRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.home.proto.base.DataBaseResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new AlarmServiceMethodDescriptorSupplier("Create")).build();
        }
      }
    }
    return getCreateMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.home.proto.alarm.InsertOrUpdateRequest, io.holoinsight.server.home.proto.base.DataBaseResponse> getUpdateMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "Update",
      requestType = io.holoinsight.server.home.proto.alarm.InsertOrUpdateRequest.class,
      responseType = io.holoinsight.server.home.proto.base.DataBaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.home.proto.alarm.InsertOrUpdateRequest, io.holoinsight.server.home.proto.base.DataBaseResponse> getUpdateMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.home.proto.alarm.InsertOrUpdateRequest, io.holoinsight.server.home.proto.base.DataBaseResponse> getUpdateMethod;
    if ((getUpdateMethod = AlarmServiceGrpc.getUpdateMethod) == null) {
      synchronized (AlarmServiceGrpc.class) {
        if ((getUpdateMethod = AlarmServiceGrpc.getUpdateMethod) == null) {
          AlarmServiceGrpc.getUpdateMethod = getUpdateMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.home.proto.alarm.InsertOrUpdateRequest, io.holoinsight.server.home.proto.base.DataBaseResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Update"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.home.proto.alarm.InsertOrUpdateRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.home.proto.base.DataBaseResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new AlarmServiceMethodDescriptorSupplier("Update")).build();
        }
      }
    }
    return getUpdateMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.home.proto.alarm.QueryAlarmHistoryPageRequest, io.holoinsight.server.home.proto.base.DataBaseResponse> getQueryAlarmHistoryByPageMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "QueryAlarmHistoryByPage",
      requestType = io.holoinsight.server.home.proto.alarm.QueryAlarmHistoryPageRequest.class,
      responseType = io.holoinsight.server.home.proto.base.DataBaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.home.proto.alarm.QueryAlarmHistoryPageRequest, io.holoinsight.server.home.proto.base.DataBaseResponse> getQueryAlarmHistoryByPageMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.home.proto.alarm.QueryAlarmHistoryPageRequest, io.holoinsight.server.home.proto.base.DataBaseResponse> getQueryAlarmHistoryByPageMethod;
    if ((getQueryAlarmHistoryByPageMethod =
        AlarmServiceGrpc.getQueryAlarmHistoryByPageMethod) == null) {
      synchronized (AlarmServiceGrpc.class) {
        if ((getQueryAlarmHistoryByPageMethod =
            AlarmServiceGrpc.getQueryAlarmHistoryByPageMethod) == null) {
          AlarmServiceGrpc.getQueryAlarmHistoryByPageMethod = getQueryAlarmHistoryByPageMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.home.proto.alarm.QueryAlarmHistoryPageRequest, io.holoinsight.server.home.proto.base.DataBaseResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(
                      generateFullMethodName(SERVICE_NAME, "QueryAlarmHistoryByPage"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.home.proto.alarm.QueryAlarmHistoryPageRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.home.proto.base.DataBaseResponse.getDefaultInstance()))
                  .setSchemaDescriptor(
                      new AlarmServiceMethodDescriptorSupplier("QueryAlarmHistoryByPage"))
                  .build();
        }
      }
    }
    return getQueryAlarmHistoryByPageMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.home.proto.alarm.QueryAlarmHistoryDetailPageRequest, io.holoinsight.server.home.proto.base.DataBaseResponse> getQueryAlarmHistoryDetailByPageMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "QueryAlarmHistoryDetailByPage",
      requestType = io.holoinsight.server.home.proto.alarm.QueryAlarmHistoryDetailPageRequest.class,
      responseType = io.holoinsight.server.home.proto.base.DataBaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.home.proto.alarm.QueryAlarmHistoryDetailPageRequest, io.holoinsight.server.home.proto.base.DataBaseResponse> getQueryAlarmHistoryDetailByPageMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.home.proto.alarm.QueryAlarmHistoryDetailPageRequest, io.holoinsight.server.home.proto.base.DataBaseResponse> getQueryAlarmHistoryDetailByPageMethod;
    if ((getQueryAlarmHistoryDetailByPageMethod =
        AlarmServiceGrpc.getQueryAlarmHistoryDetailByPageMethod) == null) {
      synchronized (AlarmServiceGrpc.class) {
        if ((getQueryAlarmHistoryDetailByPageMethod =
            AlarmServiceGrpc.getQueryAlarmHistoryDetailByPageMethod) == null) {
          AlarmServiceGrpc.getQueryAlarmHistoryDetailByPageMethod =
              getQueryAlarmHistoryDetailByPageMethod =
                  io.grpc.MethodDescriptor.<io.holoinsight.server.home.proto.alarm.QueryAlarmHistoryDetailPageRequest, io.holoinsight.server.home.proto.base.DataBaseResponse>newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "QueryAlarmHistoryDetailByPage"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                          io.holoinsight.server.home.proto.alarm.QueryAlarmHistoryDetailPageRequest
                              .getDefaultInstance()))
                      .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                          .marshaller(io.holoinsight.server.home.proto.base.DataBaseResponse
                              .getDefaultInstance()))
                      .setSchemaDescriptor(
                          new AlarmServiceMethodDescriptorSupplier("QueryAlarmHistoryDetailByPage"))
                      .build();
        }
      }
    }
    return getQueryAlarmHistoryDetailByPageMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static AlarmServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AlarmServiceStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<AlarmServiceStub>() {
          @Override
          public AlarmServiceStub newStub(io.grpc.Channel channel,
              io.grpc.CallOptions callOptions) {
            return new AlarmServiceStub(channel, callOptions);
          }
        };
    return AlarmServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static AlarmServiceBlockingStub newBlockingStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AlarmServiceBlockingStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<AlarmServiceBlockingStub>() {
          @Override
          public AlarmServiceBlockingStub newStub(io.grpc.Channel channel,
              io.grpc.CallOptions callOptions) {
            return new AlarmServiceBlockingStub(channel, callOptions);
          }
        };
    return AlarmServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static AlarmServiceFutureStub newFutureStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AlarmServiceFutureStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<AlarmServiceFutureStub>() {
          @Override
          public AlarmServiceFutureStub newStub(io.grpc.Channel channel,
              io.grpc.CallOptions callOptions) {
            return new AlarmServiceFutureStub(channel, callOptions);
          }
        };
    return AlarmServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class AlarmServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void queryById(io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.home.proto.base.DataBaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getQueryByIdMethod(), responseObserver);
    }

    /**
     */
    public void deleteById(io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.home.proto.base.DataBaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDeleteByIdMethod(), responseObserver);
    }

    /**
     */
    public void shutdownById(io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.home.proto.base.DataBaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getShutdownByIdMethod(),
          responseObserver);
    }

    /**
     */
    public void create(io.holoinsight.server.home.proto.alarm.InsertOrUpdateRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.home.proto.base.DataBaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCreateMethod(), responseObserver);
    }

    /**
     */
    public void update(io.holoinsight.server.home.proto.alarm.InsertOrUpdateRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.home.proto.base.DataBaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getUpdateMethod(), responseObserver);
    }

    /**
     */
    public void queryAlarmHistoryByPage(
        io.holoinsight.server.home.proto.alarm.QueryAlarmHistoryPageRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.home.proto.base.DataBaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getQueryAlarmHistoryByPageMethod(),
          responseObserver);
    }

    /**
     */
    public void queryAlarmHistoryDetailByPage(
        io.holoinsight.server.home.proto.alarm.QueryAlarmHistoryDetailPageRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.home.proto.base.DataBaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getQueryAlarmHistoryDetailByPageMethod(),
          responseObserver);
    }

    @Override
    public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(getQueryByIdMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest, io.holoinsight.server.home.proto.base.DataBaseResponse>(
                  this, METHODID_QUERY_BY_ID)))
          .addMethod(getDeleteByIdMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest, io.holoinsight.server.home.proto.base.DataBaseResponse>(
                  this, METHODID_DELETE_BY_ID)))
          .addMethod(getShutdownByIdMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest, io.holoinsight.server.home.proto.base.DataBaseResponse>(
                  this, METHODID_SHUTDOWN_BY_ID)))
          .addMethod(getCreateMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.home.proto.alarm.InsertOrUpdateRequest, io.holoinsight.server.home.proto.base.DataBaseResponse>(
                  this, METHODID_CREATE)))
          .addMethod(getUpdateMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.home.proto.alarm.InsertOrUpdateRequest, io.holoinsight.server.home.proto.base.DataBaseResponse>(
                  this, METHODID_UPDATE)))
          .addMethod(getQueryAlarmHistoryByPageMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.home.proto.alarm.QueryAlarmHistoryPageRequest, io.holoinsight.server.home.proto.base.DataBaseResponse>(
                  this, METHODID_QUERY_ALARM_HISTORY_BY_PAGE)))
          .addMethod(getQueryAlarmHistoryDetailByPageMethod(),
              io.grpc.stub.ServerCalls.asyncUnaryCall(
                  new MethodHandlers<io.holoinsight.server.home.proto.alarm.QueryAlarmHistoryDetailPageRequest, io.holoinsight.server.home.proto.base.DataBaseResponse>(
                      this, METHODID_QUERY_ALARM_HISTORY_DETAIL_BY_PAGE)))
          .build();
    }
  }

  /**
   */
  public static final class AlarmServiceStub
      extends io.grpc.stub.AbstractAsyncStub<AlarmServiceStub> {
    private AlarmServiceStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected AlarmServiceStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AlarmServiceStub(channel, callOptions);
    }

    /**
     */
    public void queryById(io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.home.proto.base.DataBaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getQueryByIdMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void deleteById(io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.home.proto.base.DataBaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDeleteByIdMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void shutdownById(io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.home.proto.base.DataBaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getShutdownByIdMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void create(io.holoinsight.server.home.proto.alarm.InsertOrUpdateRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.home.proto.base.DataBaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void update(io.holoinsight.server.home.proto.alarm.InsertOrUpdateRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.home.proto.base.DataBaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUpdateMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void queryAlarmHistoryByPage(
        io.holoinsight.server.home.proto.alarm.QueryAlarmHistoryPageRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.home.proto.base.DataBaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getQueryAlarmHistoryByPageMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void queryAlarmHistoryDetailByPage(
        io.holoinsight.server.home.proto.alarm.QueryAlarmHistoryDetailPageRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.home.proto.base.DataBaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getQueryAlarmHistoryDetailByPageMethod(), getCallOptions()), request,
          responseObserver);
    }
  }

  /**
   */
  public static final class AlarmServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<AlarmServiceBlockingStub> {
    private AlarmServiceBlockingStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected AlarmServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new AlarmServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public io.holoinsight.server.home.proto.base.DataBaseResponse queryById(
        io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getQueryByIdMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.home.proto.base.DataBaseResponse deleteById(
        io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getDeleteByIdMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.home.proto.base.DataBaseResponse shutdownById(
        io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getShutdownByIdMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.home.proto.base.DataBaseResponse create(
        io.holoinsight.server.home.proto.alarm.InsertOrUpdateRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getCreateMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.home.proto.base.DataBaseResponse update(
        io.holoinsight.server.home.proto.alarm.InsertOrUpdateRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getUpdateMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.home.proto.base.DataBaseResponse queryAlarmHistoryByPage(
        io.holoinsight.server.home.proto.alarm.QueryAlarmHistoryPageRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(),
          getQueryAlarmHistoryByPageMethod(), getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.home.proto.base.DataBaseResponse queryAlarmHistoryDetailByPage(
        io.holoinsight.server.home.proto.alarm.QueryAlarmHistoryDetailPageRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(),
          getQueryAlarmHistoryDetailByPageMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class AlarmServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<AlarmServiceFutureStub> {
    private AlarmServiceFutureStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected AlarmServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new AlarmServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.home.proto.base.DataBaseResponse> queryById(
        io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest request) {
      return io.grpc.stub.ClientCalls
          .futureUnaryCall(getChannel().newCall(getQueryByIdMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.home.proto.base.DataBaseResponse> deleteById(
        io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest request) {
      return io.grpc.stub.ClientCalls
          .futureUnaryCall(getChannel().newCall(getDeleteByIdMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.home.proto.base.DataBaseResponse> shutdownById(
        io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getShutdownByIdMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.home.proto.base.DataBaseResponse> create(
        io.holoinsight.server.home.proto.alarm.InsertOrUpdateRequest request) {
      return io.grpc.stub.ClientCalls
          .futureUnaryCall(getChannel().newCall(getCreateMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.home.proto.base.DataBaseResponse> update(
        io.holoinsight.server.home.proto.alarm.InsertOrUpdateRequest request) {
      return io.grpc.stub.ClientCalls
          .futureUnaryCall(getChannel().newCall(getUpdateMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.home.proto.base.DataBaseResponse> queryAlarmHistoryByPage(
        io.holoinsight.server.home.proto.alarm.QueryAlarmHistoryPageRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getQueryAlarmHistoryByPageMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.home.proto.base.DataBaseResponse> queryAlarmHistoryDetailByPage(
        io.holoinsight.server.home.proto.alarm.QueryAlarmHistoryDetailPageRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getQueryAlarmHistoryDetailByPageMethod(), getCallOptions()),
          request);
    }
  }

  private static final int METHODID_QUERY_BY_ID = 0;
  private static final int METHODID_DELETE_BY_ID = 1;
  private static final int METHODID_SHUTDOWN_BY_ID = 2;
  private static final int METHODID_CREATE = 3;
  private static final int METHODID_UPDATE = 4;
  private static final int METHODID_QUERY_ALARM_HISTORY_BY_PAGE = 5;
  private static final int METHODID_QUERY_ALARM_HISTORY_DETAIL_BY_PAGE = 6;

  private static final class MethodHandlers<Req, Resp>
      implements io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AlarmServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(AlarmServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_QUERY_BY_ID:
          serviceImpl.queryById(
              (io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.home.proto.base.DataBaseResponse>) responseObserver);
          break;
        case METHODID_DELETE_BY_ID:
          serviceImpl.deleteById(
              (io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.home.proto.base.DataBaseResponse>) responseObserver);
          break;
        case METHODID_SHUTDOWN_BY_ID:
          serviceImpl.shutdownById(
              (io.holoinsight.server.home.proto.alarm.QueryOrDeleteRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.home.proto.base.DataBaseResponse>) responseObserver);
          break;
        case METHODID_CREATE:
          serviceImpl.create((io.holoinsight.server.home.proto.alarm.InsertOrUpdateRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.home.proto.base.DataBaseResponse>) responseObserver);
          break;
        case METHODID_UPDATE:
          serviceImpl.update((io.holoinsight.server.home.proto.alarm.InsertOrUpdateRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.home.proto.base.DataBaseResponse>) responseObserver);
          break;
        case METHODID_QUERY_ALARM_HISTORY_BY_PAGE:
          serviceImpl.queryAlarmHistoryByPage(
              (io.holoinsight.server.home.proto.alarm.QueryAlarmHistoryPageRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.home.proto.base.DataBaseResponse>) responseObserver);
          break;
        case METHODID_QUERY_ALARM_HISTORY_DETAIL_BY_PAGE:
          serviceImpl.queryAlarmHistoryDetailByPage(
              (io.holoinsight.server.home.proto.alarm.QueryAlarmHistoryDetailPageRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.home.proto.base.DataBaseResponse>) responseObserver);
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

  private static abstract class AlarmServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier,
      io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    AlarmServiceBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return io.holoinsight.server.home.proto.alarm.AlarmServiceProto.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("AlarmService");
    }
  }

  private static final class AlarmServiceFileDescriptorSupplier
      extends AlarmServiceBaseDescriptorSupplier {
    AlarmServiceFileDescriptorSupplier() {}
  }

  private static final class AlarmServiceMethodDescriptorSupplier extends
      AlarmServiceBaseDescriptorSupplier implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    AlarmServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (AlarmServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new AlarmServiceFileDescriptorSupplier())
              .addMethod(getQueryByIdMethod()).addMethod(getDeleteByIdMethod())
              .addMethod(getShutdownByIdMethod()).addMethod(getCreateMethod())
              .addMethod(getUpdateMethod()).addMethod(getQueryAlarmHistoryByPageMethod())
              .addMethod(getQueryAlarmHistoryDetailByPageMethod()).build();
        }
      }
    }
    return result;
  }
}
