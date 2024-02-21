/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.grpc.agent;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(value = "by gRPC proto compiler (version 1.23.0)",
    comments = "Source: registry-for-agent.proto")
public final class RegistryServiceForAgentGrpc {

  private RegistryServiceForAgentGrpc() {}

  public static final String SERVICE_NAME =
      "io.holoinsight.server.registry.grpc.agent.RegistryServiceForAgent";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.google.protobuf.Empty, com.google.protobuf.Empty> getPingMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "ping",
      requestType = com.google.protobuf.Empty.class, responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.protobuf.Empty, com.google.protobuf.Empty> getPingMethod() {
    io.grpc.MethodDescriptor<com.google.protobuf.Empty, com.google.protobuf.Empty> getPingMethod;
    if ((getPingMethod = RegistryServiceForAgentGrpc.getPingMethod) == null) {
      synchronized (RegistryServiceForAgentGrpc.class) {
        if ((getPingMethod = RegistryServiceForAgentGrpc.getPingMethod) == null) {
          RegistryServiceForAgentGrpc.getPingMethod = getPingMethod =
              io.grpc.MethodDescriptor.<com.google.protobuf.Empty, com.google.protobuf.Empty>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ping"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(com.google.protobuf.Empty.getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(com.google.protobuf.Empty.getDefaultInstance()))
                  .setSchemaDescriptor(new RegistryServiceForAgentMethodDescriptorSupplier("ping"))
                  .build();
        }
      }
    }
    return getPingMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.agent.RegisterAgentRequest, io.holoinsight.server.registry.grpc.agent.RegisterAgentResponse> getRegisterAgentMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "register_agent",
      requestType = io.holoinsight.server.registry.grpc.agent.RegisterAgentRequest.class,
      responseType = io.holoinsight.server.registry.grpc.agent.RegisterAgentResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.agent.RegisterAgentRequest, io.holoinsight.server.registry.grpc.agent.RegisterAgentResponse> getRegisterAgentMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.agent.RegisterAgentRequest, io.holoinsight.server.registry.grpc.agent.RegisterAgentResponse> getRegisterAgentMethod;
    if ((getRegisterAgentMethod = RegistryServiceForAgentGrpc.getRegisterAgentMethod) == null) {
      synchronized (RegistryServiceForAgentGrpc.class) {
        if ((getRegisterAgentMethod = RegistryServiceForAgentGrpc.getRegisterAgentMethod) == null) {
          RegistryServiceForAgentGrpc.getRegisterAgentMethod = getRegisterAgentMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.registry.grpc.agent.RegisterAgentRequest, io.holoinsight.server.registry.grpc.agent.RegisterAgentResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "register_agent"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.registry.grpc.agent.RegisterAgentRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.registry.grpc.agent.RegisterAgentResponse
                          .getDefaultInstance()))
                  .setSchemaDescriptor(
                      new RegistryServiceForAgentMethodDescriptorSupplier("register_agent"))
                  .build();
        }
      }
    }
    return getRegisterAgentMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.agent.SendAgentHeartbeatRequest, io.holoinsight.server.registry.grpc.agent.SendAgentHeartbeatResponse> getSendAgentHeartbeatMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "send_agent_heartbeat",
      requestType = io.holoinsight.server.registry.grpc.agent.SendAgentHeartbeatRequest.class,
      responseType = io.holoinsight.server.registry.grpc.agent.SendAgentHeartbeatResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.agent.SendAgentHeartbeatRequest, io.holoinsight.server.registry.grpc.agent.SendAgentHeartbeatResponse> getSendAgentHeartbeatMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.agent.SendAgentHeartbeatRequest, io.holoinsight.server.registry.grpc.agent.SendAgentHeartbeatResponse> getSendAgentHeartbeatMethod;
    if ((getSendAgentHeartbeatMethod =
        RegistryServiceForAgentGrpc.getSendAgentHeartbeatMethod) == null) {
      synchronized (RegistryServiceForAgentGrpc.class) {
        if ((getSendAgentHeartbeatMethod =
            RegistryServiceForAgentGrpc.getSendAgentHeartbeatMethod) == null) {
          RegistryServiceForAgentGrpc.getSendAgentHeartbeatMethod = getSendAgentHeartbeatMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.registry.grpc.agent.SendAgentHeartbeatRequest, io.holoinsight.server.registry.grpc.agent.SendAgentHeartbeatResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "send_agent_heartbeat"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.registry.grpc.agent.SendAgentHeartbeatRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.registry.grpc.agent.SendAgentHeartbeatResponse
                          .getDefaultInstance()))
                  .setSchemaDescriptor(
                      new RegistryServiceForAgentMethodDescriptorSupplier("send_agent_heartbeat"))
                  .build();
        }
      }
    }
    return getSendAgentHeartbeatMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.agent.GetControlConfigsRequest, io.holoinsight.server.registry.grpc.agent.GetControlConfigsResponse> getGetControlConfigsMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "get_control_configs",
      requestType = io.holoinsight.server.registry.grpc.agent.GetControlConfigsRequest.class,
      responseType = io.holoinsight.server.registry.grpc.agent.GetControlConfigsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.agent.GetControlConfigsRequest, io.holoinsight.server.registry.grpc.agent.GetControlConfigsResponse> getGetControlConfigsMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.agent.GetControlConfigsRequest, io.holoinsight.server.registry.grpc.agent.GetControlConfigsResponse> getGetControlConfigsMethod;
    if ((getGetControlConfigsMethod =
        RegistryServiceForAgentGrpc.getGetControlConfigsMethod) == null) {
      synchronized (RegistryServiceForAgentGrpc.class) {
        if ((getGetControlConfigsMethod =
            RegistryServiceForAgentGrpc.getGetControlConfigsMethod) == null) {
          RegistryServiceForAgentGrpc.getGetControlConfigsMethod = getGetControlConfigsMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.registry.grpc.agent.GetControlConfigsRequest, io.holoinsight.server.registry.grpc.agent.GetControlConfigsResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "get_control_configs"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.registry.grpc.agent.GetControlConfigsRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.registry.grpc.agent.GetControlConfigsResponse
                          .getDefaultInstance()))
                  .setSchemaDescriptor(
                      new RegistryServiceForAgentMethodDescriptorSupplier("get_control_configs"))
                  .build();
        }
      }
    }
    return getGetControlConfigsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.agent.GetCollectTasksRequest, io.holoinsight.server.registry.grpc.agent.GetCollectTasksResponse> getGetCollectTasksMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "get_collect_tasks",
      requestType = io.holoinsight.server.registry.grpc.agent.GetCollectTasksRequest.class,
      responseType = io.holoinsight.server.registry.grpc.agent.GetCollectTasksResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.agent.GetCollectTasksRequest, io.holoinsight.server.registry.grpc.agent.GetCollectTasksResponse> getGetCollectTasksMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.agent.GetCollectTasksRequest, io.holoinsight.server.registry.grpc.agent.GetCollectTasksResponse> getGetCollectTasksMethod;
    if ((getGetCollectTasksMethod = RegistryServiceForAgentGrpc.getGetCollectTasksMethod) == null) {
      synchronized (RegistryServiceForAgentGrpc.class) {
        if ((getGetCollectTasksMethod =
            RegistryServiceForAgentGrpc.getGetCollectTasksMethod) == null) {
          RegistryServiceForAgentGrpc.getGetCollectTasksMethod = getGetCollectTasksMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.registry.grpc.agent.GetCollectTasksRequest, io.holoinsight.server.registry.grpc.agent.GetCollectTasksResponse>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "get_collect_tasks"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.registry.grpc.agent.GetCollectTasksRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.registry.grpc.agent.GetCollectTasksResponse
                          .getDefaultInstance()))
                  .setSchemaDescriptor(
                      new RegistryServiceForAgentMethodDescriptorSupplier("get_collect_tasks"))
                  .build();
        }
      }
    }
    return getGetCollectTasksMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.common.grpc.GenericRpcCommand, io.holoinsight.server.common.grpc.GenericRpcCommand> getBiStreamsMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "bi_streams",
      requestType = io.holoinsight.server.common.grpc.GenericRpcCommand.class,
      responseType = io.holoinsight.server.common.grpc.GenericRpcCommand.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.common.grpc.GenericRpcCommand, io.holoinsight.server.common.grpc.GenericRpcCommand> getBiStreamsMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.common.grpc.GenericRpcCommand, io.holoinsight.server.common.grpc.GenericRpcCommand> getBiStreamsMethod;
    if ((getBiStreamsMethod = RegistryServiceForAgentGrpc.getBiStreamsMethod) == null) {
      synchronized (RegistryServiceForAgentGrpc.class) {
        if ((getBiStreamsMethod = RegistryServiceForAgentGrpc.getBiStreamsMethod) == null) {
          RegistryServiceForAgentGrpc.getBiStreamsMethod = getBiStreamsMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.common.grpc.GenericRpcCommand, io.holoinsight.server.common.grpc.GenericRpcCommand>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "bi_streams"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.common.grpc.GenericRpcCommand.getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.common.grpc.GenericRpcCommand.getDefaultInstance()))
                  .setSchemaDescriptor(
                      new RegistryServiceForAgentMethodDescriptorSupplier("bi_streams"))
                  .build();
        }
      }
    }
    return getBiStreamsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.agent.MetaSync.FullSyncRequest, com.google.protobuf.Empty> getMetaFullSyncMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "meta_full_sync",
      requestType = io.holoinsight.server.registry.grpc.agent.MetaSync.FullSyncRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.agent.MetaSync.FullSyncRequest, com.google.protobuf.Empty> getMetaFullSyncMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.agent.MetaSync.FullSyncRequest, com.google.protobuf.Empty> getMetaFullSyncMethod;
    if ((getMetaFullSyncMethod = RegistryServiceForAgentGrpc.getMetaFullSyncMethod) == null) {
      synchronized (RegistryServiceForAgentGrpc.class) {
        if ((getMetaFullSyncMethod = RegistryServiceForAgentGrpc.getMetaFullSyncMethod) == null) {
          RegistryServiceForAgentGrpc.getMetaFullSyncMethod = getMetaFullSyncMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.registry.grpc.agent.MetaSync.FullSyncRequest, com.google.protobuf.Empty>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "meta_full_sync"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.registry.grpc.agent.MetaSync.FullSyncRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(com.google.protobuf.Empty.getDefaultInstance()))
                  .setSchemaDescriptor(
                      new RegistryServiceForAgentMethodDescriptorSupplier("meta_full_sync"))
                  .build();
        }
      }
    }
    return getMetaFullSyncMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.agent.MetaSync.DeltaSyncRequest, com.google.protobuf.Empty> getMetaDeltaSyncMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "meta_delta_sync",
      requestType = io.holoinsight.server.registry.grpc.agent.MetaSync.DeltaSyncRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.agent.MetaSync.DeltaSyncRequest, com.google.protobuf.Empty> getMetaDeltaSyncMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.agent.MetaSync.DeltaSyncRequest, com.google.protobuf.Empty> getMetaDeltaSyncMethod;
    if ((getMetaDeltaSyncMethod = RegistryServiceForAgentGrpc.getMetaDeltaSyncMethod) == null) {
      synchronized (RegistryServiceForAgentGrpc.class) {
        if ((getMetaDeltaSyncMethod = RegistryServiceForAgentGrpc.getMetaDeltaSyncMethod) == null) {
          RegistryServiceForAgentGrpc.getMetaDeltaSyncMethod = getMetaDeltaSyncMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.registry.grpc.agent.MetaSync.DeltaSyncRequest, com.google.protobuf.Empty>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "meta_delta_sync"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                      io.holoinsight.server.registry.grpc.agent.MetaSync.DeltaSyncRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(com.google.protobuf.Empty.getDefaultInstance()))
                  .setSchemaDescriptor(
                      new RegistryServiceForAgentMethodDescriptorSupplier("meta_delta_sync"))
                  .build();
        }
      }
    }
    return getMetaDeltaSyncMethod;
  }

  private static volatile io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.agent.ReportEventRequest, com.google.protobuf.Empty> getReportEventsMethod;

  @io.grpc.stub.annotations.RpcMethod(fullMethodName = SERVICE_NAME + '/' + "report_events",
      requestType = io.holoinsight.server.registry.grpc.agent.ReportEventRequest.class,
      responseType = com.google.protobuf.Empty.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.agent.ReportEventRequest, com.google.protobuf.Empty> getReportEventsMethod() {
    io.grpc.MethodDescriptor<io.holoinsight.server.registry.grpc.agent.ReportEventRequest, com.google.protobuf.Empty> getReportEventsMethod;
    if ((getReportEventsMethod = RegistryServiceForAgentGrpc.getReportEventsMethod) == null) {
      synchronized (RegistryServiceForAgentGrpc.class) {
        if ((getReportEventsMethod = RegistryServiceForAgentGrpc.getReportEventsMethod) == null) {
          RegistryServiceForAgentGrpc.getReportEventsMethod = getReportEventsMethod =
              io.grpc.MethodDescriptor.<io.holoinsight.server.registry.grpc.agent.ReportEventRequest, com.google.protobuf.Empty>newBuilder()
                  .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
                  .setFullMethodName(generateFullMethodName(SERVICE_NAME, "report_events"))
                  .setSampledToLocalTracing(true)
                  .setRequestMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(io.holoinsight.server.registry.grpc.agent.ReportEventRequest
                          .getDefaultInstance()))
                  .setResponseMarshaller(io.grpc.protobuf.ProtoUtils
                      .marshaller(com.google.protobuf.Empty.getDefaultInstance()))
                  .setSchemaDescriptor(
                      new RegistryServiceForAgentMethodDescriptorSupplier("report_events"))
                  .build();
        }
      }
    }
    return getReportEventsMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static RegistryServiceForAgentStub newStub(io.grpc.Channel channel) {
    return new RegistryServiceForAgentStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static RegistryServiceForAgentBlockingStub newBlockingStub(io.grpc.Channel channel) {
    return new RegistryServiceForAgentBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static RegistryServiceForAgentFutureStub newFutureStub(io.grpc.Channel channel) {
    return new RegistryServiceForAgentFutureStub(channel);
  }

  /**
   */
  public static abstract class RegistryServiceForAgentImplBase implements io.grpc.BindableService {

    /**
     */
    public void ping(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(getPingMethod(), responseObserver);
    }

    /**
     * <pre>
     * 向服务端注册一个agent, 每N时间执行一次, 携带的信息比较多
     * </pre>
     */
    public void registerAgent(
        io.holoinsight.server.registry.grpc.agent.RegisterAgentRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.registry.grpc.agent.RegisterAgentResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getRegisterAgentMethod(), responseObserver);
    }

    /**
     * <pre>
     * 发送Agent心跳
     * </pre>
     */
    public void sendAgentHeartbeat(
        io.holoinsight.server.registry.grpc.agent.SendAgentHeartbeatRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.registry.grpc.agent.SendAgentHeartbeatResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getSendAgentHeartbeatMethod(), responseObserver);
    }

    /**
     * <pre>
     * 查询registry对该agent的控制参数
     * </pre>
     */
    public void getControlConfigs(
        io.holoinsight.server.registry.grpc.agent.GetControlConfigsRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.registry.grpc.agent.GetControlConfigsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getGetControlConfigsMethod(), responseObserver);
    }

    /**
     * <pre>
     * 查询采集配置
     * </pre>
     */
    public void getCollectTasks(
        io.holoinsight.server.registry.grpc.agent.GetCollectTasksRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.registry.grpc.agent.GetCollectTasksResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getGetCollectTasksMethod(), responseObserver);
    }

    /**
     * <pre>
     * 双向流 用于实现反向调用, 实现 reverse rpc on grpc
     * 见文档 https://yuque.antfin.com/neo-matrix/lgdwtb/oevt3g
     * </pre>
     */
    public io.grpc.stub.StreamObserver<io.holoinsight.server.common.grpc.GenericRpcCommand> biStreams(
        io.grpc.stub.StreamObserver<io.holoinsight.server.common.grpc.GenericRpcCommand> responseObserver) {
      return asyncUnimplementedStreamingCall(getBiStreamsMethod(), responseObserver);
    }

    /**
     */
    public void metaFullSync(
        io.holoinsight.server.registry.grpc.agent.MetaSync.FullSyncRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(getMetaFullSyncMethod(), responseObserver);
    }

    /**
     */
    public void metaDeltaSync(
        io.holoinsight.server.registry.grpc.agent.MetaSync.DeltaSyncRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(getMetaDeltaSyncMethod(), responseObserver);
    }

    /**
     */
    public void reportEvents(io.holoinsight.server.registry.grpc.agent.ReportEventRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(getReportEventsMethod(), responseObserver);
    }

    @java.lang.Override
    public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor()).addMethod(
          getPingMethod(),
          asyncUnaryCall(new MethodHandlers<com.google.protobuf.Empty, com.google.protobuf.Empty>(
              this, METHODID_PING)))
          .addMethod(getRegisterAgentMethod(), asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.registry.grpc.agent.RegisterAgentRequest, io.holoinsight.server.registry.grpc.agent.RegisterAgentResponse>(
                  this, METHODID_REGISTER_AGENT)))
          .addMethod(getSendAgentHeartbeatMethod(), asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.registry.grpc.agent.SendAgentHeartbeatRequest, io.holoinsight.server.registry.grpc.agent.SendAgentHeartbeatResponse>(
                  this, METHODID_SEND_AGENT_HEARTBEAT)))
          .addMethod(getGetControlConfigsMethod(), asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.registry.grpc.agent.GetControlConfigsRequest, io.holoinsight.server.registry.grpc.agent.GetControlConfigsResponse>(
                  this, METHODID_GET_CONTROL_CONFIGS)))
          .addMethod(getGetCollectTasksMethod(), asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.registry.grpc.agent.GetCollectTasksRequest, io.holoinsight.server.registry.grpc.agent.GetCollectTasksResponse>(
                  this, METHODID_GET_COLLECT_TASKS)))
          .addMethod(getBiStreamsMethod(), asyncBidiStreamingCall(
              new MethodHandlers<io.holoinsight.server.common.grpc.GenericRpcCommand, io.holoinsight.server.common.grpc.GenericRpcCommand>(
                  this, METHODID_BI_STREAMS)))
          .addMethod(getMetaFullSyncMethod(), asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.registry.grpc.agent.MetaSync.FullSyncRequest, com.google.protobuf.Empty>(
                  this, METHODID_META_FULL_SYNC)))
          .addMethod(getMetaDeltaSyncMethod(), asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.registry.grpc.agent.MetaSync.DeltaSyncRequest, com.google.protobuf.Empty>(
                  this, METHODID_META_DELTA_SYNC)))
          .addMethod(getReportEventsMethod(), asyncUnaryCall(
              new MethodHandlers<io.holoinsight.server.registry.grpc.agent.ReportEventRequest, com.google.protobuf.Empty>(
                  this, METHODID_REPORT_EVENTS)))
          .build();
    }
  }

  /**
   */
  public static final class RegistryServiceForAgentStub
      extends io.grpc.stub.AbstractStub<RegistryServiceForAgentStub> {
    private RegistryServiceForAgentStub(io.grpc.Channel channel) {
      super(channel);
    }

    private RegistryServiceForAgentStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RegistryServiceForAgentStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new RegistryServiceForAgentStub(channel, callOptions);
    }

    /**
     */
    public void ping(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(getChannel().newCall(getPingMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     * <pre>
     * 向服务端注册一个agent, 每N时间执行一次, 携带的信息比较多
     * </pre>
     */
    public void registerAgent(
        io.holoinsight.server.registry.grpc.agent.RegisterAgentRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.registry.grpc.agent.RegisterAgentResponse> responseObserver) {
      asyncUnaryCall(getChannel().newCall(getRegisterAgentMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     * <pre>
     * 发送Agent心跳
     * </pre>
     */
    public void sendAgentHeartbeat(
        io.holoinsight.server.registry.grpc.agent.SendAgentHeartbeatRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.registry.grpc.agent.SendAgentHeartbeatResponse> responseObserver) {
      asyncUnaryCall(getChannel().newCall(getSendAgentHeartbeatMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     * <pre>
     * 查询registry对该agent的控制参数
     * </pre>
     */
    public void getControlConfigs(
        io.holoinsight.server.registry.grpc.agent.GetControlConfigsRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.registry.grpc.agent.GetControlConfigsResponse> responseObserver) {
      asyncUnaryCall(getChannel().newCall(getGetControlConfigsMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     * <pre>
     * 查询采集配置
     * </pre>
     */
    public void getCollectTasks(
        io.holoinsight.server.registry.grpc.agent.GetCollectTasksRequest request,
        io.grpc.stub.StreamObserver<io.holoinsight.server.registry.grpc.agent.GetCollectTasksResponse> responseObserver) {
      asyncUnaryCall(getChannel().newCall(getGetCollectTasksMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     * <pre>
     * 双向流 用于实现反向调用, 实现 reverse rpc on grpc
     * 见文档 https://yuque.antfin.com/neo-matrix/lgdwtb/oevt3g
     * </pre>
     */
    public io.grpc.stub.StreamObserver<io.holoinsight.server.common.grpc.GenericRpcCommand> biStreams(
        io.grpc.stub.StreamObserver<io.holoinsight.server.common.grpc.GenericRpcCommand> responseObserver) {
      return asyncBidiStreamingCall(getChannel().newCall(getBiStreamsMethod(), getCallOptions()),
          responseObserver);
    }

    /**
     */
    public void metaFullSync(
        io.holoinsight.server.registry.grpc.agent.MetaSync.FullSyncRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(getChannel().newCall(getMetaFullSyncMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void metaDeltaSync(
        io.holoinsight.server.registry.grpc.agent.MetaSync.DeltaSyncRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(getChannel().newCall(getMetaDeltaSyncMethod(), getCallOptions()), request,
          responseObserver);
    }

    /**
     */
    public void reportEvents(io.holoinsight.server.registry.grpc.agent.ReportEventRequest request,
        io.grpc.stub.StreamObserver<com.google.protobuf.Empty> responseObserver) {
      asyncUnaryCall(getChannel().newCall(getReportEventsMethod(), getCallOptions()), request,
          responseObserver);
    }
  }

  /**
   */
  public static final class RegistryServiceForAgentBlockingStub
      extends io.grpc.stub.AbstractStub<RegistryServiceForAgentBlockingStub> {
    private RegistryServiceForAgentBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private RegistryServiceForAgentBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RegistryServiceForAgentBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new RegistryServiceForAgentBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.google.protobuf.Empty ping(com.google.protobuf.Empty request) {
      return blockingUnaryCall(getChannel(), getPingMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 向服务端注册一个agent, 每N时间执行一次, 携带的信息比较多
     * </pre>
     */
    public io.holoinsight.server.registry.grpc.agent.RegisterAgentResponse registerAgent(
        io.holoinsight.server.registry.grpc.agent.RegisterAgentRequest request) {
      return blockingUnaryCall(getChannel(), getRegisterAgentMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 发送Agent心跳
     * </pre>
     */
    public io.holoinsight.server.registry.grpc.agent.SendAgentHeartbeatResponse sendAgentHeartbeat(
        io.holoinsight.server.registry.grpc.agent.SendAgentHeartbeatRequest request) {
      return blockingUnaryCall(getChannel(), getSendAgentHeartbeatMethod(), getCallOptions(),
          request);
    }

    /**
     * <pre>
     * 查询registry对该agent的控制参数
     * </pre>
     */
    public io.holoinsight.server.registry.grpc.agent.GetControlConfigsResponse getControlConfigs(
        io.holoinsight.server.registry.grpc.agent.GetControlConfigsRequest request) {
      return blockingUnaryCall(getChannel(), getGetControlConfigsMethod(), getCallOptions(),
          request);
    }

    /**
     * <pre>
     * 查询采集配置
     * </pre>
     */
    public io.holoinsight.server.registry.grpc.agent.GetCollectTasksResponse getCollectTasks(
        io.holoinsight.server.registry.grpc.agent.GetCollectTasksRequest request) {
      return blockingUnaryCall(getChannel(), getGetCollectTasksMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty metaFullSync(
        io.holoinsight.server.registry.grpc.agent.MetaSync.FullSyncRequest request) {
      return blockingUnaryCall(getChannel(), getMetaFullSyncMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty metaDeltaSync(
        io.holoinsight.server.registry.grpc.agent.MetaSync.DeltaSyncRequest request) {
      return blockingUnaryCall(getChannel(), getMetaDeltaSyncMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.google.protobuf.Empty reportEvents(
        io.holoinsight.server.registry.grpc.agent.ReportEventRequest request) {
      return blockingUnaryCall(getChannel(), getReportEventsMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class RegistryServiceForAgentFutureStub
      extends io.grpc.stub.AbstractStub<RegistryServiceForAgentFutureStub> {
    private RegistryServiceForAgentFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private RegistryServiceForAgentFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RegistryServiceForAgentFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new RegistryServiceForAgentFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> ping(
        com.google.protobuf.Empty request) {
      return futureUnaryCall(getChannel().newCall(getPingMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * 向服务端注册一个agent, 每N时间执行一次, 携带的信息比较多
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.registry.grpc.agent.RegisterAgentResponse> registerAgent(
        io.holoinsight.server.registry.grpc.agent.RegisterAgentRequest request) {
      return futureUnaryCall(getChannel().newCall(getRegisterAgentMethod(), getCallOptions()),
          request);
    }

    /**
     * <pre>
     * 发送Agent心跳
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.registry.grpc.agent.SendAgentHeartbeatResponse> sendAgentHeartbeat(
        io.holoinsight.server.registry.grpc.agent.SendAgentHeartbeatRequest request) {
      return futureUnaryCall(getChannel().newCall(getSendAgentHeartbeatMethod(), getCallOptions()),
          request);
    }

    /**
     * <pre>
     * 查询registry对该agent的控制参数
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.registry.grpc.agent.GetControlConfigsResponse> getControlConfigs(
        io.holoinsight.server.registry.grpc.agent.GetControlConfigsRequest request) {
      return futureUnaryCall(getChannel().newCall(getGetControlConfigsMethod(), getCallOptions()),
          request);
    }

    /**
     * <pre>
     * 查询采集配置
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<io.holoinsight.server.registry.grpc.agent.GetCollectTasksResponse> getCollectTasks(
        io.holoinsight.server.registry.grpc.agent.GetCollectTasksRequest request) {
      return futureUnaryCall(getChannel().newCall(getGetCollectTasksMethod(), getCallOptions()),
          request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> metaFullSync(
        io.holoinsight.server.registry.grpc.agent.MetaSync.FullSyncRequest request) {
      return futureUnaryCall(getChannel().newCall(getMetaFullSyncMethod(), getCallOptions()),
          request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> metaDeltaSync(
        io.holoinsight.server.registry.grpc.agent.MetaSync.DeltaSyncRequest request) {
      return futureUnaryCall(getChannel().newCall(getMetaDeltaSyncMethod(), getCallOptions()),
          request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.google.protobuf.Empty> reportEvents(
        io.holoinsight.server.registry.grpc.agent.ReportEventRequest request) {
      return futureUnaryCall(getChannel().newCall(getReportEventsMethod(), getCallOptions()),
          request);
    }
  }

  private static final int METHODID_PING = 0;
  private static final int METHODID_REGISTER_AGENT = 1;
  private static final int METHODID_SEND_AGENT_HEARTBEAT = 2;
  private static final int METHODID_GET_CONTROL_CONFIGS = 3;
  private static final int METHODID_GET_COLLECT_TASKS = 4;
  private static final int METHODID_META_FULL_SYNC = 5;
  private static final int METHODID_META_DELTA_SYNC = 6;
  private static final int METHODID_REPORT_EVENTS = 7;
  private static final int METHODID_BI_STREAMS = 8;

  private static final class MethodHandlers<Req, Resp>
      implements io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final RegistryServiceForAgentImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(RegistryServiceForAgentImplBase serviceImpl, int methodId) {
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
        case METHODID_REGISTER_AGENT:
          serviceImpl.registerAgent(
              (io.holoinsight.server.registry.grpc.agent.RegisterAgentRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.registry.grpc.agent.RegisterAgentResponse>) responseObserver);
          break;
        case METHODID_SEND_AGENT_HEARTBEAT:
          serviceImpl.sendAgentHeartbeat(
              (io.holoinsight.server.registry.grpc.agent.SendAgentHeartbeatRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.registry.grpc.agent.SendAgentHeartbeatResponse>) responseObserver);
          break;
        case METHODID_GET_CONTROL_CONFIGS:
          serviceImpl.getControlConfigs(
              (io.holoinsight.server.registry.grpc.agent.GetControlConfigsRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.registry.grpc.agent.GetControlConfigsResponse>) responseObserver);
          break;
        case METHODID_GET_COLLECT_TASKS:
          serviceImpl.getCollectTasks(
              (io.holoinsight.server.registry.grpc.agent.GetCollectTasksRequest) request,
              (io.grpc.stub.StreamObserver<io.holoinsight.server.registry.grpc.agent.GetCollectTasksResponse>) responseObserver);
          break;
        case METHODID_META_FULL_SYNC:
          serviceImpl.metaFullSync(
              (io.holoinsight.server.registry.grpc.agent.MetaSync.FullSyncRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_META_DELTA_SYNC:
          serviceImpl.metaDeltaSync(
              (io.holoinsight.server.registry.grpc.agent.MetaSync.DeltaSyncRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
          break;
        case METHODID_REPORT_EVENTS:
          serviceImpl.reportEvents(
              (io.holoinsight.server.registry.grpc.agent.ReportEventRequest) request,
              (io.grpc.stub.StreamObserver<com.google.protobuf.Empty>) responseObserver);
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
        case METHODID_BI_STREAMS:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.biStreams(
              (io.grpc.stub.StreamObserver<io.holoinsight.server.common.grpc.GenericRpcCommand>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class RegistryServiceForAgentBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier,
      io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    RegistryServiceForAgentBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return io.holoinsight.server.registry.grpc.agent.RegistryForAgentProtos.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("RegistryServiceForAgent");
    }
  }

  private static final class RegistryServiceForAgentFileDescriptorSupplier
      extends RegistryServiceForAgentBaseDescriptorSupplier {
    RegistryServiceForAgentFileDescriptorSupplier() {}
  }

  private static final class RegistryServiceForAgentMethodDescriptorSupplier
      extends RegistryServiceForAgentBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    RegistryServiceForAgentMethodDescriptorSupplier(String methodName) {
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
      synchronized (RegistryServiceForAgentGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new RegistryServiceForAgentFileDescriptorSupplier())
              .addMethod(getPingMethod()).addMethod(getRegisterAgentMethod())
              .addMethod(getSendAgentHeartbeatMethod()).addMethod(getGetControlConfigsMethod())
              .addMethod(getGetCollectTasksMethod()).addMethod(getBiStreamsMethod())
              .addMethod(getMetaFullSyncMethod()).addMethod(getMetaDeltaSyncMethod())
              .addMethod(getReportEventsMethod()).build();
        }
      }
    }
    return result;
  }
}
