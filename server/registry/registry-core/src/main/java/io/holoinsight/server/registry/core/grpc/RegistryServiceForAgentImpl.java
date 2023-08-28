/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import com.xzchaoo.commons.basic.Ack;
import com.xzchaoo.commons.stat.StatAccumulator;
import com.xzchaoo.commons.stat.Stats;
import com.xzchaoo.commons.stat.StringsKey;

import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import io.holoinsight.server.common.GrpcUtils;
import io.holoinsight.server.common.JsonUtils;
import io.holoinsight.server.common.MetricsUtils;
import io.holoinsight.server.common.TrafficTracer;
import io.holoinsight.server.common.auth.ApikeyAuthService;
import io.holoinsight.server.common.auth.AuthErrorException;
import io.holoinsight.server.common.auth.AuthInfo;
import io.holoinsight.server.common.grpc.CommonRequestHeader;
import io.holoinsight.server.common.grpc.CommonResponseHeader;
import io.holoinsight.server.common.grpc.GenericRpcCommand;
import io.holoinsight.server.registry.core.agent.Agent;
import io.holoinsight.server.registry.core.agent.AgentEventService;
import io.holoinsight.server.registry.core.agent.AgentService;
import io.holoinsight.server.registry.core.agent.AgentStorage;
import io.holoinsight.server.registry.core.collecttarget.CollectTargetKey;
import io.holoinsight.server.registry.core.collecttarget.CollectTargetStorage;
import io.holoinsight.server.registry.core.dim.ProdDimService;
import io.holoinsight.server.registry.core.grpc.stream.ServerStreamManager;
import io.holoinsight.server.registry.core.lock.TargetProtector;
import io.holoinsight.server.registry.core.meta.DeltaSyncRequest;
import io.holoinsight.server.registry.core.meta.FullSyncRequest;
import io.holoinsight.server.registry.core.meta.MetaSyncService;
import io.holoinsight.server.registry.core.meta.Resource;
import io.holoinsight.server.registry.core.template.CollectRange;
import io.holoinsight.server.registry.core.template.CollectTemplate;
import io.holoinsight.server.registry.core.template.TemplateStorage;
import io.holoinsight.server.registry.grpc.agent.BasicConfig;
import io.holoinsight.server.registry.grpc.agent.CollectConfig;
import io.holoinsight.server.registry.grpc.agent.CollectConfigsBucket;
import io.holoinsight.server.registry.grpc.agent.CollectTarget;
import io.holoinsight.server.registry.grpc.agent.CollectTask;
import io.holoinsight.server.registry.grpc.agent.GetCollectTasksRequest;
import io.holoinsight.server.registry.grpc.agent.GetCollectTasksResponse;
import io.holoinsight.server.registry.grpc.agent.GetControlConfigsRequest;
import io.holoinsight.server.registry.grpc.agent.GetControlConfigsResponse;
import io.holoinsight.server.registry.grpc.agent.MetaSync;
import io.holoinsight.server.registry.grpc.agent.RegisterAgentRequest;
import io.holoinsight.server.registry.grpc.agent.RegisterAgentResponse;
import io.holoinsight.server.registry.grpc.agent.RegistryServiceForAgentGrpc;
import io.holoinsight.server.registry.grpc.agent.ReportEventRequest;
import io.holoinsight.server.registry.grpc.agent.SendAgentHeartbeatRequest;
import io.holoinsight.server.registry.grpc.agent.SendAgentHeartbeatResponse;
import reactor.core.publisher.Mono;

/**
 * <p>
 * created at 2022/3/1
 *
 * @author zzhb101
 */
@RegistryGrpcForAgent
@Component
public class RegistryServiceForAgentImpl
    extends RegistryServiceForAgentGrpc.RegistryServiceForAgentImplBase {
  private static final Logger LOGGER = LoggerFactory.getLogger(RegistryServiceForAgentImpl.class);
  private static final StatAccumulator<StringsKey> GRPC_AGENT_HEARTBEAT_STAT =
      MetricsUtils.SM1S.create("grpc.agent.heartbeat");
  private static final StatAccumulator<StringsKey> AGENT_PULLCONFIG_STAT =
      MetricsUtils.SM.create("agent.pullconfig.stat");
  @Autowired
  private ServerStreamManager serverStreamManager;
  @Autowired
  private AgentService agentService;
  @Autowired
  private GrpcConfig grpcConfig;
  @Autowired
  private CollectTargetStorage collectTargetStorage;
  @Autowired
  private TemplateStorage templateStorage;
  @Autowired
  private ProdDimService prodDimService;
  @Autowired
  private ApikeyAuthService apikeyAuthService;
  @Autowired
  private AgentStorage agentStorage;
  @Autowired
  private MetaSyncService metaSyncService;
  @Autowired
  private AgentEventService agentEventService;

  @Override
  public void ping(Empty request, StreamObserver<Empty> o) {
    o.onNext(Empty.getDefaultInstance());
    o.onCompleted();
  }

  @Override
  public void registerAgent(RegisterAgentRequest request, StreamObserver<RegisterAgentResponse> o) {

    authAndMap(request, request.getHeader(), o, a -> {
      RegisterAgentResponse resp = RegisterAgentResponse.newBuilder() //
          .setHeader(CommonResponseHeader.newBuilder() //
              .setCode(0) //
              .setMessage("OK") //
              .build()) //
          .setTenant(a.getTenant()) //
          .build(); //

      try {
        agentService.registerAgent(a, request);
      } catch (Throwable e) {
        LOGGER.error("register agent error", e);
        // grpc 返回的先不把错误信息带出去, 防止泄露一些敏感信息
        throw Status.INTERNAL.withDescription("register agent error").asRuntimeException();
      }
      return resp;
    });
  }

  @Override
  public void sendAgentHeartbeat(SendAgentHeartbeatRequest request,
      StreamObserver<SendAgentHeartbeatResponse> o) {
    GRPC_AGENT_HEARTBEAT_STAT.add(StringsKey.EMPTY, Stats.V_1);
    maybeEnableCompression(o);

    authAndMap(request, request.getHeader(), o, a -> {
      if (agentStorage.get(request.getAgentId()) == null) {
        throw Status.UNAUTHENTICATED.withDescription("agent not found").asRuntimeException();
      }
      SendAgentHeartbeatResponse resp = SendAgentHeartbeatResponse.newBuilder() //
          .setHeader(CommonResponseHeader.newBuilder() //
              .setCode(0) //
              .setMessage("OK") //
              .build()) //
          .build();
      agentService.updateAgentHeartbeat(a, request.getAgentId());
      return resp;
    });
  }

  @Override
  public void getControlConfigs(GetControlConfigsRequest request,
      StreamObserver<GetControlConfigsResponse> o) {
    maybeEnableCompression(o);
    authAndMap(request, request.getHeader(), o, a -> {
      GrpcConfig.Agent agentConfig = grpcConfig.getAgent();

      return GetControlConfigsResponse.newBuilder() //
          .setBasicConfig(BasicConfig.newBuilder() //
              .setHeartbeatIntervalSeconds(agentConfig.getHeartbeatInterval()) //
              .setReconnectInterval(agentConfig.getReconnectInterval()) //
              .setSyncConfigsIntervalSeconds(agentConfig.getSyncConfigInterval()) //
              .build()) //
          .build();
    });
  }

  @Override
  public void getCollectTasks(GetCollectTasksRequest request,
      StreamObserver<GetCollectTasksResponse> o) {
    maybeEnableCompression(o);
    authAndMap(request, request.getHeader(), o, a -> getCollectTasks0(request));
  }

  @Override
  public StreamObserver<GenericRpcCommand> biStreams(StreamObserver<GenericRpcCommand> o) {
    // 这个性能差不多也够了
    // 单个连接的情况下
    // 不开压缩
    // count=20000 concurrency=8 cost=1716
    // 开压缩
    // count=20000 concurrency=8 cost=2444
    maybeEnableCompression(o);
    // TODO auth
    return serverStreamManager.createServerStream(o).getReader();
  }

  @Override
  public void metaFullSync(MetaSync.FullSyncRequest request, StreamObserver<Empty> o) {
    // TODO 先auth
    FullSyncRequest req;
    if (StringUtils.isNotEmpty(request.getTemp())) {
      req = JsonUtils.fromJson(request.getTemp(), FullSyncRequest.class);
    } else {
      req = new FullSyncRequest();
      req.setApikey(request.getHeader().getApikey());
      req.setWorkspace(request.getWorkspace());
      req.setCluster(request.getCluster());
      req.setType(request.getType());
      List<Resource> resources = request.getResourceList() //
          .stream() //
          .map(RegistryServiceForAgentImpl::convertToResourceModel) //
          .collect(Collectors.toList()); //
      req.setResources(resources);
    }
    metaSyncService.handleFull(req);
    o.onNext(Empty.getDefaultInstance());
    o.onCompleted();
  }

  @Override
  public void metaDeltaSync(MetaSync.DeltaSyncRequest request, StreamObserver<Empty> o) {
    authAndMap(request, request.getHeader(), o, ai -> {
      DeltaSyncRequest req = new DeltaSyncRequest();
      req.setApikey(request.getHeader().getApikey());
      req.setWorkspace(request.getWorkspace());
      req.setCluster(request.getCluster());
      req.setType(request.getType());
      req.setAdd(request.getAddList() //
          .stream() //
          .map(RegistryServiceForAgentImpl::convertToResourceModel) //
          .collect(Collectors.toList()));
      req.setDel(request.getDelList() //
          .stream() //
          .map(RegistryServiceForAgentImpl::convertToResourceModel) //
          .collect(Collectors.toList()));
      metaSyncService.handleDelta(req);
      return Empty.getDefaultInstance();
    });
  }

  @Override
  public void reportEvents(ReportEventRequest request, StreamObserver<Empty> o) {
    authAndMap(request, request.getHeader(), o, ai -> { //
      agentEventService.reportEvents(ai, request); //
      return Empty.getDefaultInstance(); //
    }); //
  }

  private <R> void authAndFlatMap(CommonRequestHeader header, StreamObserver<R> o,
      Function<? super AuthInfo, ? extends Mono<? extends R>> transformer) {
    apikeyAuthService.get(header.getApikey()) //
        .flatMap(transformer) //
        .subscribe(o::onNext, o::onError, o::onCompleted); //
  }

  private <R> void authAndMap(Object request, CommonRequestHeader header, StreamObserver<R> o,
      Function<? super AuthInfo, ? extends R> transformer) {
    TrafficTracer tt = TrafficTracer.KEY.get();
    apikeyAuthService.get(header.getApikey()) //
        .map(ai -> { //
          tt.setTenant(ai.getTenant()); //
          return transformer.apply(ai);
        }) //
        .subscribe(o::onNext, error -> {
          if (error instanceof StatusRuntimeException) {
            o.onError(error);
            return;
          }
          if (error instanceof StatusException) {
            o.onError(error);
            return;
          }

          if (error instanceof AuthErrorException) {
            o.onError(
                Status.UNAUTHENTICATED.withDescription(error.getMessage()).asRuntimeException());
          } else {
            LOGGER.error("internal error, request={}", request, error);
            o.onError(Status.INTERNAL.withDescription(error.getMessage()).asRuntimeException());
          }
        }, o::onCompleted); //
  }

  private GetCollectTasksResponse getCollectTasks0(GetCollectTasksRequest request) {
    Agent agent = agentStorage.get(request.getAgentId());
    if (agent == null) {
      throw new StatusRuntimeException(
          Status.UNAUTHENTICATED.withDescription("no agent with id " + request.getAgentId()));
    }

    List<CollectTargetKey> keys;
    Ack ack = TargetProtector.read();
    try {
      Set<CollectTargetKey> set = collectTargetStorage.getKeysByAgent(request.getAgentId());
      keys = new ArrayList<>(set);
    } finally {
      ack.ack();
    }
    // 我们要计算状态, 需要一种算法, 对于给定相同的集合算出相同的hash
    // 要求: 当"集合的元素顺序可能是变化的, 但内容是一样的" 的时候, 该hash值也要相同
    // 一个办法就是每次都对集合进行排序后再使用普通的hash算法

    keys.sort(Comparator.comparingLong(CollectTargetKey::getTemplateId)
        .thenComparing(CollectTargetKey::getDimId));

    GetCollectTasksResponse.Builder respB = GetCollectTasksResponse.newBuilder();

    CommonResponseHeader header = CommonResponseHeader.newBuilder() //
        .setCode(0) //
        .setMessage("OK") //
        .build(); //

    respB.setHeader(header);

    // TODO 此处要先build成targets
    int count = 0;
    int missDim = 0;
    {
      Set<Long> processedTemplates = new HashSet<>();
      Set<String> processedDims = new HashSet<>();
      // Set<String> processedAgentIds = new HashSet<>();
      Hasher h = Hashing.murmur3_32().newHasher();

      CollectConfigsBucket.Builder bb = CollectConfigsBucket.newBuilder();

      for (CollectTargetKey key : keys) {
        CollectTemplate t = templateStorage.get(key.getTemplateId());
        if (t == null) {
          // 临时错误, 理论重试过段时间可以解决
          throw new IllegalStateException("no template " + key.getTemplateId());
        }
        String tenant = t.getTenant();

        if (processedTemplates.add(key.getTemplateId())) {
          String type = t.getType();
          if (type == null) {
            type = "SQLTASK";
          }
          CollectConfig b = CollectConfig.newBuilder() //
              .setKey(t.getTableName()) //
              .setVersion(t.getVersion()) //
              .setContent(ByteString.copyFromUtf8(t.getJson())) //
              .setType(type) //
              .build();
          respB.putCollectConfigs(t.getTableName(), b);
        }

        if (processedDims.add(key.getDimId())) {
          CollectTarget.Builder builder = CollectTarget.newBuilder() //
              // 这个采集目标的唯一标识
              .setKey(key.getDimId()) //
              .setType("localhost");

          // TODO 解释
          if (t.getExecutorSelector().getType().equals(CollectRange.CENTRAL)) {
            builder.putMeta("tenant", tenant);
          }

          if (key.getDimId().startsWith("dim2:")) {
            Map<String, Object> dim =
                prodDimService.queryByDimId(t.getCollectRange().getCloudmonitor().getTable(),
                    key.getDimId().substring("dim2:".length()), false);
            if (dim == null) {
              ++missDim;
              continue;
            }

            String dimType = (String) dim.get("_type");
            // avoid throwing NPE when switch
            if (dimType == null) {
              dimType = "";
            }
            switch (dimType) {
              case "POD":
                builder.setType("pod");
                builder.putMeta("type", "pod");
                builder.putMeta("ip", (String) dim.getOrDefault("ip", ""));
                builder.putMeta("hostname", (String) dim.getOrDefault("hostname", ""));
                builder.putMeta("app", (String) dim.getOrDefault("app", ""));
                builder.putMeta("namespace", (String) dim.getOrDefault("namespace", ""));
                builder.putMeta("pod", (String) dim.getOrDefault("name", ""));
                builder.putMeta("hostIP", (String) dim.getOrDefault("hostIP", ""));
                break;
              case "node_tenant":
                builder.setType("ob_node_tenant");
                dim.forEach((k, v) -> {
                  if (!k.startsWith("_") && v != null) {
                    builder.putMeta(k, v.toString());
                  }
                });
                break;
              default:
                builder.setType(dimType);
                dim.forEach((k, v) -> {
                  if (!k.startsWith("_") && v != null) {
                    builder.putMeta(k, v.toString());
                  }
                });
                break;
            }
          }
          CollectTarget pbCt = builder.build(); //
          respB.putCollectTargets(key.getDimId(), pbCt);
        }

        count++;
        {
          // TODO 把版本号放进去
          // TODO 考虑到商业化版本没有那么多配置, 可以把配置ids带在请求头里
          h.putLong(key.getTemplateId());
          h.putString(t.getVersion(), StandardCharsets.UTF_8);
          h.putString(key.getDimId(), StandardCharsets.UTF_8);

          bb.addCollectTasks(CollectTask.newBuilder() //
              .setKey(t.getTableName() + "/" + key.getDimId()) //
              .setCollectConfigKey(t.getTableName()) //
              .setCollectTargetKey(key.getDimId()) //
              .build()); //
        }

      }

      String state = h.hash().toString();
      bb.setState(state);
      respB.putBuckets("1", bb.build());
    }
    LOGGER.info("agent={} keys={} missDim={} tasks={}", request.getAgentId(), keys.size(), missDim,
        count);
    AGENT_PULLCONFIG_STAT.add(StringsKey.of(agent.getTenant()), new long[] {1, count});
    return respB.build();
  }

  private void maybeEnableCompression(StreamObserver<?> o) {
    GrpcUtils.setCompressionGzip(o);
  }

  private static Resource convertToResourceModel(MetaSync.Resource pb) {
    Resource r = new Resource();

    r.setName(pb.getName());
    r.setNamespace(pb.getNamespace());
    r.setLabels(pb.getLabelsMap());
    r.setAnnotations(pb.getAnnotationsMap());
    r.setApp(pb.getApp());
    r.setIp(pb.getIp());
    r.setHostname(pb.getHostname());
    r.setHostIP(pb.getHostIP());

    r.setSpec(pb.getSpecMap());

    return r;
  }
}
