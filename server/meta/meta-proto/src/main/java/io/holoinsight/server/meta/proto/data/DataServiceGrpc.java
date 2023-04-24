/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.proto.data;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * 定义Query相关接口
 * </pre>
 */
@javax.annotation.Generated(value = "by gRPC proto compiler (version 1.42.2)",
    comments = "Source: DataService.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class DataServiceGrpc {

  private DataServiceGrpc() {}

  public static final String SERVICE_NAME = "scheduler.DataService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse> getInsertOrUpdateMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "insertOrUpdate",
      requestType = io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest.class,
      responseType = io.holoinsight.server.meta.proto.data.DataBaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse> getInsertOrUpdateMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse> getInsertOrUpdateMethod;
    if ((getInsertOrUpdateMethod = DataServiceGrpc.getInsertOrUpdateMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getInsertOrUpdateMethod = DataServiceGrpc.getInsertOrUpdateMethod) == null) {
          DataServiceGrpc.getInsertOrUpdateMethod = getInsertOrUpdateMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "insertOrUpdate"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.meta.proto.data.DataBaseResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("insertOrUpdate"))
                  .build();
        }
      }
    }
    return getInsertOrUpdateMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse> getInsertMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "insert",
      requestType = io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest.class,
      responseType = io.holoinsight.server.meta.proto.data.DataBaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse> getInsertMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse> getInsertMethod;
    if ((getInsertMethod = DataServiceGrpc.getInsertMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getInsertMethod = DataServiceGrpc.getInsertMethod) == null) {
          DataServiceGrpc.getInsertMethod = getInsertMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "insert"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.meta.proto.data.DataBaseResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("insert")).build();
        }
      }
    }
    return getInsertMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse> getUpdateMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "update",
      requestType = io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest.class,
      responseType = io.holoinsight.server.meta.proto.data.DataBaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse> getUpdateMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse> getUpdateMethod;
    if ((getUpdateMethod = DataServiceGrpc.getUpdateMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getUpdateMethod = DataServiceGrpc.getUpdateMethod) == null) {
          DataServiceGrpc.getUpdateMethod = getUpdateMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "update"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.meta.proto.data.DataBaseResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("update")).build();
        }
      }
    }
    return getUpdateMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.QueryDataByTableRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse> getQueryDataByTableMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "queryDataByTable",
      requestType = io.holoinsight.server.meta.proto.data.QueryDataByTableRequest.class,
      responseType = io.holoinsight.server.meta.proto.data.QueryDataResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.QueryDataByTableRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse> getQueryDataByTableMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.QueryDataByTableRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse> getQueryDataByTableMethod;
    if ((getQueryDataByTableMethod = DataServiceGrpc.getQueryDataByTableMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getQueryDataByTableMethod = DataServiceGrpc.getQueryDataByTableMethod) == null) {
          DataServiceGrpc.getQueryDataByTableMethod = getQueryDataByTableMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.meta.proto.data.QueryDataByTableRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "queryDataByTable"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.meta.proto.data.QueryDataByTableRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.meta.proto.data.QueryDataResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("queryDataByTable"))
                  .build();
        }
      }
    }
    return getQueryDataByTableMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.QueryDataByPkRequest, io.holoinsight.server.meta.proto.data.QueryOneDataResponse> getQueryDataByPkMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "queryDataByPk",
      requestType = io.holoinsight.server.meta.proto.data.QueryDataByPkRequest.class,
      responseType = io.holoinsight.server.meta.proto.data.QueryOneDataResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.QueryDataByPkRequest, io.holoinsight.server.meta.proto.data.QueryOneDataResponse> getQueryDataByPkMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.QueryDataByPkRequest, io.holoinsight.server.meta.proto.data.QueryOneDataResponse> getQueryDataByPkMethod;
    if ((getQueryDataByPkMethod = DataServiceGrpc.getQueryDataByPkMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getQueryDataByPkMethod = DataServiceGrpc.getQueryDataByPkMethod) == null) {
          DataServiceGrpc.getQueryDataByPkMethod = getQueryDataByPkMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.meta.proto.data.QueryDataByPkRequest, io.holoinsight.server.meta.proto.data.QueryOneDataResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "queryDataByPk"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.meta.proto.data.QueryDataByPkRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.meta.proto.data.QueryOneDataResponse
                          .getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("queryDataByPk"))
                  .build();
        }
      }
    }
    return getQueryDataByPkMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.QueryDataByPksRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse> getQueryDataByPksMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "queryDataByPks",
      requestType = io.holoinsight.server.meta.proto.data.QueryDataByPksRequest.class,
      responseType = io.holoinsight.server.meta.proto.data.QueryDataResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.QueryDataByPksRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse> getQueryDataByPksMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.QueryDataByPksRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse> getQueryDataByPksMethod;
    if ((getQueryDataByPksMethod = DataServiceGrpc.getQueryDataByPksMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getQueryDataByPksMethod = DataServiceGrpc.getQueryDataByPksMethod) == null) {
          DataServiceGrpc.getQueryDataByPksMethod = getQueryDataByPksMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.meta.proto.data.QueryDataByPksRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "queryDataByPks"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.meta.proto.data.QueryDataByPksRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.meta.proto.data.QueryDataResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("queryDataByPks"))
                  .build();
        }
      }
    }
    return getQueryDataByPksMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.QueryDataByTableRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse> getQueryDataByTableStreamMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "queryDataByTableStream",
      requestType = io.holoinsight.server.meta.proto.data.QueryDataByTableRequest.class,
      responseType = io.holoinsight.server.meta.proto.data.QueryDataResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.QueryDataByTableRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse> getQueryDataByTableStreamMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.QueryDataByTableRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse> getQueryDataByTableStreamMethod;
    if ((getQueryDataByTableStreamMethod =
        DataServiceGrpc.getQueryDataByTableStreamMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getQueryDataByTableStreamMethod =
            DataServiceGrpc.getQueryDataByTableStreamMethod) == null) {
          DataServiceGrpc.getQueryDataByTableStreamMethod = getQueryDataByTableStreamMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.meta.proto.data.QueryDataByTableRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "queryDataByTableStream"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.meta.proto.data.QueryDataByTableRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.meta.proto.data.QueryDataResponse.getDefaultInstance()))
                  .setSchemaDescriptor(
                      new DataServiceMethodDescriptorSupplier("queryDataByTableStream"))
                  .build();
        }
      }
    }
    return getQueryDataByTableStreamMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.QueryDataByTableRowsRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse> getQueryDataByTableRowsStreamMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "queryDataByTableRowsStream",
      requestType = io.holoinsight.server.meta.proto.data.QueryDataByTableRowsRequest.class,
      responseType = io.holoinsight.server.meta.proto.data.QueryDataResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.QueryDataByTableRowsRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse> getQueryDataByTableRowsStreamMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.QueryDataByTableRowsRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse> getQueryDataByTableRowsStreamMethod;
    if ((getQueryDataByTableRowsStreamMethod =
        DataServiceGrpc.getQueryDataByTableRowsStreamMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getQueryDataByTableRowsStreamMethod =
            DataServiceGrpc.getQueryDataByTableRowsStreamMethod) == null) {
          DataServiceGrpc.getQueryDataByTableRowsStreamMethod =
              getQueryDataByTableRowsStreamMethod =
                  io.grpc.MethodDescriptor.<io.holoinsight.server.meta.proto.data.QueryDataByTableRowsRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse>newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "queryDataByTableRowsStream"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                          io.holoinsight.server.meta.proto.data.QueryDataByTableRowsRequest
                              .getDefaultInstance()))
                      .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                          .marshaller(io.holoinsight.server.meta.proto.data.QueryDataResponse
                              .getDefaultInstance()))
                      .setSchemaDescriptor(
                          new DataServiceMethodDescriptorSupplier("queryDataByTableRowsStream"))
                      .build();
        }
      }
    }
    return getQueryDataByTableRowsStreamMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.BatchDeleteByPkRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse> getBatchDeleteByPkMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "batchDeleteByPk",
      requestType = io.holoinsight.server.meta.proto.data.BatchDeleteByPkRequest.class,
      responseType = io.holoinsight.server.meta.proto.data.DataBaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.BatchDeleteByPkRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse> getBatchDeleteByPkMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.BatchDeleteByPkRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse> getBatchDeleteByPkMethod;
    if ((getBatchDeleteByPkMethod = DataServiceGrpc.getBatchDeleteByPkMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getBatchDeleteByPkMethod = DataServiceGrpc.getBatchDeleteByPkMethod) == null) {
          DataServiceGrpc.getBatchDeleteByPkMethod = getBatchDeleteByPkMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.meta.proto.data.BatchDeleteByPkRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "batchDeleteByPk"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.meta.proto.data.BatchDeleteByPkRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.meta.proto.data.DataBaseResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("batchDeleteByPk"))
                  .build();
        }
      }
    }
    return getBatchDeleteByPkMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.DeleteDataByExampleRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse> getDeleteByExampleMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "deleteByExample",
      requestType = io.holoinsight.server.meta.proto.data.DeleteDataByExampleRequest.class,
      responseType = io.holoinsight.server.meta.proto.data.DataBaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.DeleteDataByExampleRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse> getDeleteByExampleMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.DeleteDataByExampleRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse> getDeleteByExampleMethod;
    if ((getDeleteByExampleMethod = DataServiceGrpc.getDeleteByExampleMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getDeleteByExampleMethod = DataServiceGrpc.getDeleteByExampleMethod) == null) {
          DataServiceGrpc.getDeleteByExampleMethod = getDeleteByExampleMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.meta.proto.data.DeleteDataByExampleRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "deleteByExample"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.meta.proto.data.DeleteDataByExampleRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.meta.proto.data.DataBaseResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("deleteByExample"))
                  .build();
        }
      }
    }
    return getDeleteByExampleMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.DeleteDataByExampleRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse> getDeleteByRowMapMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "deleteByRowMap",
      requestType = io.holoinsight.server.meta.proto.data.DeleteDataByExampleRequest.class,
      responseType = io.holoinsight.server.meta.proto.data.DataBaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.DeleteDataByExampleRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse> getDeleteByRowMapMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.DeleteDataByExampleRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse> getDeleteByRowMapMethod;
    if ((getDeleteByRowMapMethod = DataServiceGrpc.getDeleteByRowMapMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getDeleteByRowMapMethod = DataServiceGrpc.getDeleteByRowMapMethod) == null) {
          DataServiceGrpc.getDeleteByRowMapMethod = getDeleteByRowMapMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.meta.proto.data.DeleteDataByExampleRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "deleteByRowMap"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.meta.proto.data.DeleteDataByExampleRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.meta.proto.data.DataBaseResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("deleteByRowMap"))
                  .build();
        }
      }
    }
    return getDeleteByRowMapMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.UpdateDataByExampleRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse> getUpdateByExampleMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "updateByExample",
      requestType = io.holoinsight.server.meta.proto.data.UpdateDataByExampleRequest.class,
      responseType = io.holoinsight.server.meta.proto.data.DataBaseResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.UpdateDataByExampleRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse> getUpdateByExampleMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.UpdateDataByExampleRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse> getUpdateByExampleMethod;
    if ((getUpdateByExampleMethod = DataServiceGrpc.getUpdateByExampleMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getUpdateByExampleMethod = DataServiceGrpc.getUpdateByExampleMethod) == null) {
          DataServiceGrpc.getUpdateByExampleMethod = getUpdateByExampleMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.meta.proto.data.UpdateDataByExampleRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "updateByExample"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.meta.proto.data.UpdateDataByExampleRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.meta.proto.data.DataBaseResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("updateByExample"))
                  .build();
        }
      }
    }
    return getUpdateByExampleMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse> getQueryByExampleMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "queryByExample",
      requestType = io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest.class,
      responseType = io.holoinsight.server.meta.proto.data.QueryDataResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse> getQueryByExampleMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse> getQueryByExampleMethod;
    if ((getQueryByExampleMethod = DataServiceGrpc.getQueryByExampleMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getQueryByExampleMethod = DataServiceGrpc.getQueryByExampleMethod) == null) {
          DataServiceGrpc.getQueryByExampleMethod = getQueryByExampleMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "queryByExample"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.meta.proto.data.QueryDataResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("queryByExample"))
                  .build();
        }
      }
    }
    return getQueryByExampleMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse> getFuzzyByExampleMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "fuzzyByExample",
      requestType = io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest.class,
      responseType = io.holoinsight.server.meta.proto.data.QueryDataResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse> getFuzzyByExampleMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse> getFuzzyByExampleMethod;
    if ((getFuzzyByExampleMethod = DataServiceGrpc.getFuzzyByExampleMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getFuzzyByExampleMethod = DataServiceGrpc.getFuzzyByExampleMethod) == null) {
          DataServiceGrpc.getFuzzyByExampleMethod = getFuzzyByExampleMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "fuzzyByExample"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.meta.proto.data.QueryDataResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("fuzzyByExample"))
                  .build();
        }
      }
    }
    return getFuzzyByExampleMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse> getQueryByExampleStreamMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "queryByExampleStream",
      requestType = io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest.class,
      responseType = io.holoinsight.server.meta.proto.data.QueryDataResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse> getQueryByExampleStreamMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse> getQueryByExampleStreamMethod;
    if ((getQueryByExampleStreamMethod = DataServiceGrpc.getQueryByExampleStreamMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getQueryByExampleStreamMethod =
            DataServiceGrpc.getQueryByExampleStreamMethod) == null) {
          DataServiceGrpc.getQueryByExampleStreamMethod = getQueryByExampleStreamMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "queryByExampleStream"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.meta.proto.data.QueryDataResponse.getDefaultInstance()))
                  .setSchemaDescriptor(
                      new DataServiceMethodDescriptorSupplier("queryByExampleStream"))
                  .build();
        }
      }
    }
    return getQueryByExampleStreamMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.DataHello, io.holoinsight.server.meta.proto.data.DataHello> getHeartBeatMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "heartBeat",
      requestType = io.holoinsight.server.meta.proto.data.DataHello.class,
      responseType = io.holoinsight.server.meta.proto.data.DataHello.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.DataHello, io.holoinsight.server.meta.proto.data.DataHello> getHeartBeatMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.meta.proto.data.DataHello, io.holoinsight.server.meta.proto.data.DataHello> getHeartBeatMethod;
    if ((getHeartBeatMethod = DataServiceGrpc.getHeartBeatMethod) == null) {
      synchronized (DataServiceGrpc.class) {
        if ((getHeartBeatMethod = DataServiceGrpc.getHeartBeatMethod) == null) {
          DataServiceGrpc.getHeartBeatMethod = getHeartBeatMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.meta.proto.data.DataHello, io.holoinsight.server.meta.proto.data.DataHello>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "heartBeat"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.meta.proto.data.DataHello.getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.meta.proto.data.DataHello.getDefaultInstance()))
                  .setSchemaDescriptor(new DataServiceMethodDescriptorSupplier("heartBeat"))
                  .build();
        }
      }
    }
    return getHeartBeatMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static DataServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DataServiceStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<DataServiceStub>() {
          @java.lang.Override
          public DataServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
            return new DataServiceStub(channel, callOptions);
          }
        };
    return DataServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static DataServiceBlockingStub newBlockingStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DataServiceBlockingStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<DataServiceBlockingStub>() {
          @java.lang.Override
          public DataServiceBlockingStub newStub(io.grpc.Channel channel,
              io.grpc.CallOptions callOptions) {
            return new DataServiceBlockingStub(channel, callOptions);
          }
        };
    return DataServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static DataServiceFutureStub newFutureStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DataServiceFutureStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<DataServiceFutureStub>() {
          @java.lang.Override
          public DataServiceFutureStub newStub(io.grpc.Channel channel,
              io.grpc.CallOptions callOptions) {
            return new DataServiceFutureStub(channel, callOptions);
          }
        };
    return DataServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * 定义Query相关接口
   * </pre>
   */
  public static abstract class DataServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void insertOrUpdate(io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.DataBaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getInsertOrUpdateMethod(),
          responseObserver);
    }

    /**
     */
    public void insert(io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.DataBaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getInsertMethod(), responseObserver);
    }

    /**
     */
    public void update(io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.DataBaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getUpdateMethod(), responseObserver);
    }

    /**
     */
    public void queryDataByTable(
        io.holoinsight.server.meta.proto.data.QueryDataByTableRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.QueryDataResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getQueryDataByTableMethod(),
          responseObserver);
    }

    /**
     */
    public void queryDataByPk(io.holoinsight.server.meta.proto.data.QueryDataByPkRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.QueryOneDataResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getQueryDataByPkMethod(),
          responseObserver);
    }

    /**
     */
    public void queryDataByPks(io.holoinsight.server.meta.proto.data.QueryDataByPksRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.QueryDataResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getQueryDataByPksMethod(),
          responseObserver);
    }

    /**
     */
    public void queryDataByTableStream(
        io.holoinsight.server.meta.proto.data.QueryDataByTableRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.QueryDataResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getQueryDataByTableStreamMethod(),
          responseObserver);
    }

    /**
     */
    public void queryDataByTableRowsStream(
        io.holoinsight.server.meta.proto.data.QueryDataByTableRowsRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.QueryDataResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getQueryDataByTableRowsStreamMethod(),
          responseObserver);
    }

    /**
     */
    public void batchDeleteByPk(
        io.holoinsight.server.meta.proto.data.BatchDeleteByPkRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.DataBaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getBatchDeleteByPkMethod(),
          responseObserver);
    }

    /**
     */
    public void deleteByExample(
        io.holoinsight.server.meta.proto.data.DeleteDataByExampleRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.DataBaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDeleteByExampleMethod(),
          responseObserver);
    }

    /**
     */
    public void deleteByRowMap(
        io.holoinsight.server.meta.proto.data.DeleteDataByExampleRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.DataBaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDeleteByRowMapMethod(),
          responseObserver);
    }

    /**
     */
    public void updateByExample(
        io.holoinsight.server.meta.proto.data.UpdateDataByExampleRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.DataBaseResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getUpdateByExampleMethod(),
          responseObserver);
    }

    /**
     */
    public void queryByExample(
        io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.QueryDataResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getQueryByExampleMethod(),
          responseObserver);
    }

    /**
     */
    public void fuzzyByExample(
        io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.QueryDataResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getFuzzyByExampleMethod(),
          responseObserver);
    }

    /**
     */
    public void queryByExampleStream(
        io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.QueryDataResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getQueryByExampleStreamMethod(),
          responseObserver);
    }

    /**
     * <pre>
     * 心跳
     * </pre>
     */
    public void heartBeat(io.holoinsight.server.meta.proto.data.DataHello request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.DataHello> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getHeartBeatMethod(), responseObserver);
    }

    @java.lang.Override
    public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(getInsertOrUpdateMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse>(
                  this, METHODID_INSERT_OR_UPDATE)))
          .addMethod(getInsertMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse>(
                  this, METHODID_INSERT)))
          .addMethod(getUpdateMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse>(
                  this, METHODID_UPDATE)))
          .addMethod(getQueryDataByTableMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.meta.proto.data.QueryDataByTableRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse>(
                  this, METHODID_QUERY_DATA_BY_TABLE)))
          .addMethod(getQueryDataByPkMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.meta.proto.data.QueryDataByPkRequest, io.holoinsight.server.meta.proto.data.QueryOneDataResponse>(
                  this, METHODID_QUERY_DATA_BY_PK)))
          .addMethod(getQueryDataByPksMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.meta.proto.data.QueryDataByPksRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse>(
                  this, METHODID_QUERY_DATA_BY_PKS)))
          .addMethod(getQueryDataByTableStreamMethod(),
              io.grpc.stub.ServerCalls.asyncServerStreamingCall(
                  new MethodHandlers<io.holoinsight.server.meta.proto.data.QueryDataByTableRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse>(
                      this, METHODID_QUERY_DATA_BY_TABLE_STREAM)))
          .addMethod(getQueryDataByTableRowsStreamMethod(),
              io.grpc.stub.ServerCalls.asyncServerStreamingCall(
                  new MethodHandlers<io.holoinsight.server.meta.proto.data.QueryDataByTableRowsRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse>(
                      this, METHODID_QUERY_DATA_BY_TABLE_ROWS_STREAM)))
          .addMethod(getBatchDeleteByPkMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.meta.proto.data.BatchDeleteByPkRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse>(
                  this, METHODID_BATCH_DELETE_BY_PK)))
          .addMethod(getDeleteByExampleMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.meta.proto.data.DeleteDataByExampleRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse>(
                  this, METHODID_DELETE_BY_EXAMPLE)))
          .addMethod(getDeleteByRowMapMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.meta.proto.data.DeleteDataByExampleRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse>(
                  this, METHODID_DELETE_BY_ROW_MAP)))
          .addMethod(getUpdateByExampleMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.meta.proto.data.UpdateDataByExampleRequest, io.holoinsight.server.meta.proto.data.DataBaseResponse>(
                  this, METHODID_UPDATE_BY_EXAMPLE)))
          .addMethod(getQueryByExampleMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse>(
                  this, METHODID_QUERY_BY_EXAMPLE)))
          .addMethod(getFuzzyByExampleMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse>(
                  this, METHODID_FUZZY_BY_EXAMPLE)))
          .addMethod(getQueryByExampleStreamMethod(),
              io.grpc.stub.ServerCalls.asyncServerStreamingCall(
                  new MethodHandlers<io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest, io.holoinsight.server.meta.proto.data.QueryDataResponse>(
                      this, METHODID_QUERY_BY_EXAMPLE_STREAM)))
          .addMethod(getHeartBeatMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.meta.proto.data.DataHello, io.holoinsight.server.meta.proto.data.DataHello>(
                  this, METHODID_HEART_BEAT)))
          .build();
    }
  }

  /**
   * <pre>
   * 定义Query相关接口
   * </pre>
   */
  public static final class DataServiceStub
      extends io.grpc.stub.AbstractAsyncStub<DataServiceStub> {
    private DataServiceStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DataServiceStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new DataServiceStub(channel, callOptions);
    }

    /**
     */
    public void insertOrUpdate(io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.DataBaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getInsertOrUpdateMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void insert(io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.DataBaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getInsertMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void update(io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.DataBaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUpdateMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void queryDataByTable(
        io.holoinsight.server.meta.proto.data.QueryDataByTableRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.QueryDataResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getQueryDataByTableMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void queryDataByPk(io.holoinsight.server.meta.proto.data.QueryDataByPkRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.QueryOneDataResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getQueryDataByPkMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void queryDataByPks(io.holoinsight.server.meta.proto.data.QueryDataByPksRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.QueryDataResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getQueryDataByPksMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void queryDataByTableStream(
        io.holoinsight.server.meta.proto.data.QueryDataByTableRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.QueryDataResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getQueryDataByTableStreamMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void queryDataByTableRowsStream(
        io.holoinsight.server.meta.proto.data.QueryDataByTableRowsRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.QueryDataResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getQueryDataByTableRowsStreamMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void batchDeleteByPk(
        io.holoinsight.server.meta.proto.data.BatchDeleteByPkRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.DataBaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getBatchDeleteByPkMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void deleteByExample(
        io.holoinsight.server.meta.proto.data.DeleteDataByExampleRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.DataBaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDeleteByExampleMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void deleteByRowMap(
        io.holoinsight.server.meta.proto.data.DeleteDataByExampleRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.DataBaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDeleteByRowMapMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void updateByExample(
        io.holoinsight.server.meta.proto.data.UpdateDataByExampleRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.DataBaseResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUpdateByExampleMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void queryByExample(
        io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.QueryDataResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getQueryByExampleMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void fuzzyByExample(
        io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.QueryDataResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getFuzzyByExampleMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void queryByExampleStream(
        io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.QueryDataResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getQueryByExampleStreamMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     * <pre>
     * 心跳
     * </pre>
     */
    public void heartBeat(io.holoinsight.server.meta.proto.data.DataHello request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.DataHello> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getHeartBeatMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   * 定义Query相关接口
   * </pre>
   */
  public static final class DataServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<DataServiceBlockingStub> {
    private DataServiceBlockingStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DataServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DataServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public io.holoinsight.server.meta.proto.data.DataBaseResponse insertOrUpdate(
        io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getInsertOrUpdateMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.meta.proto.data.DataBaseResponse insert(
        io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getInsertMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.meta.proto.data.DataBaseResponse update(
        io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getUpdateMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.meta.proto.data.QueryDataResponse queryDataByTable(
        io.holoinsight.server.meta.proto.data.QueryDataByTableRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getQueryDataByTableMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.meta.proto.data.QueryOneDataResponse queryDataByPk(
        io.holoinsight.server.meta.proto.data.QueryDataByPkRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getQueryDataByPkMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.meta.proto.data.QueryDataResponse queryDataByPks(
        io.holoinsight.server.meta.proto.data.QueryDataByPksRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getQueryDataByPksMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<io.holoinsight.server.meta.proto.data.QueryDataResponse> queryDataByTableStream(
        io.holoinsight.server.meta.proto.data.QueryDataByTableRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(getChannel(),
          getQueryDataByTableStreamMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<io.holoinsight.server.meta.proto.data.QueryDataResponse> queryDataByTableRowsStream(
        io.holoinsight.server.meta.proto.data.QueryDataByTableRowsRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(getChannel(),
          getQueryDataByTableRowsStreamMethod(), getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.meta.proto.data.DataBaseResponse batchDeleteByPk(
        io.holoinsight.server.meta.proto.data.BatchDeleteByPkRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getBatchDeleteByPkMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.meta.proto.data.DataBaseResponse deleteByExample(
        io.holoinsight.server.meta.proto.data.DeleteDataByExampleRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getDeleteByExampleMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.meta.proto.data.DataBaseResponse deleteByRowMap(
        io.holoinsight.server.meta.proto.data.DeleteDataByExampleRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getDeleteByRowMapMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.meta.proto.data.DataBaseResponse updateByExample(
        io.holoinsight.server.meta.proto.data.UpdateDataByExampleRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getUpdateByExampleMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.meta.proto.data.QueryDataResponse queryByExample(
        io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getQueryByExampleMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.meta.proto.data.QueryDataResponse fuzzyByExample(
        io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getFuzzyByExampleMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<io.holoinsight.server.meta.proto.data.QueryDataResponse> queryByExampleStream(
        io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(getChannel(),
          getQueryByExampleStreamMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 心跳
     * </pre>
     */
    public io.holoinsight.server.meta.proto.data.DataHello heartBeat(
        io.holoinsight.server.meta.proto.data.DataHello request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getHeartBeatMethod(),
          getCallOptions(), request);
    }
  }

  /**
   * <pre>
   * 定义Query相关接口
   * </pre>
   */
  public static final class DataServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<DataServiceFutureStub> {
    private DataServiceFutureStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DataServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DataServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.meta.proto.data.DataBaseResponse> insertOrUpdate(
        io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getInsertOrUpdateMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.meta.proto.data.DataBaseResponse> insert(
        io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest request) {
      return io.grpc.stub.ClientCalls
          .futureUnaryCall(getChannel().newCall(getInsertMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.meta.proto.data.DataBaseResponse> update(
        io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest request) {
      return io.grpc.stub.ClientCalls
          .futureUnaryCall(getChannel().newCall(getUpdateMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.meta.proto.data.QueryDataResponse> queryDataByTable(
        io.holoinsight.server.meta.proto.data.QueryDataByTableRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getQueryDataByTableMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.meta.proto.data.QueryOneDataResponse> queryDataByPk(
        io.holoinsight.server.meta.proto.data.QueryDataByPkRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getQueryDataByPkMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.meta.proto.data.QueryDataResponse> queryDataByPks(
        io.holoinsight.server.meta.proto.data.QueryDataByPksRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getQueryDataByPksMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.meta.proto.data.DataBaseResponse> batchDeleteByPk(
        io.holoinsight.server.meta.proto.data.BatchDeleteByPkRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getBatchDeleteByPkMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.meta.proto.data.DataBaseResponse> deleteByExample(
        io.holoinsight.server.meta.proto.data.DeleteDataByExampleRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDeleteByExampleMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.meta.proto.data.DataBaseResponse> deleteByRowMap(
        io.holoinsight.server.meta.proto.data.DeleteDataByExampleRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDeleteByRowMapMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.meta.proto.data.DataBaseResponse> updateByExample(
        io.holoinsight.server.meta.proto.data.UpdateDataByExampleRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getUpdateByExampleMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.meta.proto.data.QueryDataResponse> queryByExample(
        io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getQueryByExampleMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.meta.proto.data.QueryDataResponse> fuzzyByExample(
        io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getFuzzyByExampleMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * 心跳
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.meta.proto.data.DataHello> heartBeat(
        io.holoinsight.server.meta.proto.data.DataHello request) {
      return io.grpc.stub.ClientCalls
          .futureUnaryCall(getChannel().newCall(getHeartBeatMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_INSERT_OR_UPDATE = 0;
  private static final int METHODID_INSERT = 1;
  private static final int METHODID_UPDATE = 2;
  private static final int METHODID_QUERY_DATA_BY_TABLE = 3;
  private static final int METHODID_QUERY_DATA_BY_PK = 4;
  private static final int METHODID_QUERY_DATA_BY_PKS = 5;
  private static final int METHODID_QUERY_DATA_BY_TABLE_STREAM = 6;
  private static final int METHODID_QUERY_DATA_BY_TABLE_ROWS_STREAM = 7;
  private static final int METHODID_BATCH_DELETE_BY_PK = 8;
  private static final int METHODID_DELETE_BY_EXAMPLE = 9;
  private static final int METHODID_DELETE_BY_ROW_MAP = 10;
  private static final int METHODID_UPDATE_BY_EXAMPLE = 11;
  private static final int METHODID_QUERY_BY_EXAMPLE = 12;
  private static final int METHODID_FUZZY_BY_EXAMPLE = 13;
  private static final int METHODID_QUERY_BY_EXAMPLE_STREAM = 14;
  private static final int METHODID_HEART_BEAT = 15;

  private static final class MethodHandlers<Req, Resp>
      implements io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final DataServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(DataServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_INSERT_OR_UPDATE:
          serviceImpl.insertOrUpdate(
              (io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.DataBaseResponse>) responseObserver);
          break;
        case METHODID_INSERT:
          serviceImpl.insert((io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.DataBaseResponse>) responseObserver);
          break;
        case METHODID_UPDATE:
          serviceImpl.update((io.holoinsight.server.meta.proto.data.InsertOrUpdateRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.DataBaseResponse>) responseObserver);
          break;
        case METHODID_QUERY_DATA_BY_TABLE:
          serviceImpl.queryDataByTable(
              (io.holoinsight.server.meta.proto.data.QueryDataByTableRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.QueryDataResponse>) responseObserver);
          break;
        case METHODID_QUERY_DATA_BY_PK:
          serviceImpl.queryDataByPk(
              (io.holoinsight.server.meta.proto.data.QueryDataByPkRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.QueryOneDataResponse>) responseObserver);
          break;
        case METHODID_QUERY_DATA_BY_PKS:
          serviceImpl.queryDataByPks(
              (io.holoinsight.server.meta.proto.data.QueryDataByPksRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.QueryDataResponse>) responseObserver);
          break;
        case METHODID_QUERY_DATA_BY_TABLE_STREAM:
          serviceImpl.queryDataByTableStream(
              (io.holoinsight.server.meta.proto.data.QueryDataByTableRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.QueryDataResponse>) responseObserver);
          break;
        case METHODID_QUERY_DATA_BY_TABLE_ROWS_STREAM:
          serviceImpl.queryDataByTableRowsStream(
              (io.holoinsight.server.meta.proto.data.QueryDataByTableRowsRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.QueryDataResponse>) responseObserver);
          break;
        case METHODID_BATCH_DELETE_BY_PK:
          serviceImpl.batchDeleteByPk(
              (io.holoinsight.server.meta.proto.data.BatchDeleteByPkRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.DataBaseResponse>) responseObserver);
          break;
        case METHODID_DELETE_BY_EXAMPLE:
          serviceImpl.deleteByExample(
              (io.holoinsight.server.meta.proto.data.DeleteDataByExampleRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.DataBaseResponse>) responseObserver);
          break;
        case METHODID_DELETE_BY_ROW_MAP:
          serviceImpl.deleteByRowMap(
              (io.holoinsight.server.meta.proto.data.DeleteDataByExampleRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.DataBaseResponse>) responseObserver);
          break;
        case METHODID_UPDATE_BY_EXAMPLE:
          serviceImpl.updateByExample(
              (io.holoinsight.server.meta.proto.data.UpdateDataByExampleRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.DataBaseResponse>) responseObserver);
          break;
        case METHODID_QUERY_BY_EXAMPLE:
          serviceImpl.queryByExample(
              (io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.QueryDataResponse>) responseObserver);
          break;
        case METHODID_FUZZY_BY_EXAMPLE:
          serviceImpl.fuzzyByExample(
              (io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.QueryDataResponse>) responseObserver);
          break;
        case METHODID_QUERY_BY_EXAMPLE_STREAM:
          serviceImpl.queryByExampleStream(
              (io.holoinsight.server.meta.proto.data.QueryDataByExampleRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.QueryDataResponse>) responseObserver);
          break;
        case METHODID_HEART_BEAT:
          serviceImpl.heartBeat((io.holoinsight.server.meta.proto.data.DataHello) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.meta.proto.data.DataHello>) responseObserver);
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

  private static abstract class DataServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier,
      io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    DataServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return io.holoinsight.server.meta.proto.data.DataServiceProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("DataService");
    }
  }

  private static final class DataServiceFileDescriptorSupplier
      extends DataServiceBaseDescriptorSupplier {
    DataServiceFileDescriptorSupplier() {}
  }

  private static final class DataServiceMethodDescriptorSupplier extends
      DataServiceBaseDescriptorSupplier implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    DataServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (DataServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new DataServiceFileDescriptorSupplier())
              .addMethod(getInsertOrUpdateMethod()).addMethod(getInsertMethod())
              .addMethod(getUpdateMethod()).addMethod(getQueryDataByTableMethod())
              .addMethod(getQueryDataByPkMethod()).addMethod(getQueryDataByPksMethod())
              .addMethod(getQueryDataByTableStreamMethod())
              .addMethod(getQueryDataByTableRowsStreamMethod())
              .addMethod(getBatchDeleteByPkMethod()).addMethod(getDeleteByExampleMethod())
              .addMethod(getDeleteByRowMapMethod()).addMethod(getUpdateByExampleMethod())
              .addMethod(getQueryByExampleMethod()).addMethod(getFuzzyByExampleMethod())
              .addMethod(getQueryByExampleStreamMethod()).addMethod(getHeartBeatMethod()).build();
        }
      }
    }
    return result;
  }
}
