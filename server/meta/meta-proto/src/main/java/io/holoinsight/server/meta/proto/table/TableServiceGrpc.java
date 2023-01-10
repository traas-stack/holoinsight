/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.proto.table;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(value = "by gRPC proto compiler (version 1.42.2)",
    comments = "Source: TableService.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class TableServiceGrpc {

  private TableServiceGrpc() {}

  public static final String SERVICE_NAME = "scheduler.TableService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.table.CreateTableRequest, io.holoinsight.server.meta.proto.table.TableBaseResponse> getCreateTableMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "createTable",
      requestType = io.holoinsight.server.meta.proto.table.CreateTableRequest.class,
      responseType = io.holoinsight.server.meta.proto.table.TableBaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.table.CreateTableRequest, io.holoinsight.server.meta.proto.table.TableBaseResponse> getCreateTableMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.table.CreateTableRequest, io.holoinsight.server.meta.proto.table.TableBaseResponse> getCreateTableMethod;
    if ((getCreateTableMethod = TableServiceGrpc.getCreateTableMethod) == null) {
      synchronized (TableServiceGrpc.class) {
        if ((getCreateTableMethod = TableServiceGrpc.getCreateTableMethod) == null) {
          TableServiceGrpc.getCreateTableMethod = getCreateTableMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.meta.proto.table.CreateTableRequest, io.holoinsight.server.meta.proto.table.TableBaseResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "createTable"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.meta.proto.table.CreateTableRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.meta.proto.table.TableBaseResponse
                          .getDefaultInstance()))
                  .setSchemaDescriptor(new TableServiceMethodDescriptorSupplier("createTable"))
                  .build();
        }
      }
    }
    return getCreateTableMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.table.DeleteTableRequest, io.holoinsight.server.meta.proto.table.TableBaseResponse> getDeleteTableMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "deleteTable",
      requestType = io.holoinsight.server.meta.proto.table.DeleteTableRequest.class,
      responseType = io.holoinsight.server.meta.proto.table.TableBaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.table.DeleteTableRequest, io.holoinsight.server.meta.proto.table.TableBaseResponse> getDeleteTableMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.table.DeleteTableRequest, io.holoinsight.server.meta.proto.table.TableBaseResponse> getDeleteTableMethod;
    if ((getDeleteTableMethod = TableServiceGrpc.getDeleteTableMethod) == null) {
      synchronized (TableServiceGrpc.class) {
        if ((getDeleteTableMethod = TableServiceGrpc.getDeleteTableMethod) == null) {
          TableServiceGrpc.getDeleteTableMethod = getDeleteTableMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.meta.proto.table.DeleteTableRequest, io.holoinsight.server.meta.proto.table.TableBaseResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "deleteTable"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.meta.proto.table.DeleteTableRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.meta.proto.table.TableBaseResponse
                          .getDefaultInstance()))
                  .setSchemaDescriptor(new TableServiceMethodDescriptorSupplier("deleteTable"))
                  .build();
        }
      }
    }
    return getDeleteTableMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.table.CreateIndexKeyRequest, io.holoinsight.server.meta.proto.table.TableBaseResponse> getCreateIndexKeyMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "createIndexKey",
      requestType = io.holoinsight.server.meta.proto.table.CreateIndexKeyRequest.class,
      responseType = io.holoinsight.server.meta.proto.table.TableBaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.table.CreateIndexKeyRequest, io.holoinsight.server.meta.proto.table.TableBaseResponse> getCreateIndexKeyMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.table.CreateIndexKeyRequest, io.holoinsight.server.meta.proto.table.TableBaseResponse> getCreateIndexKeyMethod;
    if ((getCreateIndexKeyMethod = TableServiceGrpc.getCreateIndexKeyMethod) == null) {
      synchronized (TableServiceGrpc.class) {
        if ((getCreateIndexKeyMethod = TableServiceGrpc.getCreateIndexKeyMethod) == null) {
          TableServiceGrpc.getCreateIndexKeyMethod = getCreateIndexKeyMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.meta.proto.table.CreateIndexKeyRequest, io.holoinsight.server.meta.proto.table.TableBaseResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "createIndexKey"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.meta.proto.table.CreateIndexKeyRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.meta.proto.table.TableBaseResponse
                          .getDefaultInstance()))
                  .setSchemaDescriptor(new TableServiceMethodDescriptorSupplier("createIndexKey"))
                  .build();
        }
      }
    }
    return getCreateIndexKeyMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.table.DeleteIndexKeyRequest, io.holoinsight.server.meta.proto.table.TableBaseResponse> getDeleteIndexKeyMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "deleteIndexKey",
      requestType = io.holoinsight.server.meta.proto.table.DeleteIndexKeyRequest.class,
      responseType = io.holoinsight.server.meta.proto.table.TableBaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.table.DeleteIndexKeyRequest, io.holoinsight.server.meta.proto.table.TableBaseResponse> getDeleteIndexKeyMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.table.DeleteIndexKeyRequest, io.holoinsight.server.meta.proto.table.TableBaseResponse> getDeleteIndexKeyMethod;
    if ((getDeleteIndexKeyMethod = TableServiceGrpc.getDeleteIndexKeyMethod) == null) {
      synchronized (TableServiceGrpc.class) {
        if ((getDeleteIndexKeyMethod = TableServiceGrpc.getDeleteIndexKeyMethod) == null) {
          TableServiceGrpc.getDeleteIndexKeyMethod = getDeleteIndexKeyMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.meta.proto.table.DeleteIndexKeyRequest, io.holoinsight.server.meta.proto.table.TableBaseResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "deleteIndexKey"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.meta.proto.table.DeleteIndexKeyRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.meta.proto.table.TableBaseResponse
                          .getDefaultInstance()))
                  .setSchemaDescriptor(new TableServiceMethodDescriptorSupplier("deleteIndexKey"))
                  .build();
        }
      }
    }
    return getDeleteIndexKeyMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.table.CreateIndexKeyRequest, io.holoinsight.server.meta.proto.table.TableDataResponse> getGetIndexInfoMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "getIndexInfo",
      requestType = io.holoinsight.server.meta.proto.table.CreateIndexKeyRequest.class,
      responseType = io.holoinsight.server.meta.proto.table.TableDataResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.table.CreateIndexKeyRequest, io.holoinsight.server.meta.proto.table.TableDataResponse> getGetIndexInfoMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.table.CreateIndexKeyRequest, io.holoinsight.server.meta.proto.table.TableDataResponse> getGetIndexInfoMethod;
    if ((getGetIndexInfoMethod = TableServiceGrpc.getGetIndexInfoMethod) == null) {
      synchronized (TableServiceGrpc.class) {
        if ((getGetIndexInfoMethod = TableServiceGrpc.getGetIndexInfoMethod) == null) {
          TableServiceGrpc.getGetIndexInfoMethod = getGetIndexInfoMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.meta.proto.table.CreateIndexKeyRequest, io.holoinsight.server.meta.proto.table.TableDataResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "getIndexInfo"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.meta.proto.table.CreateIndexKeyRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.meta.proto.table.TableDataResponse
                          .getDefaultInstance()))
                  .setSchemaDescriptor(new TableServiceMethodDescriptorSupplier("getIndexInfo"))
                  .build();
        }
      }
    }
    return getGetIndexInfoMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.table.UpdateTableStatusRequest, io.holoinsight.server.meta.proto.table.TableBaseResponse> getUpdateTableStatusMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "updateTableStatus",
      requestType = io.holoinsight.server.meta.proto.table.UpdateTableStatusRequest.class,
      responseType = io.holoinsight.server.meta.proto.table.TableBaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.table.UpdateTableStatusRequest, io.holoinsight.server.meta.proto.table.TableBaseResponse> getUpdateTableStatusMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.table.UpdateTableStatusRequest, io.holoinsight.server.meta.proto.table.TableBaseResponse> getUpdateTableStatusMethod;
    if ((getUpdateTableStatusMethod = TableServiceGrpc.getUpdateTableStatusMethod) == null) {
      synchronized (TableServiceGrpc.class) {
        if ((getUpdateTableStatusMethod = TableServiceGrpc.getUpdateTableStatusMethod) == null) {
          TableServiceGrpc.getUpdateTableStatusMethod = getUpdateTableStatusMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.meta.proto.table.UpdateTableStatusRequest, io.holoinsight.server.meta.proto.table.TableBaseResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "updateTableStatus"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.meta.proto.table.UpdateTableStatusRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.meta.proto.table.TableBaseResponse
                          .getDefaultInstance()))
                  .setSchemaDescriptor(
                      new TableServiceMethodDescriptorSupplier("updateTableStatus"))
                  .build();
        }
      }
    }
    return getUpdateTableStatusMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.table.TableHello, io.holoinsight.server.meta.proto.table.TableHello> getHeartBeatMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "heartBeat",
      requestType = io.holoinsight.server.meta.proto.table.TableHello.class,
      responseType = io.holoinsight.server.meta.proto.table.TableHello.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.table.TableHello, io.holoinsight.server.meta.proto.table.TableHello> getHeartBeatMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.table.TableHello, io.holoinsight.server.meta.proto.table.TableHello> getHeartBeatMethod;
    if ((getHeartBeatMethod = TableServiceGrpc.getHeartBeatMethod) == null) {
      synchronized (TableServiceGrpc.class) {
        if ((getHeartBeatMethod = TableServiceGrpc.getHeartBeatMethod) == null) {
          TableServiceGrpc.getHeartBeatMethod = getHeartBeatMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.meta.proto.table.TableHello, io.holoinsight.server.meta.proto.table.TableHello>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "heartBeat"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.meta.proto.table.TableHello.getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.meta.proto.table.TableHello.getDefaultInstance()))
                  .setSchemaDescriptor(new TableServiceMethodDescriptorSupplier("heartBeat"))
                  .build();
        }
      }
    }
    return getHeartBeatMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static TableServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TableServiceStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<TableServiceStub>() {
          @java.lang.Override
          public TableServiceStub newStub(io.grpc.Channel channel,
              io.grpc.CallOptions callOptions) {
            return new TableServiceStub(channel, callOptions);
          }
        };
    return TableServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static TableServiceBlockingStub newBlockingStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TableServiceBlockingStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<TableServiceBlockingStub>() {
          @java.lang.Override
          public TableServiceBlockingStub newStub(io.grpc.Channel channel,
              io.grpc.CallOptions callOptions) {
            return new TableServiceBlockingStub(channel, callOptions);
          }
        };
    return TableServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static TableServiceFutureStub newFutureStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TableServiceFutureStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<TableServiceFutureStub>() {
          @java.lang.Override
          public TableServiceFutureStub newStub(io.grpc.Channel channel,
              io.grpc.CallOptions callOptions) {
            return new TableServiceFutureStub(channel, callOptions);
          }
        };
    return TableServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class TableServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void createTable(io.holoinsight.server.meta.proto.table.CreateTableRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.table.TableBaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCreateTableMethod(),
          responseObserver);
    }

    /**
     */
    public void deleteTable(io.holoinsight.server.meta.proto.table.DeleteTableRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.table.TableBaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDeleteTableMethod(),
          responseObserver);
    }

    /**
     */
    public void createIndexKey(io.holoinsight.server.meta.proto.table.CreateIndexKeyRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.table.TableBaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCreateIndexKeyMethod(),
          responseObserver);
    }

    /**
     */
    public void deleteIndexKey(io.holoinsight.server.meta.proto.table.DeleteIndexKeyRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.table.TableBaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDeleteIndexKeyMethod(),
          responseObserver);
    }

    /**
     */
    public void getIndexInfo(io.holoinsight.server.meta.proto.table.CreateIndexKeyRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.table.TableDataResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetIndexInfoMethod(),
          responseObserver);
    }

    /**
     */
    public void updateTableStatus(
        io.holoinsight.server.meta.proto.table.UpdateTableStatusRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.table.TableBaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getUpdateTableStatusMethod(),
          responseObserver);
    }

    /**
     * <pre>
     * 心跳
     * </pre>
     */
    public void heartBeat(io.holoinsight.server.meta.proto.table.TableHello request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.table.TableHello> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getHeartBeatMethod(), responseObserver);
    }

    @java.lang.Override
    public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(getCreateTableMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.meta.proto.table.CreateTableRequest, io.holoinsight.server.meta.proto.table.TableBaseResponse>(
                  this, METHODID_CREATE_TABLE)))
          .addMethod(getDeleteTableMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.meta.proto.table.DeleteTableRequest, io.holoinsight.server.meta.proto.table.TableBaseResponse>(
                  this, METHODID_DELETE_TABLE)))
          .addMethod(getCreateIndexKeyMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.meta.proto.table.CreateIndexKeyRequest, io.holoinsight.server.meta.proto.table.TableBaseResponse>(
                  this, METHODID_CREATE_INDEX_KEY)))
          .addMethod(getDeleteIndexKeyMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.meta.proto.table.DeleteIndexKeyRequest, io.holoinsight.server.meta.proto.table.TableBaseResponse>(
                  this, METHODID_DELETE_INDEX_KEY)))
          .addMethod(getGetIndexInfoMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.meta.proto.table.CreateIndexKeyRequest, io.holoinsight.server.meta.proto.table.TableDataResponse>(
                  this, METHODID_GET_INDEX_INFO)))
          .addMethod(getUpdateTableStatusMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.meta.proto.table.UpdateTableStatusRequest, io.holoinsight.server.meta.proto.table.TableBaseResponse>(
                  this, METHODID_UPDATE_TABLE_STATUS)))
          .addMethod(getHeartBeatMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.meta.proto.table.TableHello, io.holoinsight.server.meta.proto.table.TableHello>(
                  this, METHODID_HEART_BEAT)))
          .build();
    }
  }

  /**
   */
  public static final class TableServiceStub
      extends io.grpc.stub.AbstractAsyncStub<TableServiceStub> {
    private TableServiceStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TableServiceStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new TableServiceStub(channel, callOptions);
    }

    /**
     */
    public void createTable(io.holoinsight.server.meta.proto.table.CreateTableRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.table.TableBaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateTableMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void deleteTable(io.holoinsight.server.meta.proto.table.DeleteTableRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.table.TableBaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDeleteTableMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void createIndexKey(io.holoinsight.server.meta.proto.table.CreateIndexKeyRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.table.TableBaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateIndexKeyMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void deleteIndexKey(io.holoinsight.server.meta.proto.table.DeleteIndexKeyRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.table.TableBaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDeleteIndexKeyMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void getIndexInfo(io.holoinsight.server.meta.proto.table.CreateIndexKeyRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.table.TableDataResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetIndexInfoMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void updateTableStatus(
        io.holoinsight.server.meta.proto.table.UpdateTableStatusRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.table.TableBaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUpdateTableStatusMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     * <pre>
     * 心跳
     * </pre>
     */
    public void heartBeat(io.holoinsight.server.meta.proto.table.TableHello request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.table.TableHello> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getHeartBeatMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class TableServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<TableServiceBlockingStub> {
    private TableServiceBlockingStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TableServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TableServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public io.holoinsight.server.meta.proto.table.TableBaseResponse createTable(
        io.holoinsight.server.meta.proto.table.CreateTableRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getCreateTableMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.meta.proto.table.TableBaseResponse deleteTable(
        io.holoinsight.server.meta.proto.table.DeleteTableRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getDeleteTableMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.meta.proto.table.TableBaseResponse createIndexKey(
        io.holoinsight.server.meta.proto.table.CreateIndexKeyRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getCreateIndexKeyMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.meta.proto.table.TableBaseResponse deleteIndexKey(
        io.holoinsight.server.meta.proto.table.DeleteIndexKeyRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getDeleteIndexKeyMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.meta.proto.table.TableDataResponse getIndexInfo(
        io.holoinsight.server.meta.proto.table.CreateIndexKeyRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getGetIndexInfoMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.meta.proto.table.TableBaseResponse updateTableStatus(
        io.holoinsight.server.meta.proto.table.UpdateTableStatusRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getUpdateTableStatusMethod(),
          getCallOptions(), request);
    }

    /**
     * <pre>
     * 心跳
     * </pre>
     */
    public io.holoinsight.server.meta.proto.table.TableHello heartBeat(
        io.holoinsight.server.meta.proto.table.TableHello request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getHeartBeatMethod(),
          getCallOptions(), request);
    }
  }

  /**
   */
  public static final class TableServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<TableServiceFutureStub> {
    private TableServiceFutureStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TableServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new TableServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.meta.proto.table.TableBaseResponse> createTable(
        io.holoinsight.server.meta.proto.table.CreateTableRequest request) {
      return io.grpc.stub.ClientCalls
          .futureUnaryCall(getChannel().newCall(getCreateTableMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.meta.proto.table.TableBaseResponse> deleteTable(
        io.holoinsight.server.meta.proto.table.DeleteTableRequest request) {
      return io.grpc.stub.ClientCalls
          .futureUnaryCall(getChannel().newCall(getDeleteTableMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.meta.proto.table.TableBaseResponse> createIndexKey(
        io.holoinsight.server.meta.proto.table.CreateIndexKeyRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreateIndexKeyMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.meta.proto.table.TableBaseResponse> deleteIndexKey(
        io.holoinsight.server.meta.proto.table.DeleteIndexKeyRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDeleteIndexKeyMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.meta.proto.table.TableDataResponse> getIndexInfo(
        io.holoinsight.server.meta.proto.table.CreateIndexKeyRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetIndexInfoMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.meta.proto.table.TableBaseResponse> updateTableStatus(
        io.holoinsight.server.meta.proto.table.UpdateTableStatusRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getUpdateTableStatusMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * 心跳
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.meta.proto.table.TableHello> heartBeat(
        io.holoinsight.server.meta.proto.table.TableHello request) {
      return io.grpc.stub.ClientCalls
          .futureUnaryCall(getChannel().newCall(getHeartBeatMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CREATE_TABLE = 0;
  private static final int METHODID_DELETE_TABLE = 1;
  private static final int METHODID_CREATE_INDEX_KEY = 2;
  private static final int METHODID_DELETE_INDEX_KEY = 3;
  private static final int METHODID_GET_INDEX_INFO = 4;
  private static final int METHODID_UPDATE_TABLE_STATUS = 5;
  private static final int METHODID_HEART_BEAT = 6;

  private static final class MethodHandlers<Req, Resp>
      implements io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final TableServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(TableServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CREATE_TABLE:
          serviceImpl.createTable(
              (io.holoinsight.server.meta.proto.table.CreateTableRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.table.TableBaseResponse>) responseObserver);
          break;
        case METHODID_DELETE_TABLE:
          serviceImpl.deleteTable(
              (io.holoinsight.server.meta.proto.table.DeleteTableRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.table.TableBaseResponse>) responseObserver);
          break;
        case METHODID_CREATE_INDEX_KEY:
          serviceImpl.createIndexKey(
              (io.holoinsight.server.meta.proto.table.CreateIndexKeyRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.table.TableBaseResponse>) responseObserver);
          break;
        case METHODID_DELETE_INDEX_KEY:
          serviceImpl.deleteIndexKey(
              (io.holoinsight.server.meta.proto.table.DeleteIndexKeyRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.table.TableBaseResponse>) responseObserver);
          break;
        case METHODID_GET_INDEX_INFO:
          serviceImpl.getIndexInfo(
              (io.holoinsight.server.meta.proto.table.CreateIndexKeyRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.table.TableDataResponse>) responseObserver);
          break;
        case METHODID_UPDATE_TABLE_STATUS:
          serviceImpl.updateTableStatus(
              (io.holoinsight.server.meta.proto.table.UpdateTableStatusRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.table.TableBaseResponse>) responseObserver);
          break;
        case METHODID_HEART_BEAT:
          serviceImpl.heartBeat((io.holoinsight.server.meta.proto.table.TableHello) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.table.TableHello>) responseObserver);
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

  private static abstract class TableServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier,
      io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    TableServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return io.holoinsight.server.meta.proto.table.TableServiceProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("TableService");
    }
  }

  private static final class TableServiceFileDescriptorSupplier
      extends TableServiceBaseDescriptorSupplier {
    TableServiceFileDescriptorSupplier() {}
  }

  private static final class TableServiceMethodDescriptorSupplier extends
      TableServiceBaseDescriptorSupplier implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    TableServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (TableServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new TableServiceFileDescriptorSupplier())
              .addMethod(getCreateTableMethod()).addMethod(getDeleteTableMethod())
              .addMethod(getCreateIndexKeyMethod()).addMethod(getDeleteIndexKeyMethod())
              .addMethod(getGetIndexInfoMethod()).addMethod(getUpdateTableStatusMethod())
              .addMethod(getHeartBeatMethod()).build();
        }
      }
    }
    return result;
  }
}
