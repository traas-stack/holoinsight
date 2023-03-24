package io.holoinsight.server.query.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(value = "by gRPC proto compiler (version 1.42.2)",
    comments = "Source: query_proto.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class QueryServiceGrpc {

  private QueryServiceGrpc() {}

  public static final String SERVICE_NAME = "io.holoinsight.server.query.grpc.QueryService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryRequest, io.holoinsight.server.query.grpc.QueryProto.QueryResponse> getQueryDataMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "QueryData",
      requestType = io.holoinsight.server.query.grpc.QueryProto.QueryRequest.class,
      responseType = io.holoinsight.server.query.grpc.QueryProto.QueryResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryRequest, io.holoinsight.server.query.grpc.QueryProto.QueryResponse> getQueryDataMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryRequest, io.holoinsight.server.query.grpc.QueryProto.QueryResponse> getQueryDataMethod;
    if ((getQueryDataMethod = QueryServiceGrpc.getQueryDataMethod) == null) {
      synchronized (QueryServiceGrpc.class) {
        if ((getQueryDataMethod = QueryServiceGrpc.getQueryDataMethod) == null) {
          QueryServiceGrpc.getQueryDataMethod = getQueryDataMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.query.grpc.QueryProto.QueryRequest, io.holoinsight.server.query.grpc.QueryProto.QueryResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "QueryData"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.query.grpc.QueryProto.QueryRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.query.grpc.QueryProto.QueryResponse
                          .getDefaultInstance()))
                  .setSchemaDescriptor(new QueryServiceMethodDescriptorSupplier("QueryData"))
                  .build();
        }
      }
    }
    return getQueryDataMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryRequest, io.holoinsight.server.query.grpc.QueryProto.QueryResponse> getQueryTagsMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "QueryTags",
      requestType = io.holoinsight.server.query.grpc.QueryProto.QueryRequest.class,
      responseType = io.holoinsight.server.query.grpc.QueryProto.QueryResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryRequest, io.holoinsight.server.query.grpc.QueryProto.QueryResponse> getQueryTagsMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryRequest, io.holoinsight.server.query.grpc.QueryProto.QueryResponse> getQueryTagsMethod;
    if ((getQueryTagsMethod = QueryServiceGrpc.getQueryTagsMethod) == null) {
      synchronized (QueryServiceGrpc.class) {
        if ((getQueryTagsMethod = QueryServiceGrpc.getQueryTagsMethod) == null) {
          QueryServiceGrpc.getQueryTagsMethod = getQueryTagsMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.query.grpc.QueryProto.QueryRequest, io.holoinsight.server.query.grpc.QueryProto.QueryResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "QueryTags"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.query.grpc.QueryProto.QueryRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.query.grpc.QueryProto.QueryResponse
                          .getDefaultInstance()))
                  .setSchemaDescriptor(new QueryServiceMethodDescriptorSupplier("QueryTags"))
                  .build();
        }
      }
    }
    return getQueryTagsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryRequest, io.holoinsight.server.query.grpc.QueryProto.QuerySchemaResponse> getQuerySchemaMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "QuerySchema",
      requestType = io.holoinsight.server.query.grpc.QueryProto.QueryRequest.class,
      responseType = io.holoinsight.server.query.grpc.QueryProto.QuerySchemaResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryRequest, io.holoinsight.server.query.grpc.QueryProto.QuerySchemaResponse> getQuerySchemaMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryRequest, io.holoinsight.server.query.grpc.QueryProto.QuerySchemaResponse> getQuerySchemaMethod;
    if ((getQuerySchemaMethod = QueryServiceGrpc.getQuerySchemaMethod) == null) {
      synchronized (QueryServiceGrpc.class) {
        if ((getQuerySchemaMethod = QueryServiceGrpc.getQuerySchemaMethod) == null) {
          QueryServiceGrpc.getQuerySchemaMethod = getQuerySchemaMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.query.grpc.QueryProto.QueryRequest, io.holoinsight.server.query.grpc.QueryProto.QuerySchemaResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "QuerySchema"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.query.grpc.QueryProto.QueryRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.query.grpc.QueryProto.QuerySchemaResponse
                          .getDefaultInstance()))
                  .setSchemaDescriptor(new QueryServiceMethodDescriptorSupplier("QuerySchema"))
                  .build();
        }
      }
    }
    return getQuerySchemaMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryMetricsRequest, io.holoinsight.server.query.grpc.QueryProto.QueryMetricsResponse> getQueryMetricsMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "QueryMetrics",
      requestType = io.holoinsight.server.query.grpc.QueryProto.QueryMetricsRequest.class,
      responseType = io.holoinsight.server.query.grpc.QueryProto.QueryMetricsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryMetricsRequest, io.holoinsight.server.query.grpc.QueryProto.QueryMetricsResponse> getQueryMetricsMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryMetricsRequest, io.holoinsight.server.query.grpc.QueryProto.QueryMetricsResponse> getQueryMetricsMethod;
    if ((getQueryMetricsMethod = QueryServiceGrpc.getQueryMetricsMethod) == null) {
      synchronized (QueryServiceGrpc.class) {
        if ((getQueryMetricsMethod = QueryServiceGrpc.getQueryMetricsMethod) == null) {
          QueryServiceGrpc.getQueryMetricsMethod = getQueryMetricsMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.query.grpc.QueryProto.QueryMetricsRequest, io.holoinsight.server.query.grpc.QueryProto.QueryMetricsResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "QueryMetrics"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.query.grpc.QueryProto.QueryMetricsRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.query.grpc.QueryProto.QueryMetricsResponse
                          .getDefaultInstance()))
                  .setSchemaDescriptor(new QueryServiceMethodDescriptorSupplier("QueryMetrics"))
                  .build();
        }
      }
    }
    return getQueryMetricsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryRequest, io.holoinsight.server.query.grpc.QueryProto.QueryResponse> getDeleteKeysMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "DeleteKeys",
      requestType = io.holoinsight.server.query.grpc.QueryProto.QueryRequest.class,
      responseType = io.holoinsight.server.query.grpc.QueryProto.QueryResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryRequest, io.holoinsight.server.query.grpc.QueryProto.QueryResponse> getDeleteKeysMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryRequest, io.holoinsight.server.query.grpc.QueryProto.QueryResponse> getDeleteKeysMethod;
    if ((getDeleteKeysMethod = QueryServiceGrpc.getDeleteKeysMethod) == null) {
      synchronized (QueryServiceGrpc.class) {
        if ((getDeleteKeysMethod = QueryServiceGrpc.getDeleteKeysMethod) == null) {
          QueryServiceGrpc.getDeleteKeysMethod = getDeleteKeysMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.query.grpc.QueryProto.QueryRequest, io.holoinsight.server.query.grpc.QueryProto.QueryResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DeleteKeys"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.query.grpc.QueryProto.QueryRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.query.grpc.QueryProto.QueryResponse
                          .getDefaultInstance()))
                  .setSchemaDescriptor(new QueryServiceMethodDescriptorSupplier("DeleteKeys"))
                  .build();
        }
      }
    }
    return getDeleteKeysMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.PqlInstantRequest, io.holoinsight.server.query.grpc.QueryProto.QueryResponse> getPqlInstantQueryMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "pqlInstantQuery",
      requestType = io.holoinsight.server.query.grpc.QueryProto.PqlInstantRequest.class,
      responseType = io.holoinsight.server.query.grpc.QueryProto.QueryResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.PqlInstantRequest, io.holoinsight.server.query.grpc.QueryProto.QueryResponse> getPqlInstantQueryMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.PqlInstantRequest, io.holoinsight.server.query.grpc.QueryProto.QueryResponse> getPqlInstantQueryMethod;
    if ((getPqlInstantQueryMethod = QueryServiceGrpc.getPqlInstantQueryMethod) == null) {
      synchronized (QueryServiceGrpc.class) {
        if ((getPqlInstantQueryMethod = QueryServiceGrpc.getPqlInstantQueryMethod) == null) {
          QueryServiceGrpc.getPqlInstantQueryMethod = getPqlInstantQueryMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.query.grpc.QueryProto.PqlInstantRequest, io.holoinsight.server.query.grpc.QueryProto.QueryResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "pqlInstantQuery"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.query.grpc.QueryProto.PqlInstantRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.query.grpc.QueryProto.QueryResponse
                          .getDefaultInstance()))
                  .setSchemaDescriptor(new QueryServiceMethodDescriptorSupplier("pqlInstantQuery"))
                  .build();
        }
      }
    }
    return getPqlInstantQueryMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.PqlRangeRequest, io.holoinsight.server.query.grpc.QueryProto.QueryResponse> getPqlRangeQueryMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "pqlRangeQuery",
      requestType = io.holoinsight.server.query.grpc.QueryProto.PqlRangeRequest.class,
      responseType = io.holoinsight.server.query.grpc.QueryProto.QueryResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.PqlRangeRequest, io.holoinsight.server.query.grpc.QueryProto.QueryResponse> getPqlRangeQueryMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.PqlRangeRequest, io.holoinsight.server.query.grpc.QueryProto.QueryResponse> getPqlRangeQueryMethod;
    if ((getPqlRangeQueryMethod = QueryServiceGrpc.getPqlRangeQueryMethod) == null) {
      synchronized (QueryServiceGrpc.class) {
        if ((getPqlRangeQueryMethod = QueryServiceGrpc.getPqlRangeQueryMethod) == null) {
          QueryServiceGrpc.getPqlRangeQueryMethod = getPqlRangeQueryMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.query.grpc.QueryProto.PqlRangeRequest, io.holoinsight.server.query.grpc.QueryProto.QueryResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "pqlRangeQuery"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.query.grpc.QueryProto.PqlRangeRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.query.grpc.QueryProto.QueryResponse
                          .getDefaultInstance()))
                  .setSchemaDescriptor(new QueryServiceMethodDescriptorSupplier("pqlRangeQuery"))
                  .build();
        }
      }
    }
    return getPqlRangeQueryMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryTraceRequest, io.holoinsight.server.query.grpc.QueryProto.TraceBrief> getQueryBasicTracesMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "QueryBasicTraces",
      requestType = io.holoinsight.server.query.grpc.QueryProto.QueryTraceRequest.class,
      responseType = io.holoinsight.server.query.grpc.QueryProto.TraceBrief.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryTraceRequest, io.holoinsight.server.query.grpc.QueryProto.TraceBrief> getQueryBasicTracesMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryTraceRequest, io.holoinsight.server.query.grpc.QueryProto.TraceBrief> getQueryBasicTracesMethod;
    if ((getQueryBasicTracesMethod = QueryServiceGrpc.getQueryBasicTracesMethod) == null) {
      synchronized (QueryServiceGrpc.class) {
        if ((getQueryBasicTracesMethod = QueryServiceGrpc.getQueryBasicTracesMethod) == null) {
          QueryServiceGrpc.getQueryBasicTracesMethod = getQueryBasicTracesMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.query.grpc.QueryProto.QueryTraceRequest, io.holoinsight.server.query.grpc.QueryProto.TraceBrief>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "QueryBasicTraces"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.query.grpc.QueryProto.QueryTraceRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.query.grpc.QueryProto.TraceBrief.getDefaultInstance()))
                  .setSchemaDescriptor(new QueryServiceMethodDescriptorSupplier("QueryBasicTraces"))
                  .build();
        }
      }
    }
    return getQueryBasicTracesMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryTraceRequest, io.holoinsight.server.query.grpc.QueryProto.Trace> getQueryTraceMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "QueryTrace",
      requestType = io.holoinsight.server.query.grpc.QueryProto.QueryTraceRequest.class,
      responseType = io.holoinsight.server.query.grpc.QueryProto.Trace.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryTraceRequest, io.holoinsight.server.query.grpc.QueryProto.Trace> getQueryTraceMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryTraceRequest, io.holoinsight.server.query.grpc.QueryProto.Trace> getQueryTraceMethod;
    if ((getQueryTraceMethod = QueryServiceGrpc.getQueryTraceMethod) == null) {
      synchronized (QueryServiceGrpc.class) {
        if ((getQueryTraceMethod = QueryServiceGrpc.getQueryTraceMethod) == null) {
          QueryServiceGrpc.getQueryTraceMethod = getQueryTraceMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.query.grpc.QueryProto.QueryTraceRequest, io.holoinsight.server.query.grpc.QueryProto.Trace>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "QueryTrace"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.query.grpc.QueryProto.QueryTraceRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.query.grpc.QueryProto.Trace.getDefaultInstance()))
                  .setSchemaDescriptor(new QueryServiceMethodDescriptorSupplier("QueryTrace"))
                  .build();
        }
      }
    }
    return getQueryTraceMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse> getQueryServiceListMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "QueryServiceList",
      requestType = io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest.class,
      responseType = io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse> getQueryServiceListMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse> getQueryServiceListMethod;
    if ((getQueryServiceListMethod = QueryServiceGrpc.getQueryServiceListMethod) == null) {
      synchronized (QueryServiceGrpc.class) {
        if ((getQueryServiceListMethod = QueryServiceGrpc.getQueryServiceListMethod) == null) {
          QueryServiceGrpc.getQueryServiceListMethod = getQueryServiceListMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "QueryServiceList"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse
                          .getDefaultInstance()))
                  .setSchemaDescriptor(new QueryServiceMethodDescriptorSupplier("QueryServiceList"))
                  .build();
        }
      }
    }
    return getQueryServiceListMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse> getQueryEndpointListMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "QueryEndpointList",
      requestType = io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest.class,
      responseType = io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse> getQueryEndpointListMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse> getQueryEndpointListMethod;
    if ((getQueryEndpointListMethod = QueryServiceGrpc.getQueryEndpointListMethod) == null) {
      synchronized (QueryServiceGrpc.class) {
        if ((getQueryEndpointListMethod = QueryServiceGrpc.getQueryEndpointListMethod) == null) {
          QueryServiceGrpc.getQueryEndpointListMethod = getQueryEndpointListMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "QueryEndpointList"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse
                          .getDefaultInstance()))
                  .setSchemaDescriptor(
                      new QueryServiceMethodDescriptorSupplier("QueryEndpointList"))
                  .build();
        }
      }
    }
    return getQueryEndpointListMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse> getQueryServiceInstanceListMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "QueryServiceInstanceList",
      requestType = io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest.class,
      responseType = io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse> getQueryServiceInstanceListMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse> getQueryServiceInstanceListMethod;
    if ((getQueryServiceInstanceListMethod =
        QueryServiceGrpc.getQueryServiceInstanceListMethod) == null) {
      synchronized (QueryServiceGrpc.class) {
        if ((getQueryServiceInstanceListMethod =
            QueryServiceGrpc.getQueryServiceInstanceListMethod) == null) {
          QueryServiceGrpc.getQueryServiceInstanceListMethod = getQueryServiceInstanceListMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(
                      generateFullMethodName(SERVICE_NAME, "QueryServiceInstanceList"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse
                          .getDefaultInstance()))
                  .setSchemaDescriptor(
                      new QueryServiceMethodDescriptorSupplier("QueryServiceInstanceList"))
                  .build();
        }
      }
    }
    return getQueryServiceInstanceListMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.QueryVirtualComponentResponse> getQueryComponentListMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "QueryComponentList",
      requestType = io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest.class,
      responseType = io.holoinsight.server.query.grpc.QueryProto.QueryVirtualComponentResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.QueryVirtualComponentResponse> getQueryComponentListMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.QueryVirtualComponentResponse> getQueryComponentListMethod;
    if ((getQueryComponentListMethod = QueryServiceGrpc.getQueryComponentListMethod) == null) {
      synchronized (QueryServiceGrpc.class) {
        if ((getQueryComponentListMethod = QueryServiceGrpc.getQueryComponentListMethod) == null) {
          QueryServiceGrpc.getQueryComponentListMethod = getQueryComponentListMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.QueryVirtualComponentResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "QueryComponentList"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.query.grpc.QueryProto.QueryVirtualComponentResponse
                          .getDefaultInstance()))
                  .setSchemaDescriptor(
                      new QueryServiceMethodDescriptorSupplier("QueryComponentList"))
                  .build();
        }
      }
    }
    return getQueryComponentListMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.TraceIds> getQueryComponentTraceIdsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "QueryComponentTraceIds",
      requestType = io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest.class,
      responseType = io.holoinsight.server.query.grpc.QueryProto.TraceIds.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.TraceIds> getQueryComponentTraceIdsMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.TraceIds> getQueryComponentTraceIdsMethod;
    if ((getQueryComponentTraceIdsMethod =
        QueryServiceGrpc.getQueryComponentTraceIdsMethod) == null) {
      synchronized (QueryServiceGrpc.class) {
        if ((getQueryComponentTraceIdsMethod =
            QueryServiceGrpc.getQueryComponentTraceIdsMethod) == null) {
          QueryServiceGrpc.getQueryComponentTraceIdsMethod = getQueryComponentTraceIdsMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.TraceIds>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "QueryComponentTraceIds"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.query.grpc.QueryProto.TraceIds.getDefaultInstance()))
                  .setSchemaDescriptor(
                      new QueryServiceMethodDescriptorSupplier("QueryComponentTraceIds"))
                  .build();
        }
      }
    }
    return getQueryComponentTraceIdsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryTopologyRequest, io.holoinsight.server.query.grpc.QueryProto.Topology> getQueryTopologyMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "QueryTopology",
      requestType = io.holoinsight.server.query.grpc.QueryProto.QueryTopologyRequest.class,
      responseType = io.holoinsight.server.query.grpc.QueryProto.Topology.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryTopologyRequest, io.holoinsight.server.query.grpc.QueryProto.Topology> getQueryTopologyMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryTopologyRequest, io.holoinsight.server.query.grpc.QueryProto.Topology> getQueryTopologyMethod;
    if ((getQueryTopologyMethod = QueryServiceGrpc.getQueryTopologyMethod) == null) {
      synchronized (QueryServiceGrpc.class) {
        if ((getQueryTopologyMethod = QueryServiceGrpc.getQueryTopologyMethod) == null) {
          QueryServiceGrpc.getQueryTopologyMethod = getQueryTopologyMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.query.grpc.QueryProto.QueryTopologyRequest, io.holoinsight.server.query.grpc.QueryProto.Topology>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "QueryTopology"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.query.grpc.QueryProto.QueryTopologyRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.query.grpc.QueryProto.Topology.getDefaultInstance()))
                  .setSchemaDescriptor(new QueryServiceMethodDescriptorSupplier("QueryTopology"))
                  .build();
        }
      }
    }
    return getQueryTopologyMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.BizopsEndpoints> getQueryBizEndpointListMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "QueryBizEndpointList",
      requestType = io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest.class,
      responseType = io.holoinsight.server.query.grpc.QueryProto.BizopsEndpoints.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.BizopsEndpoints> getQueryBizEndpointListMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.BizopsEndpoints> getQueryBizEndpointListMethod;
    if ((getQueryBizEndpointListMethod = QueryServiceGrpc.getQueryBizEndpointListMethod) == null) {
      synchronized (QueryServiceGrpc.class) {
        if ((getQueryBizEndpointListMethod =
            QueryServiceGrpc.getQueryBizEndpointListMethod) == null) {
          QueryServiceGrpc.getQueryBizEndpointListMethod = getQueryBizEndpointListMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.BizopsEndpoints>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "QueryBizEndpointList"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.query.grpc.QueryProto.BizopsEndpoints
                          .getDefaultInstance()))
                  .setSchemaDescriptor(
                      new QueryServiceMethodDescriptorSupplier("QueryBizEndpointList"))
                  .build();
        }
      }
    }
    return getQueryBizEndpointListMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.BizopsEndpoints> getQueryBizErrorCodeListMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "QueryBizErrorCodeList",
      requestType = io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest.class,
      responseType = io.holoinsight.server.query.grpc.QueryProto.BizopsEndpoints.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.BizopsEndpoints> getQueryBizErrorCodeListMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.BizopsEndpoints> getQueryBizErrorCodeListMethod;
    if ((getQueryBizErrorCodeListMethod =
        QueryServiceGrpc.getQueryBizErrorCodeListMethod) == null) {
      synchronized (QueryServiceGrpc.class) {
        if ((getQueryBizErrorCodeListMethod =
            QueryServiceGrpc.getQueryBizErrorCodeListMethod) == null) {
          QueryServiceGrpc.getQueryBizErrorCodeListMethod = getQueryBizErrorCodeListMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.BizopsEndpoints>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "QueryBizErrorCodeList"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.query.grpc.QueryProto.BizopsEndpoints
                          .getDefaultInstance()))
                  .setSchemaDescriptor(
                      new QueryServiceMethodDescriptorSupplier("QueryBizErrorCodeList"))
                  .build();
        }
      }
    }
    return getQueryBizErrorCodeListMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.QuerySlowSqlResponse> getQuerySlowSqlListMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "QuerySlowSqlList",
      requestType = io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest.class,
      responseType = io.holoinsight.server.query.grpc.QueryProto.QuerySlowSqlResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.QuerySlowSqlResponse> getQuerySlowSqlListMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.QuerySlowSqlResponse> getQuerySlowSqlListMethod;
    if ((getQuerySlowSqlListMethod = QueryServiceGrpc.getQuerySlowSqlListMethod) == null) {
      synchronized (QueryServiceGrpc.class) {
        if ((getQuerySlowSqlListMethod = QueryServiceGrpc.getQuerySlowSqlListMethod) == null) {
          QueryServiceGrpc.getQuerySlowSqlListMethod = getQuerySlowSqlListMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.QuerySlowSqlResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "QuerySlowSqlList"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.query.grpc.QueryProto.QuerySlowSqlResponse
                          .getDefaultInstance()))
                  .setSchemaDescriptor(new QueryServiceMethodDescriptorSupplier("QuerySlowSqlList"))
                  .build();
        }
      }
    }
    return getQuerySlowSqlListMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static QueryServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<QueryServiceStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<QueryServiceStub>() {
          @java.lang.Override
          public QueryServiceStub newStub(io.grpc.Channel channel,
              io.grpc.CallOptions callOptions) {
            return new QueryServiceStub(channel, callOptions);
          }
        };
    return QueryServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static QueryServiceBlockingStub newBlockingStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<QueryServiceBlockingStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<QueryServiceBlockingStub>() {
          @java.lang.Override
          public QueryServiceBlockingStub newStub(io.grpc.Channel channel,
              io.grpc.CallOptions callOptions) {
            return new QueryServiceBlockingStub(channel, callOptions);
          }
        };
    return QueryServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static QueryServiceFutureStub newFutureStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<QueryServiceFutureStub> factory =
        new io.grpc.stub.AbstractStub.StubFactory<QueryServiceFutureStub>() {
          @java.lang.Override
          public QueryServiceFutureStub newStub(io.grpc.Channel channel,
              io.grpc.CallOptions callOptions) {
            return new QueryServiceFutureStub(channel, callOptions);
          }
        };
    return QueryServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class QueryServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void queryData(io.holoinsight.server.query.grpc.QueryProto.QueryRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QueryResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getQueryDataMethod(), responseObserver);
    }

    /**
     */
    public void queryTags(io.holoinsight.server.query.grpc.QueryProto.QueryRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QueryResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getQueryTagsMethod(), responseObserver);
    }

    /**
     */
    public void querySchema(io.holoinsight.server.query.grpc.QueryProto.QueryRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QuerySchemaResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getQuerySchemaMethod(),
          responseObserver);
    }

    /**
     */
    public void queryMetrics(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetricsRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QueryMetricsResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getQueryMetricsMethod(),
          responseObserver);
    }

    /**
     */
    public void deleteKeys(io.holoinsight.server.query.grpc.QueryProto.QueryRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QueryResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDeleteKeysMethod(), responseObserver);
    }

    /**
     */
    public void pqlInstantQuery(
        io.holoinsight.server.query.grpc.QueryProto.PqlInstantRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QueryResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getPqlInstantQueryMethod(),
          responseObserver);
    }

    /**
     */
    public void pqlRangeQuery(io.holoinsight.server.query.grpc.QueryProto.PqlRangeRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QueryResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getPqlRangeQueryMethod(),
          responseObserver);
    }

    /**
     */
    public void queryBasicTraces(
        io.holoinsight.server.query.grpc.QueryProto.QueryTraceRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.TraceBrief> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getQueryBasicTracesMethod(),
          responseObserver);
    }

    /**
     */
    public void queryTrace(io.holoinsight.server.query.grpc.QueryProto.QueryTraceRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.Trace> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getQueryTraceMethod(), responseObserver);
    }

    /**
     */
    public void queryServiceList(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getQueryServiceListMethod(),
          responseObserver);
    }

    /**
     */
    public void queryEndpointList(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getQueryEndpointListMethod(),
          responseObserver);
    }

    /**
     */
    public void queryServiceInstanceList(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getQueryServiceInstanceListMethod(),
          responseObserver);
    }

    /**
     */
    public void queryComponentList(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QueryVirtualComponentResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getQueryComponentListMethod(),
          responseObserver);
    }

    /**
     */
    public void queryComponentTraceIds(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.TraceIds> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getQueryComponentTraceIdsMethod(),
          responseObserver);
    }

    /**
     */
    public void queryTopology(
        io.holoinsight.server.query.grpc.QueryProto.QueryTopologyRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.Topology> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getQueryTopologyMethod(),
          responseObserver);
    }

    /**
     */
    public void queryBizEndpointList(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.BizopsEndpoints> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getQueryBizEndpointListMethod(),
          responseObserver);
    }

    /**
     */
    public void queryBizErrorCodeList(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.BizopsEndpoints> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getQueryBizErrorCodeListMethod(),
          responseObserver);
    }

    /**
     */
    public void querySlowSqlList(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QuerySlowSqlResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getQuerySlowSqlListMethod(),
          responseObserver);
    }

    @java.lang.Override
    public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(getQueryDataMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.query.grpc.QueryProto.QueryRequest, io.holoinsight.server.query.grpc.QueryProto.QueryResponse>(
                  this, METHODID_QUERY_DATA)))
          .addMethod(getQueryTagsMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.query.grpc.QueryProto.QueryRequest, io.holoinsight.server.query.grpc.QueryProto.QueryResponse>(
                  this, METHODID_QUERY_TAGS)))
          .addMethod(getQuerySchemaMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.query.grpc.QueryProto.QueryRequest, io.holoinsight.server.query.grpc.QueryProto.QuerySchemaResponse>(
                  this, METHODID_QUERY_SCHEMA)))
          .addMethod(getQueryMetricsMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.query.grpc.QueryProto.QueryMetricsRequest, io.holoinsight.server.query.grpc.QueryProto.QueryMetricsResponse>(
                  this, METHODID_QUERY_METRICS)))
          .addMethod(getDeleteKeysMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.query.grpc.QueryProto.QueryRequest, io.holoinsight.server.query.grpc.QueryProto.QueryResponse>(
                  this, METHODID_DELETE_KEYS)))
          .addMethod(getPqlInstantQueryMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.query.grpc.QueryProto.PqlInstantRequest, io.holoinsight.server.query.grpc.QueryProto.QueryResponse>(
                  this, METHODID_PQL_INSTANT_QUERY)))
          .addMethod(getPqlRangeQueryMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.query.grpc.QueryProto.PqlRangeRequest, io.holoinsight.server.query.grpc.QueryProto.QueryResponse>(
                  this, METHODID_PQL_RANGE_QUERY)))
          .addMethod(getQueryBasicTracesMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.query.grpc.QueryProto.QueryTraceRequest, io.holoinsight.server.query.grpc.QueryProto.TraceBrief>(
                  this, METHODID_QUERY_BASIC_TRACES)))
          .addMethod(getQueryTraceMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.query.grpc.QueryProto.QueryTraceRequest, io.holoinsight.server.query.grpc.QueryProto.Trace>(
                  this, METHODID_QUERY_TRACE)))
          .addMethod(getQueryServiceListMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse>(
                  this, METHODID_QUERY_SERVICE_LIST)))
          .addMethod(getQueryEndpointListMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse>(
                  this, METHODID_QUERY_ENDPOINT_LIST)))
          .addMethod(getQueryServiceInstanceListMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse>(
                  this, METHODID_QUERY_SERVICE_INSTANCE_LIST)))
          .addMethod(getQueryComponentListMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.QueryVirtualComponentResponse>(
                  this, METHODID_QUERY_COMPONENT_LIST)))
          .addMethod(getQueryComponentTraceIdsMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.TraceIds>(
                  this, METHODID_QUERY_COMPONENT_TRACE_IDS)))
          .addMethod(getQueryTopologyMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.query.grpc.QueryProto.QueryTopologyRequest, io.holoinsight.server.query.grpc.QueryProto.Topology>(
                  this, METHODID_QUERY_TOPOLOGY)))
          .addMethod(getQueryBizEndpointListMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.BizopsEndpoints>(
                  this, METHODID_QUERY_BIZ_ENDPOINT_LIST)))
          .addMethod(getQueryBizErrorCodeListMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.BizopsEndpoints>(
                  this, METHODID_QUERY_BIZ_ERROR_CODE_LIST)))
          .addMethod(getQuerySlowSqlListMethod(), io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest, io.holoinsight.server.query.grpc.QueryProto.QuerySlowSqlResponse>(
                  this, METHODID_QUERY_SLOW_SQL_LIST)))
          .build();
    }
  }

  /**
   */
  public static final class QueryServiceStub
      extends io.grpc.stub.AbstractAsyncStub<QueryServiceStub> {
    private QueryServiceStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected QueryServiceStub build(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new QueryServiceStub(channel, callOptions);
    }

    /**
     */
    public void queryData(io.holoinsight.server.query.grpc.QueryProto.QueryRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QueryResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getQueryDataMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void queryTags(io.holoinsight.server.query.grpc.QueryProto.QueryRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QueryResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getQueryTagsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void querySchema(io.holoinsight.server.query.grpc.QueryProto.QueryRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QuerySchemaResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getQuerySchemaMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void queryMetrics(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetricsRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QueryMetricsResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getQueryMetricsMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void deleteKeys(io.holoinsight.server.query.grpc.QueryProto.QueryRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QueryResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDeleteKeysMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void pqlInstantQuery(
        io.holoinsight.server.query.grpc.QueryProto.PqlInstantRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QueryResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getPqlInstantQueryMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void pqlRangeQuery(io.holoinsight.server.query.grpc.QueryProto.PqlRangeRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QueryResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getPqlRangeQueryMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void queryBasicTraces(
        io.holoinsight.server.query.grpc.QueryProto.QueryTraceRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.TraceBrief> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getQueryBasicTracesMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void queryTrace(io.holoinsight.server.query.grpc.QueryProto.QueryTraceRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.Trace> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getQueryTraceMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void queryServiceList(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getQueryServiceListMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void queryEndpointList(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getQueryEndpointListMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void queryServiceInstanceList(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getQueryServiceInstanceListMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void queryComponentList(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QueryVirtualComponentResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getQueryComponentListMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void queryComponentTraceIds(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.TraceIds> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getQueryComponentTraceIdsMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void queryTopology(
        io.holoinsight.server.query.grpc.QueryProto.QueryTopologyRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.Topology> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getQueryTopologyMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void queryBizEndpointList(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.BizopsEndpoints> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getQueryBizEndpointListMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void queryBizErrorCodeList(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.BizopsEndpoints> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getQueryBizErrorCodeListMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void querySlowSqlList(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QuerySlowSqlResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getQuerySlowSqlListMethod(), getCallOptions()), request,
          responseObserver);
    }
  }

  /**
   */
  public static final class QueryServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<QueryServiceBlockingStub> {
    private QueryServiceBlockingStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected QueryServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new QueryServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public io.holoinsight.server.query.grpc.QueryProto.QueryResponse queryData(
        io.holoinsight.server.query.grpc.QueryProto.QueryRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getQueryDataMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.query.grpc.QueryProto.QueryResponse queryTags(
        io.holoinsight.server.query.grpc.QueryProto.QueryRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getQueryTagsMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.query.grpc.QueryProto.QuerySchemaResponse querySchema(
        io.holoinsight.server.query.grpc.QueryProto.QueryRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getQuerySchemaMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.query.grpc.QueryProto.QueryMetricsResponse queryMetrics(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetricsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getQueryMetricsMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.query.grpc.QueryProto.QueryResponse deleteKeys(
        io.holoinsight.server.query.grpc.QueryProto.QueryRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getDeleteKeysMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.query.grpc.QueryProto.QueryResponse pqlInstantQuery(
        io.holoinsight.server.query.grpc.QueryProto.PqlInstantRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getPqlInstantQueryMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.query.grpc.QueryProto.QueryResponse pqlRangeQuery(
        io.holoinsight.server.query.grpc.QueryProto.PqlRangeRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getPqlRangeQueryMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.query.grpc.QueryProto.TraceBrief queryBasicTraces(
        io.holoinsight.server.query.grpc.QueryProto.QueryTraceRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getQueryBasicTracesMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.query.grpc.QueryProto.Trace queryTrace(
        io.holoinsight.server.query.grpc.QueryProto.QueryTraceRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getQueryTraceMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse queryServiceList(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getQueryServiceListMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse queryEndpointList(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getQueryEndpointListMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse queryServiceInstanceList(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(),
          getQueryServiceInstanceListMethod(), getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.query.grpc.QueryProto.QueryVirtualComponentResponse queryComponentList(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getQueryComponentListMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.query.grpc.QueryProto.TraceIds queryComponentTraceIds(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(),
          getQueryComponentTraceIdsMethod(), getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.query.grpc.QueryProto.Topology queryTopology(
        io.holoinsight.server.query.grpc.QueryProto.QueryTopologyRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getQueryTopologyMethod(),
          getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.query.grpc.QueryProto.BizopsEndpoints queryBizEndpointList(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(),
          getQueryBizEndpointListMethod(), getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.query.grpc.QueryProto.BizopsEndpoints queryBizErrorCodeList(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(),
          getQueryBizErrorCodeListMethod(), getCallOptions(), request);
    }

    /**
     */
    public io.holoinsight.server.query.grpc.QueryProto.QuerySlowSqlResponse querySlowSqlList(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(getChannel(), getQuerySlowSqlListMethod(),
          getCallOptions(), request);
    }
  }

  /**
   */
  public static final class QueryServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<QueryServiceFutureStub> {
    private QueryServiceFutureStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected QueryServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new QueryServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.query.grpc.QueryProto.QueryResponse> queryData(
        io.holoinsight.server.query.grpc.QueryProto.QueryRequest request) {
      return io.grpc.stub.ClientCalls
          .futureUnaryCall(getChannel().newCall(getQueryDataMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.query.grpc.QueryProto.QueryResponse> queryTags(
        io.holoinsight.server.query.grpc.QueryProto.QueryRequest request) {
      return io.grpc.stub.ClientCalls
          .futureUnaryCall(getChannel().newCall(getQueryTagsMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.query.grpc.QueryProto.QuerySchemaResponse> querySchema(
        io.holoinsight.server.query.grpc.QueryProto.QueryRequest request) {
      return io.grpc.stub.ClientCalls
          .futureUnaryCall(getChannel().newCall(getQuerySchemaMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.query.grpc.QueryProto.QueryMetricsResponse> queryMetrics(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetricsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getQueryMetricsMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.query.grpc.QueryProto.QueryResponse> deleteKeys(
        io.holoinsight.server.query.grpc.QueryProto.QueryRequest request) {
      return io.grpc.stub.ClientCalls
          .futureUnaryCall(getChannel().newCall(getDeleteKeysMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.query.grpc.QueryProto.QueryResponse> pqlInstantQuery(
        io.holoinsight.server.query.grpc.QueryProto.PqlInstantRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getPqlInstantQueryMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.query.grpc.QueryProto.QueryResponse> pqlRangeQuery(
        io.holoinsight.server.query.grpc.QueryProto.PqlRangeRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getPqlRangeQueryMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.query.grpc.QueryProto.TraceBrief> queryBasicTraces(
        io.holoinsight.server.query.grpc.QueryProto.QueryTraceRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getQueryBasicTracesMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.query.grpc.QueryProto.Trace> queryTrace(
        io.holoinsight.server.query.grpc.QueryProto.QueryTraceRequest request) {
      return io.grpc.stub.ClientCalls
          .futureUnaryCall(getChannel().newCall(getQueryTraceMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse> queryServiceList(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getQueryServiceListMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse> queryEndpointList(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getQueryEndpointListMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse> queryServiceInstanceList(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getQueryServiceInstanceListMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.query.grpc.QueryProto.QueryVirtualComponentResponse> queryComponentList(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getQueryComponentListMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.query.grpc.QueryProto.TraceIds> queryComponentTraceIds(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getQueryComponentTraceIdsMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.query.grpc.QueryProto.Topology> queryTopology(
        io.holoinsight.server.query.grpc.QueryProto.QueryTopologyRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getQueryTopologyMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.query.grpc.QueryProto.BizopsEndpoints> queryBizEndpointList(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getQueryBizEndpointListMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.query.grpc.QueryProto.BizopsEndpoints> queryBizErrorCodeList(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getQueryBizErrorCodeListMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.query.grpc.QueryProto.QuerySlowSqlResponse> querySlowSqlList(
        io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getQuerySlowSqlListMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_QUERY_DATA = 0;
  private static final int METHODID_QUERY_TAGS = 1;
  private static final int METHODID_QUERY_SCHEMA = 2;
  private static final int METHODID_QUERY_METRICS = 3;
  private static final int METHODID_DELETE_KEYS = 4;
  private static final int METHODID_PQL_INSTANT_QUERY = 5;
  private static final int METHODID_PQL_RANGE_QUERY = 6;
  private static final int METHODID_QUERY_BASIC_TRACES = 7;
  private static final int METHODID_QUERY_TRACE = 8;
  private static final int METHODID_QUERY_SERVICE_LIST = 9;
  private static final int METHODID_QUERY_ENDPOINT_LIST = 10;
  private static final int METHODID_QUERY_SERVICE_INSTANCE_LIST = 11;
  private static final int METHODID_QUERY_COMPONENT_LIST = 12;
  private static final int METHODID_QUERY_COMPONENT_TRACE_IDS = 13;
  private static final int METHODID_QUERY_TOPOLOGY = 14;
  private static final int METHODID_QUERY_BIZ_ENDPOINT_LIST = 15;
  private static final int METHODID_QUERY_BIZ_ERROR_CODE_LIST = 16;
  private static final int METHODID_QUERY_SLOW_SQL_LIST = 17;

  private static final class MethodHandlers<Req, Resp>
      implements io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final QueryServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(QueryServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_QUERY_DATA:
          serviceImpl.queryData((io.holoinsight.server.query.grpc.QueryProto.QueryRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QueryResponse>) responseObserver);
          break;
        case METHODID_QUERY_TAGS:
          serviceImpl.queryTags((io.holoinsight.server.query.grpc.QueryProto.QueryRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QueryResponse>) responseObserver);
          break;
        case METHODID_QUERY_SCHEMA:
          serviceImpl.querySchema(
              (io.holoinsight.server.query.grpc.QueryProto.QueryRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QuerySchemaResponse>) responseObserver);
          break;
        case METHODID_QUERY_METRICS:
          serviceImpl.queryMetrics(
              (io.holoinsight.server.query.grpc.QueryProto.QueryMetricsRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QueryMetricsResponse>) responseObserver);
          break;
        case METHODID_DELETE_KEYS:
          serviceImpl.deleteKeys((io.holoinsight.server.query.grpc.QueryProto.QueryRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QueryResponse>) responseObserver);
          break;
        case METHODID_PQL_INSTANT_QUERY:
          serviceImpl.pqlInstantQuery(
              (io.holoinsight.server.query.grpc.QueryProto.PqlInstantRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QueryResponse>) responseObserver);
          break;
        case METHODID_PQL_RANGE_QUERY:
          serviceImpl.pqlRangeQuery(
              (io.holoinsight.server.query.grpc.QueryProto.PqlRangeRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QueryResponse>) responseObserver);
          break;
        case METHODID_QUERY_BASIC_TRACES:
          serviceImpl.queryBasicTraces(
              (io.holoinsight.server.query.grpc.QueryProto.QueryTraceRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.TraceBrief>) responseObserver);
          break;
        case METHODID_QUERY_TRACE:
          serviceImpl.queryTrace(
              (io.holoinsight.server.query.grpc.QueryProto.QueryTraceRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.Trace>) responseObserver);
          break;
        case METHODID_QUERY_SERVICE_LIST:
          serviceImpl.queryServiceList(
              (io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse>) responseObserver);
          break;
        case METHODID_QUERY_ENDPOINT_LIST:
          serviceImpl.queryEndpointList(
              (io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse>) responseObserver);
          break;
        case METHODID_QUERY_SERVICE_INSTANCE_LIST:
          serviceImpl.queryServiceInstanceList(
              (io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse>) responseObserver);
          break;
        case METHODID_QUERY_COMPONENT_LIST:
          serviceImpl.queryComponentList(
              (io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QueryVirtualComponentResponse>) responseObserver);
          break;
        case METHODID_QUERY_COMPONENT_TRACE_IDS:
          serviceImpl.queryComponentTraceIds(
              (io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.TraceIds>) responseObserver);
          break;
        case METHODID_QUERY_TOPOLOGY:
          serviceImpl.queryTopology(
              (io.holoinsight.server.query.grpc.QueryProto.QueryTopologyRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.Topology>) responseObserver);
          break;
        case METHODID_QUERY_BIZ_ENDPOINT_LIST:
          serviceImpl.queryBizEndpointList(
              (io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.BizopsEndpoints>) responseObserver);
          break;
        case METHODID_QUERY_BIZ_ERROR_CODE_LIST:
          serviceImpl.queryBizErrorCodeList(
              (io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.BizopsEndpoints>) responseObserver);
          break;
        case METHODID_QUERY_SLOW_SQL_LIST:
          serviceImpl.querySlowSqlList(
              (io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.query.grpc.QueryProto.QuerySlowSqlResponse>) responseObserver);
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

  private static abstract class QueryServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier,
      io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    QueryServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return io.holoinsight.server.query.grpc.QueryProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("QueryService");
    }
  }

  private static final class QueryServiceFileDescriptorSupplier
      extends QueryServiceBaseDescriptorSupplier {
    QueryServiceFileDescriptorSupplier() {}
  }

  private static final class QueryServiceMethodDescriptorSupplier extends
      QueryServiceBaseDescriptorSupplier implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    QueryServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (QueryServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new QueryServiceFileDescriptorSupplier())
              .addMethod(getQueryDataMethod()).addMethod(getQueryTagsMethod())
              .addMethod(getQuerySchemaMethod()).addMethod(getQueryMetricsMethod())
              .addMethod(getDeleteKeysMethod()).addMethod(getPqlInstantQueryMethod())
              .addMethod(getPqlRangeQueryMethod()).addMethod(getQueryBasicTracesMethod())
              .addMethod(getQueryTraceMethod()).addMethod(getQueryServiceListMethod())
              .addMethod(getQueryEndpointListMethod())
              .addMethod(getQueryServiceInstanceListMethod())
              .addMethod(getQueryComponentListMethod()).addMethod(getQueryComponentTraceIdsMethod())
              .addMethod(getQueryTopologyMethod()).addMethod(getQueryBizEndpointListMethod())
              .addMethod(getQueryBizErrorCodeListMethod()).addMethod(getQuerySlowSqlListMethod())
              .build();
        }
      }
    }
    return result;
  }
}
