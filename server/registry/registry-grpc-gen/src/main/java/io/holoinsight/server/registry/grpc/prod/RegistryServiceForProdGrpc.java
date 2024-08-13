/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.grpc.prod;

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
 * 提供给 prod 调用的 registry 服务
 * </pre>
 */
@javax.annotation.Generated(value = "by gRPC proto compiler (version 1.23.0)",
    comments = "Source: registry-for-prod.proto")
public final class RegistryServiceForProdGrpc {

  private RegistryServiceForProdGrpc() {}

  public static final String SERVICE_NAME =
      "io.holoinsight.server.registry.grpc.prod.RegistryServiceForProd";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.prod.NotifyCollectConfigUpdatedRequest, io.holoinsight.server.registry.grpc.prod.NotifyCollectConfigUpdatedResponse> getNotifyCollectConfigUpdateMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "notify_collect_config_update",
      requestType = io.holoinsight.server.registry.grpc.prod.NotifyCollectConfigUpdatedRequest.class,
      responseType = io.holoinsight.server.registry.grpc.prod.NotifyCollectConfigUpdatedResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.prod.NotifyCollectConfigUpdatedRequest, io.holoinsight.server.registry.grpc.prod.NotifyCollectConfigUpdatedResponse> getNotifyCollectConfigUpdateMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.prod.NotifyCollectConfigUpdatedRequest, io.holoinsight.server.registry.grpc.prod.NotifyCollectConfigUpdatedResponse> getNotifyCollectConfigUpdateMethod;
    if ((getNotifyCollectConfigUpdateMethod =
        RegistryServiceForProdGrpc.getNotifyCollectConfigUpdateMethod) == null) {
      synchronized (RegistryServiceForProdGrpc.class) {
        if ((getNotifyCollectConfigUpdateMethod =
            RegistryServiceForProdGrpc.getNotifyCollectConfigUpdateMethod) == null) {
          RegistryServiceForProdGrpc.getNotifyCollectConfigUpdateMethod =
              getNotifyCollectConfigUpdateMethod =
                  io.grpc.MethodDescriptor.<io.holoinsight.server.registry.grpc.prod.NotifyCollectConfigUpdatedRequest, io.holoinsight.server.registry.grpc.prod.NotifyCollectConfigUpdatedResponse>newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "notify_collect_config_update"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                          io.holoinsight.server.registry.grpc.prod.NotifyCollectConfigUpdatedRequest
                              .getDefaultInstance()))
                      .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                          io.holoinsight.server.registry.grpc.prod.NotifyCollectConfigUpdatedResponse
                              .getDefaultInstance()))
                      .setSchemaDescriptor(new RegistryServiceForProdMethodDescriptorSupplier(
                          "notify_collect_config_update"))
                      .build();
        }
      }
    }
    return getNotifyCollectConfigUpdateMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.prod.ListFilesRequest, io.holoinsight.server.registry.grpc.prod.ListFilesResponse> getListFilesMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "list_files",
      requestType = io.holoinsight.server.registry.grpc.prod.ListFilesRequest.class,
      responseType = io.holoinsight.server.registry.grpc.prod.ListFilesResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.prod.ListFilesRequest, io.holoinsight.server.registry.grpc.prod.ListFilesResponse> getListFilesMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.prod.ListFilesRequest, io.holoinsight.server.registry.grpc.prod.ListFilesResponse> getListFilesMethod;
    if ((getListFilesMethod = RegistryServiceForProdGrpc.getListFilesMethod) == null) {
      synchronized (RegistryServiceForProdGrpc.class) {
        if ((getListFilesMethod = RegistryServiceForProdGrpc.getListFilesMethod) == null) {
          RegistryServiceForProdGrpc.getListFilesMethod = getListFilesMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.registry.grpc.prod.ListFilesRequest, io.holoinsight.server.registry.grpc.prod.ListFilesResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "list_files"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.registry.grpc.prod.ListFilesRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.registry.grpc.prod.ListFilesResponse
                          .getDefaultInstance()))
                  .setSchemaDescriptor(
                      new RegistryServiceForProdMethodDescriptorSupplier("list_files"))
                  .build();
        }
      }
    }
    return getListFilesMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.prod.PreviewFileRequest, io.holoinsight.server.registry.grpc.prod.PreviewFileResponse> getPreviewFileMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "preview_file",
      requestType = io.holoinsight.server.registry.grpc.prod.PreviewFileRequest.class,
      responseType = io.holoinsight.server.registry.grpc.prod.PreviewFileResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.prod.PreviewFileRequest, io.holoinsight.server.registry.grpc.prod.PreviewFileResponse> getPreviewFileMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.prod.PreviewFileRequest, io.holoinsight.server.registry.grpc.prod.PreviewFileResponse> getPreviewFileMethod;
    if ((getPreviewFileMethod = RegistryServiceForProdGrpc.getPreviewFileMethod) == null) {
      synchronized (RegistryServiceForProdGrpc.class) {
        if ((getPreviewFileMethod = RegistryServiceForProdGrpc.getPreviewFileMethod) == null) {
          RegistryServiceForProdGrpc.getPreviewFileMethod = getPreviewFileMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.registry.grpc.prod.PreviewFileRequest, io.holoinsight.server.registry.grpc.prod.PreviewFileResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "preview_file"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.registry.grpc.prod.PreviewFileRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.registry.grpc.prod.PreviewFileResponse
                          .getDefaultInstance()))
                  .setSchemaDescriptor(
                      new RegistryServiceForProdMethodDescriptorSupplier("preview_file"))
                  .build();
        }
      }
    }
    return getPreviewFileMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.prod.InspectRequest, io.holoinsight.server.registry.grpc.prod.InspectResponse> getInspectMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "inspect",
      requestType = io.holoinsight.server.registry.grpc.prod.InspectRequest.class,
      responseType = io.holoinsight.server.registry.grpc.prod.InspectResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.prod.InspectRequest, io.holoinsight.server.registry.grpc.prod.InspectResponse> getInspectMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.prod.InspectRequest, io.holoinsight.server.registry.grpc.prod.InspectResponse> getInspectMethod;
    if ((getInspectMethod = RegistryServiceForProdGrpc.getInspectMethod) == null) {
      synchronized (RegistryServiceForProdGrpc.class) {
        if ((getInspectMethod = RegistryServiceForProdGrpc.getInspectMethod) == null) {
          RegistryServiceForProdGrpc.getInspectMethod = getInspectMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.registry.grpc.prod.InspectRequest, io.holoinsight.server.registry.grpc.prod.InspectResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "inspect"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.registry.grpc.prod.InspectRequest.getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.registry.grpc.prod.InspectResponse
                          .getDefaultInstance()))
                  .setSchemaDescriptor(
                      new RegistryServiceForProdMethodDescriptorSupplier("inspect"))
                  .build();
        }
      }
    }
    return getInspectMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.prod.DryRunRequest, io.holoinsight.server.registry.grpc.prod.DryRunResponse> getDryRunMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "dry_run",
      requestType = io.holoinsight.server.registry.grpc.prod.DryRunRequest.class,
      responseType = io.holoinsight.server.registry.grpc.prod.DryRunResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.prod.DryRunRequest, io.holoinsight.server.registry.grpc.prod.DryRunResponse> getDryRunMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.prod.DryRunRequest, io.holoinsight.server.registry.grpc.prod.DryRunResponse> getDryRunMethod;
    if ((getDryRunMethod = RegistryServiceForProdGrpc.getDryRunMethod) == null) {
      synchronized (RegistryServiceForProdGrpc.class) {
        if ((getDryRunMethod = RegistryServiceForProdGrpc.getDryRunMethod) == null) {
          RegistryServiceForProdGrpc.getDryRunMethod = getDryRunMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.registry.grpc.prod.DryRunRequest, io.holoinsight.server.registry.grpc.prod.DryRunResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "dry_run"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.registry.grpc.prod.DryRunRequest.getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.registry.grpc.prod.DryRunResponse.getDefaultInstance()))
                  .setSchemaDescriptor(
                      new RegistryServiceForProdMethodDescriptorSupplier("dry_run"))
                  .build();
        }
      }
    }
    return getDryRunMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest, io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionResponse> getCheckConfigDistributionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "check_config_distribution",
      requestType = io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest.class,
      responseType = io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest, io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionResponse> getCheckConfigDistributionMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest, io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionResponse> getCheckConfigDistributionMethod;
    if ((getCheckConfigDistributionMethod =
        RegistryServiceForProdGrpc.getCheckConfigDistributionMethod) == null) {
      synchronized (RegistryServiceForProdGrpc.class) {
        if ((getCheckConfigDistributionMethod =
            RegistryServiceForProdGrpc.getCheckConfigDistributionMethod) == null) {
          RegistryServiceForProdGrpc.getCheckConfigDistributionMethod =
              getCheckConfigDistributionMethod =
                  io.grpc.MethodDescriptor.<io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest, io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionResponse>newBuilder()
                      .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                      .setFullMethodName(
                          generateFullMethodName(SERVICE_NAME, "check_config_distribution"))
                      .setSampledToLocalTracing(true)
                      .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                          io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest
                              .getDefaultInstance()))
                      .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                          io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionResponse
                              .getDefaultInstance()))
                      .setSchemaDescriptor(new RegistryServiceForProdMethodDescriptorSupplier(
                          "check_config_distribution"))
                      .build();
        }
      }
    }
    return getCheckConfigDistributionMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static RegistryServiceForProdStub newStub(io.grpc.Channel channel) {
    return new RegistryServiceForProdStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static RegistryServiceForProdBlockingStub newBlockingStub(io.grpc.Channel channel) {
    return new RegistryServiceForProdBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static RegistryServiceForProdFutureStub newFutureStub(io.grpc.Channel channel) {
    return new RegistryServiceForProdFutureStub(channel);
  }

  /**
   * <pre>
   * 提供给 prod 调用的 registry 服务
   * </pre>
   */
  public static abstract class RegistryServiceForProdImplBase implements io.grpc.BindableService {

    /**
     */
    public void notifyCollectConfigUpdate(
        io.holoinsight.server.registry.grpc.prod.NotifyCollectConfigUpdatedRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.registry.grpc.prod.NotifyCollectConfigUpdatedResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getNotifyCollectConfigUpdateMethod(), responseObserver);
    }

    /**
     * <pre>
     * 查询目录
     * </pre>
     */
    public void listFiles(io.holoinsight.server.registry.grpc.prod.ListFilesRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.registry.grpc.prod.ListFilesResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getListFilesMethod(), responseObserver);
    }

    /**
     * <pre>
     * 预览文件
     * </pre>
     */
    public void previewFile(io.holoinsight.server.registry.grpc.prod.PreviewFileRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.registry.grpc.prod.PreviewFileResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getPreviewFileMethod(), responseObserver);
    }

    /**
     * <pre>
     * 查询agent基本信息
     * </pre>
     */
    public void inspect(io.holoinsight.server.registry.grpc.prod.InspectRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.registry.grpc.prod.InspectResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getInspectMethod(), responseObserver);
    }

    /**
     * <pre>
     * 配置试运行
     * </pre>
     */
    public void dryRun(io.holoinsight.server.registry.grpc.prod.DryRunRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.registry.grpc.prod.DryRunResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getDryRunMethod(), responseObserver);
    }

    /**
     * <pre>
     * 检查配置下发情况
     * </pre>
     */
    public void checkConfigDistribution(
        io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getCheckConfigDistributionMethod(), responseObserver);
    }

    @java.lang.Override
    public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(getNotifyCollectConfigUpdateMethod(), asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.registry.grpc.prod.NotifyCollectConfigUpdatedRequest, io.holoinsight.server.registry.grpc.prod.NotifyCollectConfigUpdatedResponse>(
                  this, METHODID_NOTIFY_COLLECT_CONFIG_UPDATE)))
          .addMethod(getListFilesMethod(), asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.registry.grpc.prod.ListFilesRequest, io.holoinsight.server.registry.grpc.prod.ListFilesResponse>(
                  this, METHODID_LIST_FILES)))
          .addMethod(getPreviewFileMethod(), asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.registry.grpc.prod.PreviewFileRequest, io.holoinsight.server.registry.grpc.prod.PreviewFileResponse>(
                  this, METHODID_PREVIEW_FILE)))
          .addMethod(getInspectMethod(), asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.registry.grpc.prod.InspectRequest, io.holoinsight.server.registry.grpc.prod.InspectResponse>(
                  this, METHODID_INSPECT)))
          .addMethod(getDryRunMethod(), asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.registry.grpc.prod.DryRunRequest, io.holoinsight.server.registry.grpc.prod.DryRunResponse>(
                  this, METHODID_DRY_RUN)))
          .addMethod(getCheckConfigDistributionMethod(), asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest, io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionResponse>(
                  this, METHODID_CHECK_CONFIG_DISTRIBUTION)))
          .build();
    }
  }

  /**
   * <pre>
   * 提供给 prod 调用的 registry 服务
   * </pre>
   */
  public static final class RegistryServiceForProdStub
      extends io.grpc.stub.AbstractStub<RegistryServiceForProdStub> {
    private RegistryServiceForProdStub(io.grpc.Channel channel) {
      super(channel);
    }

    private RegistryServiceForProdStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RegistryServiceForProdStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new RegistryServiceForProdStub(channel, callOptions);
    }

    /**
     */
    public void notifyCollectConfigUpdate(
        io.holoinsight.server.registry.grpc.prod.NotifyCollectConfigUpdatedRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.registry.grpc.prod.NotifyCollectConfigUpdatedResponse> responseObserver) {
      asyncUnaryCall(getChannel().newCall(getNotifyCollectConfigUpdateMethod(), getCallOptions()),
          request, responseObserver);
    }

    /**
     * <pre>
     * 查询目录
     * </pre>
     */
    public void listFiles(io.holoinsight.server.registry.grpc.prod.ListFilesRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.registry.grpc.prod.ListFilesResponse> responseObserver) {
      asyncUnaryCall(getChannel().newCall(getListFilesMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     * <pre>
     * 预览文件
     * </pre>
     */
    public void previewFile(io.holoinsight.server.registry.grpc.prod.PreviewFileRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.registry.grpc.prod.PreviewFileResponse> responseObserver) {
      asyncUnaryCall(getChannel().newCall(getPreviewFileMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     * <pre>
     * 查询agent基本信息
     * </pre>
     */
    public void inspect(io.holoinsight.server.registry.grpc.prod.InspectRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.registry.grpc.prod.InspectResponse> responseObserver) {
      asyncUnaryCall(getChannel().newCall(getInspectMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     * <pre>
     * 配置试运行
     * </pre>
     */
    public void dryRun(io.holoinsight.server.registry.grpc.prod.DryRunRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.registry.grpc.prod.DryRunResponse> responseObserver) {
      asyncUnaryCall(getChannel().newCall(getDryRunMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     * <pre>
     * 检查配置下发情况
     * </pre>
     */
    public void checkConfigDistribution(
        io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionResponse> responseObserver) {
      asyncUnaryCall(getChannel().newCall(getCheckConfigDistributionMethod(), getCallOptions()),
          request, responseObserver);
    }
  }

  /**
   * <pre>
   * 提供给 prod 调用的 registry 服务
   * </pre>
   */
  public static final class RegistryServiceForProdBlockingStub
      extends io.grpc.stub.AbstractStub<RegistryServiceForProdBlockingStub> {
    private RegistryServiceForProdBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private RegistryServiceForProdBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RegistryServiceForProdBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new RegistryServiceForProdBlockingStub(channel, callOptions);
    }

    /**
     */
    public io.holoinsight.server.registry.grpc.prod.NotifyCollectConfigUpdatedResponse notifyCollectConfigUpdate(
        io.holoinsight.server.registry.grpc.prod.NotifyCollectConfigUpdatedRequest request) {
      return blockingUnaryCall(getChannel(), getNotifyCollectConfigUpdateMethod(), getCallOptions(),
          request);
    }

    /**
     * <pre>
     * 查询目录
     * </pre>
     */
    public io.holoinsight.server.registry.grpc.prod.ListFilesResponse listFiles(
        io.holoinsight.server.registry.grpc.prod.ListFilesRequest request) {
      return blockingUnaryCall(getChannel(), getListFilesMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 预览文件
     * </pre>
     */
    public io.holoinsight.server.registry.grpc.prod.PreviewFileResponse previewFile(
        io.holoinsight.server.registry.grpc.prod.PreviewFileRequest request) {
      return blockingUnaryCall(getChannel(), getPreviewFileMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 查询agent基本信息
     * </pre>
     */
    public io.holoinsight.server.registry.grpc.prod.InspectResponse inspect(
        io.holoinsight.server.registry.grpc.prod.InspectRequest request) {
      return blockingUnaryCall(getChannel(), getInspectMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 配置试运行
     * </pre>
     */
    public io.holoinsight.server.registry.grpc.prod.DryRunResponse dryRun(
        io.holoinsight.server.registry.grpc.prod.DryRunRequest request) {
      return blockingUnaryCall(getChannel(), getDryRunMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 检查配置下发情况
     * </pre>
     */
    public io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionResponse checkConfigDistribution(
        io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest request) {
      return blockingUnaryCall(getChannel(), getCheckConfigDistributionMethod(), getCallOptions(),
          request);
    }
  }

  /**
   * <pre>
   * 提供给 prod 调用的 registry 服务
   * </pre>
   */
  public static final class RegistryServiceForProdFutureStub
      extends io.grpc.stub.AbstractStub<RegistryServiceForProdFutureStub> {
    private RegistryServiceForProdFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private RegistryServiceForProdFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RegistryServiceForProdFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new RegistryServiceForProdFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.registry.grpc.prod.NotifyCollectConfigUpdatedResponse> notifyCollectConfigUpdate(
        io.holoinsight.server.registry.grpc.prod.NotifyCollectConfigUpdatedRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getNotifyCollectConfigUpdateMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * 查询目录
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.registry.grpc.prod.ListFilesResponse> listFiles(
        io.holoinsight.server.registry.grpc.prod.ListFilesRequest request) {
      return futureUnaryCall(getChannel().newCall(getListFilesMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * 预览文件
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.registry.grpc.prod.PreviewFileResponse> previewFile(
        io.holoinsight.server.registry.grpc.prod.PreviewFileRequest request) {
      return futureUnaryCall(getChannel().newCall(getPreviewFileMethod(), getCallOptions()),
          request);
    }

    /**
     * <pre>
     * 查询agent基本信息
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.registry.grpc.prod.InspectResponse> inspect(
        io.holoinsight.server.registry.grpc.prod.InspectRequest request) {
      return futureUnaryCall(getChannel().newCall(getInspectMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * 配置试运行
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.registry.grpc.prod.DryRunResponse> dryRun(
        io.holoinsight.server.registry.grpc.prod.DryRunRequest request) {
      return futureUnaryCall(getChannel().newCall(getDryRunMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * 检查配置下发情况
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionResponse> checkConfigDistribution(
        io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCheckConfigDistributionMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_NOTIFY_COLLECT_CONFIG_UPDATE = 0;
  private static final int METHODID_LIST_FILES = 1;
  private static final int METHODID_PREVIEW_FILE = 2;
  private static final int METHODID_INSPECT = 3;
  private static final int METHODID_DRY_RUN = 4;
  private static final int METHODID_CHECK_CONFIG_DISTRIBUTION = 5;

  private static final class MethodHandlers<Req, Resp>
      implements io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final RegistryServiceForProdImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(RegistryServiceForProdImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_NOTIFY_COLLECT_CONFIG_UPDATE:
          serviceImpl.notifyCollectConfigUpdate(
              (io.holoinsight.server.registry.grpc.prod.NotifyCollectConfigUpdatedRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.registry.grpc.prod.NotifyCollectConfigUpdatedResponse>) responseObserver);
          break;
        case METHODID_LIST_FILES:
          serviceImpl.listFiles((io.holoinsight.server.registry.grpc.prod.ListFilesRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.registry.grpc.prod.ListFilesResponse>) responseObserver);
          break;
        case METHODID_PREVIEW_FILE:
          serviceImpl.previewFile(
              (io.holoinsight.server.registry.grpc.prod.PreviewFileRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.registry.grpc.prod.PreviewFileResponse>) responseObserver);
          break;
        case METHODID_INSPECT:
          serviceImpl.inspect((io.holoinsight.server.registry.grpc.prod.InspectRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.registry.grpc.prod.InspectResponse>) responseObserver);
          break;
        case METHODID_DRY_RUN:
          serviceImpl.dryRun((io.holoinsight.server.registry.grpc.prod.DryRunRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.registry.grpc.prod.DryRunResponse>) responseObserver);
          break;
        case METHODID_CHECK_CONFIG_DISTRIBUTION:
          serviceImpl.checkConfigDistribution(
              (io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.registry.grpc.prod.CheckConfigDistributionResponse>) responseObserver);
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

  private static abstract class RegistryServiceForProdBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier,
      io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    RegistryServiceForProdBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return io.holoinsight.server.registry.grpc.prod.RegistryForProdProtos.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("RegistryServiceForProd");
    }
  }

  private static final class RegistryServiceForProdFileDescriptorSupplier
      extends RegistryServiceForProdBaseDescriptorSupplier {
    RegistryServiceForProdFileDescriptorSupplier() {}
  }

  private static final class RegistryServiceForProdMethodDescriptorSupplier
      extends RegistryServiceForProdBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    RegistryServiceForProdMethodDescriptorSupplier(String methodName) {
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
      synchronized (RegistryServiceForProdGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new RegistryServiceForProdFileDescriptorSupplier())
              .addMethod(getNotifyCollectConfigUpdateMethod()).addMethod(getListFilesMethod())
              .addMethod(getPreviewFileMethod()).addMethod(getInspectMethod())
              .addMethod(getDryRunMethod()).addMethod(getCheckConfigDistributionMethod()).build();
        }
      }
    }
    return result;
  }
}
