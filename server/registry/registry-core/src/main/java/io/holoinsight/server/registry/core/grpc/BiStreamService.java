/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;

import io.grpc.Channel;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.holoinsight.server.common.JsonUtils;
import io.holoinsight.server.common.ProtoJsonUtils;
import io.holoinsight.server.common.grpc.CommonRequestHeader;
import io.holoinsight.server.common.grpc.CommonResponseHeader;
import io.holoinsight.server.common.threadpool.CommonThreadPools;
import io.holoinsight.server.meta.facade.model.MetaType;
import io.holoinsight.server.registry.core.RegistryProperties;
import io.holoinsight.server.registry.core.agent.Agent;
import io.holoinsight.server.registry.core.agent.AgentStorage;
import io.holoinsight.server.registry.core.agent.ConnectionInfo;
import io.holoinsight.server.registry.core.agent.DaemonsetAgent;
import io.holoinsight.server.registry.core.agent.DaemonsetAgentService;
import io.holoinsight.server.registry.core.dim.ProdDimService;
import io.holoinsight.server.registry.core.grpc.stream.ServerStream;
import io.holoinsight.server.registry.core.grpc.stream.ServerStreamManager;
import io.holoinsight.server.registry.core.grpc.streambiz.BizTypes;
import io.holoinsight.server.registry.grpc.internal.BiStreamProxyRequest;
import io.holoinsight.server.registry.grpc.internal.BiStreamProxyResponse;
import io.holoinsight.server.registry.grpc.internal.RegistryServiceForInternalGrpc;
import io.holoinsight.server.registry.grpc.prod.CheckConfigTaskDistributionResponse;
import io.holoinsight.server.registry.grpc.prod.DryRunResponse;
import io.holoinsight.server.registry.grpc.prod.InspectResponse;
import io.holoinsight.server.registry.grpc.prod.ListFilesResponse;
import io.holoinsight.server.registry.grpc.prod.PreviewFileResponse;
import io.holoinsight.server.registry.grpc.prod.TargetIdentifier;
import lombok.val;
import reactor.core.publisher.Mono;

/**
 * Bidirectional Stream Service is response for:
 * <ul>
 * <li>handle local requests whose target agent is connecting to current regsitry instance</li>
 * <li>route requests to other registry instance whose target agent is connecting to</li>
 * </ul>
 * <p>
 * created at 2022/12/30
 *
 * @author xzchaoo
 */
@Service
public class BiStreamService {
  private static final Logger LOGGER = LoggerFactory.getLogger(BiStreamService.class);
  private static final String NO_CONNECTION_MSG =
      "No connection to holoinsight-agent found. This may be caused by a temporary network failure between agent and server";
  private static final Map<Integer, Object> defaultRespMap = new HashMap<>();

  static {
    defaultRespMap.put(BizTypes.ECHO, ByteString.EMPTY);
    defaultRespMap.put(BizTypes.INSPECT, InspectResponse.getDefaultInstance());
    defaultRespMap.put(BizTypes.LIST_FILES, ListFilesResponse.getDefaultInstance());
    defaultRespMap.put(BizTypes.PREVIEW_FILE, PreviewFileResponse.getDefaultInstance());
    defaultRespMap.put(BizTypes.DRY_RUN, DryRunResponse.getDefaultInstance());
    defaultRespMap.put(BizTypes.CHECK_TASK,
        CheckConfigTaskDistributionResponse.getDefaultInstance());
  }

  @Autowired
  private AgentStorage agentStorage;
  @Autowired
  private CommonThreadPools commonThreadPools;
  private BiStreamRegistryChannelCache cache;
  @Autowired
  private ProdDimService prodDimService;
  @Autowired
  private DaemonsetAgentService daemonsetAgentService;
  @Autowired
  private ServerStreamManager streamManager;

  @Autowired
  private RegistryProperties registryProperties;

  @PostConstruct
  public void init() {
    cache = new BiStreamRegistryChannelCache(commonThreadPools,
        registryProperties.getServer().getForInternal().getPort());
    cache.start();
  }

  @PreDestroy
  public void preDestroy() {
    cache.stop();
  }

  /**
   * proxySimple proxy a rpc request to agent. Request and response must be {@link ByteString}.
   *
   * @param agentId
   * @param bizType
   * @param payload
   * @return
   */
  public Mono<ByteString> proxySimple(String agentId, int bizType, ByteString payload) {
    Agent agent = agentStorage.get(agentId);
    if (agent == null) {
      return Mono.error(new IllegalStateException("no agent " + agentId));
    }

    // 1. 获取 Agent 正在和哪台 Registry 建联
    ConnectionInfo ci = agent.getJson().getConnectionInfo();
    if (ci == null || StringUtils.isEmpty(ci.getRegistry())) {
      return Mono.error(new IllegalStateException(NO_CONNECTION_MSG));
    }

    BiStreamProxyRequest proxyRequest = BiStreamProxyRequest.newBuilder() //
        .setAgentId(agentId) //
        .setBizType(bizType) //
        .setPayload(payload) //
        .build(); //

    return Mono.create(sink -> {
      // 2. 将请求转发给这台 Registry, 这台 Registry 去和 Agent 发请求
      Channel channel = cache.getChannel(ci.getRegistry());
      val stub = RegistryServiceForInternalGrpc.newStub(channel);

      stub.bistreamProxy(proxyRequest, new StreamObserver<BiStreamProxyResponse>() {
        @Override
        public void onNext(BiStreamProxyResponse resp) {
          if (bizType == 0 || resp.getType() == bizType + 1) {
            sink.success(resp.getPayload());
          } else {
            sink.error(new IllegalStateException(
                "error bizType:" + bizType + " payload:" + resp.getPayload()));
          }
        }

        @Override
        public void onError(Throwable throwable) {
          sink.error(throwable);
        }

        @Override
        public void onCompleted() {
          // nothing
        }
      });
    });
  }

  /**
   * proxy proxy a rpc request to agent. request and response must be {@link GeneratedMessageV3}.
   *
   * @param target target of dim
   * @param bizType rpc biz type
   * @param request rpc request
   * @param defaultResp default rpc response
   * @param o grpc observer
   * @param <RESP> response type
   */
  public <RESP extends GeneratedMessageV3> void proxyForDim(TargetIdentifier target, int bizType,
      GeneratedMessageV3 request, RESP defaultResp, StreamObserver<RESP> o) {

    String tenant = target.getTenant();
    String targetUk = target.getTargetUk();

    // TODO 将表信息带在 请求体里
    Map<String, Object> row = prodDimService.queryByDimId(tenant + "_server", targetUk, false);
    if (row == null) {
      throw new IllegalStateException("no metadata " + ProtoJsonUtils.toJson(target));
    }

    // TODO 其他类型想办法支持
    Object metaType = row.get("_type");
    String rpcAgentId;
    Map<String, String> header = null;
    if (MetaType.POD.name().equals(metaType)) {
      String hostIP = (String) row.get("hostIP");
      if (StringUtils.isEmpty(hostIP)) {
        throw new IllegalStateException("no hostIP for target " + row);
      }
      DaemonsetAgent da =
          daemonsetAgentService.getState().getAgents().get(new DaemonsetAgent.Key(tenant, hostIP));
      if (da == null) {
        String msg =
            String.format("%s: tenant=[%s] hostIP=[%s]", NO_CONNECTION_MSG, tenant, hostIP);
        throw new IllegalStateException(msg);
      }
      rpcAgentId = da.getHostAgentId();
      header = Collections.singletonMap("pod", JsonUtils.toJson(row));
    } else if (MetaType.VM.name().equals(metaType)) {
      rpcAgentId = (String) row.get("agentId");
    } else {
      throw new IllegalStateException("unsupported metaType " + metaType + " target=" + target);
    }

    request = appendRequestHeader(request, header);

    // 1. 获取 Agent 正在和哪台 Registry 建联
    String connectingRegistry = null;
    Agent agent = agentStorage.get(rpcAgentId);
    if (agent != null) {
      ConnectionInfo ci = agent.getJson().getConnectionInfo();
      if (ci != null) {
        connectingRegistry = ci.getRegistry();
      }
    }
    if (connectingRegistry == null) {
      RESP errResp = setRespHeader(defaultResp, RpcCodes.RESOURCE_NOT_FOUND, NO_CONNECTION_MSG);
      o.onNext(errResp);
      o.onCompleted();
      return;
    }
    String finalConnectingRegistry = connectingRegistry;

    // 2. 将请求转发给这台 Registry, 这台 Registry 去和 Agent 发请求
    Channel channel = cache.getChannel(connectingRegistry);
    // TODO channel 销毁
    val stub = RegistryServiceForInternalGrpc.newStub(channel);

    BiStreamProxyRequest proxyRequest = BiStreamProxyRequest.newBuilder().setAgentId(rpcAgentId) //
        .setBizType(bizType) //
        .setPayload(request.toByteString()) //
        .build(); //

    stub.bistreamProxy(proxyRequest, new StreamObserver<BiStreamProxyResponse>() {
      @Override
      public void onNext(BiStreamProxyResponse biStreamProxyResponse) {
        RESP resp = null;
        try {
          resp =
              (RESP) defaultResp.getParserForType().parseFrom(biStreamProxyResponse.getPayload());
        } catch (InvalidProtocolBufferException e) {
          throw new IllegalStateException("parseFrom error", e);
        }
        o.onNext(resp);
      }

      @Override
      public void onError(Throwable e) {
        LOGGER.error("stub.bistreamProxy error, connectingRegistry={}", finalConnectingRegistry, e);
        o.onError(Status.INTERNAL.withCause(e)
            .withDescription("internal proxy error: " + e.getMessage()).asRuntimeException());
      }

      @Override
      public void onCompleted() {
        o.onCompleted();
      }
    });
  }

  public void handleLocal(BiStreamProxyRequest request, StreamObserver<BiStreamProxyResponse> o) {
    Object defaultResp = defaultRespMap.get(request.getBizType());
    if (defaultResp == null) {
      throw new IllegalStateException("unsupported bizType " + request.getBizType());
    }

    ServerStream s = streamManager.get(request.getAgentId());

    if (s == null) {
      ByteString respPayload;
      if (request.getBizType() == BizTypes.ECHO) {
        respPayload = (ByteString) defaultResp;
      } else {
        respPayload = setRespHeader((GeneratedMessageV3) defaultResp, RpcCodes.RESOURCE_NOT_FOUND,
            NO_CONNECTION_MSG).toByteString();
      }
      BiStreamProxyResponse resp = BiStreamProxyResponse.newBuilder() //
          .setType(request.getBizType() + 1) //
          .setPayload(respPayload) //
          .build(); //
      o.onNext(resp);
      o.onCompleted();
      return;
    }

    s.rpc(request.getBizType(), request.getPayload()) //
        .timeout(Duration.ofSeconds(3)) //
        .subscribe(respCmd -> {
          if (request.getBizType() == BizTypes.ECHO) {
            BiStreamProxyResponse resp =
                BiStreamProxyResponse.newBuilder().setType(request.getBizType() + 1) //
                    .setPayload(respCmd.getData()) //
                    .build(); //
            o.onNext(resp);
            o.onCompleted();
            return;
          }

          GeneratedMessageV3 defaultResp0 = (GeneratedMessageV3) defaultResp;
          if (respCmd.getBizType() == request.getBizType() + 1) {
            try {
              // RESP resp = (RESP) defaultResp.getParserForType().parseFrom(respCmd.getData());
              BiStreamProxyResponse resp = BiStreamProxyResponse.newBuilder()
                  .setType(respCmd.getBizType()).setPayload(respCmd.getData()).build();
              o.onNext(resp);
              o.onCompleted();
            } catch (Throwable e) {
              throw new RuntimeException(e);
            }
          } else {
            GeneratedMessageV3 errorResp;
            if (respCmd.getBizType() == BizTypes.BIZ_ERROR) {
              errorResp =
                  setRespHeader(defaultResp0, RpcCodes.INTERNAL, respCmd.getData().toStringUtf8());
            } else {
              errorResp = setRespHeader(defaultResp0, RpcCodes.INTERNAL,
                  "internal error: wrong bizType " + respCmd.getBizType());
            }
            BiStreamProxyResponse resp =
                BiStreamProxyResponse.newBuilder().setType(request.getBizType() + 1) //
                    .setPayload(errorResp.toByteString()) //
                    .build(); //
            o.onNext(resp);
            o.onCompleted();
          }
        }, error -> {
          LOGGER.error("[{}] rpc error", getTraceId(request), error);
          BiStreamProxyResponse resp;
          if (request.getBizType() == BizTypes.ECHO) {
            resp = BiStreamProxyResponse.newBuilder().setType(BizTypes.ECHO) //
                .setPayload(ByteString.copyFromUtf8("internal error:" + error.getMessage())) //
                .build(); //
          } else {
            GeneratedMessageV3 errorResp =
                setRespHeader((GeneratedMessageV3) defaultResp, RpcCodes.INTERNAL, //
                    "internal error:" + error.getMessage());
            resp = BiStreamProxyResponse.newBuilder().setType(request.getBizType() + 1) //
                .setPayload(errorResp.toByteString()).build();
          }
          o.onNext(resp);
          o.onCompleted();
        });
  }

  private static CommonResponseHeader respHeader(int code, String message) {
    return CommonResponseHeader.newBuilder() //
        .setCode(code) //
        .setMessage(message) //
        .build();
  }

  private static <RESP extends GeneratedMessageV3> RESP setRespHeader(RESP defaultResp, int code,
      String message) {
    Descriptors.FieldDescriptor respHeaderFD =
        defaultResp.getDescriptorForType().getFields().get(0);
    return (RESP) defaultResp.toBuilder() //
        .setField(respHeaderFD, respHeader(code, message)) //
        .build(); //
  }

  private static <Req extends GeneratedMessageV3> Req appendRequestHeader(Req req,
      Map<String, String> headers) {
    if (headers == null || headers.isEmpty()) {
      return req;
    }
    Descriptors.FieldDescriptor headerFD = req.getDescriptorForType().getFields().get(0);
    CommonRequestHeader header = (CommonRequestHeader) req.getField(headerFD);
    CommonRequestHeader.Builder headerB = header.toBuilder().putAllHeader(headers);
    return (Req) req.toBuilder() //
        .setField(headerFD, headerB.build()) //
        .build(); //
  }

  private static String getTraceId(GeneratedMessageV3 request) {
    try {
      GeneratedMessageV3 header =
          (GeneratedMessageV3) request.getField(request.getDescriptorForType().getFields().get(0));
      return (String) header.getField(header.getDescriptorForType().getFields().get(1));
    } catch (ClassCastException e) {
      LOGGER.error("getTraceId error: {}", ProtoJsonUtils.toJson(request), e);
      return "";
    }
  }

}
