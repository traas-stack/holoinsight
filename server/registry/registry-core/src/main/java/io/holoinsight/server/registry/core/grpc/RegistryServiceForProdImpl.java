/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc;

import java.util.Collections;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import io.holoinsight.server.common.JsonUtils;
import io.holoinsight.server.common.grpc.CommonRequestHeader;
import io.holoinsight.server.common.grpc.CommonResponseHeader;
import io.holoinsight.server.common.threadpool.CommonThreadPools;
import io.holoinsight.server.meta.facade.model.MetaType;
import io.holoinsight.server.registry.core.agent.Agent;
import io.holoinsight.server.registry.core.agent.AgentStorage;
import io.holoinsight.server.registry.core.agent.ConnectionInfo;
import io.holoinsight.server.registry.core.agent.DaemonsetAgent;
import io.holoinsight.server.registry.core.agent.DaemonsetAgentService;
import io.holoinsight.server.registry.core.dim.ProdDimService;
import io.holoinsight.server.registry.core.grpc.streambiz.BizTypes;
import io.holoinsight.server.registry.grpc.internal.BiStreamProxyRequest;
import io.holoinsight.server.registry.grpc.internal.BiStreamProxyResponse;
import io.holoinsight.server.registry.grpc.internal.RegistryServiceForInternalGrpc;
import io.holoinsight.server.registry.grpc.prod.InspectRequest;
import io.holoinsight.server.registry.grpc.prod.InspectResponse;
import io.holoinsight.server.registry.grpc.prod.ListFilesRequest;
import io.holoinsight.server.registry.grpc.prod.ListFilesResponse;
import io.holoinsight.server.registry.grpc.prod.PreviewFileRequest;
import io.holoinsight.server.registry.grpc.prod.PreviewFileResponse;
import io.holoinsight.server.registry.grpc.prod.RegistryServiceForProdGrpc;
import io.holoinsight.server.registry.grpc.prod.TargetIdentifier;
import lombok.val;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * <p>created at 2022/3/11
 *
 * @author zzhb101
 */
@Service
@RegistryGrpcForProd
public class RegistryServiceForProdImpl extends RegistryServiceForProdGrpc.RegistryServiceForProdImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegistryServiceForProdImpl.class);

    @Autowired
    private ProdDimService               prodDimService;
    @Autowired
    private DaemonsetAgentService        daemonsetAgentService;
    @Autowired
    private AgentStorage                 agentStorage;
    @Autowired
    private CommonThreadPools            commonThreadPools;
    private BiStreamRegistryChannelCache cache;

    @PostConstruct
    public void init() {
        cache = new BiStreamRegistryChannelCache(commonThreadPools);
        cache.start();
    }

    @PreDestroy
    public void preDestroy() {
        cache.stop();
    }

    @Override
    public void listFiles(ListFilesRequest request, StreamObserver<ListFilesResponse> o) {
        proxy(request.getTarget(), BizTypes.LIST_FILES, request, ListFilesResponse.getDefaultInstance(), o);
    }

    @Override
    public void previewFile(PreviewFileRequest request, StreamObserver<PreviewFileResponse> o) {
        proxy(request.getTarget(), BizTypes.PREVIEW_FILE, request, PreviewFileResponse.getDefaultInstance(), o);
    }

    @Override
    public void inspect(InspectRequest request, StreamObserver<InspectResponse> o) {
        proxy(request.getTarget(), BizTypes.INSPECT, request, InspectResponse.getDefaultInstance(), o);
    }

    public <RESP extends GeneratedMessageV3> void proxy(TargetIdentifier target, int bizType, GeneratedMessageV3 request, RESP defaultResp,
        StreamObserver<RESP> o) {

        String tenant = target.getTenant();
        String targetUk = target.getTargetUk();

        // TODO 将表信息带在 请求体里
        Map<String, Object> row = prodDimService.queryByDimId(tenant + "_server", targetUk, false);
        if (row == null) {
            throw new IllegalStateException("no dim data " + target);
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
            DaemonsetAgent da = daemonsetAgentService.getState().getAgents().get(new DaemonsetAgent.Key(tenant, hostIP));
            if (da == null) {
                String msg = String.format("no daemonset for [%s/%s]", tenant, hostIP);
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
        LOGGER.info("proxy row={} header={}", row, header);

        // TODO
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
            String msg = "No connection to holoinsight-agent found. This may be caused by a temporary network failure between agent and server";
            RESP errResp = setRespHeader(defaultResp, RpcCodes.RESOURCE_NOT_FOUND, msg);
            o.onNext(errResp);
            o.onCompleted();
            return;
        }

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
                    resp = (RESP) defaultResp.getParserForType().parseFrom(biStreamProxyResponse.getPayload());
                } catch (InvalidProtocolBufferException e) {
                    throw new IllegalStateException("parseFrom error", e);
                }
                o.onNext(resp);
            }

            @Override
            public void onError(Throwable throwable) {
                o.onError(throwable);
            }

            @Override
            public void onCompleted() {
                o.onCompleted();
            }
        });
    }

    private static CommonResponseHeader respHeader(int code, String message) {
        return CommonResponseHeader.newBuilder() //
            .setCode(code) //
            .setMessage(message) //
            .build();
    }

    private static <RESP extends GeneratedMessageV3> RESP setRespHeader(RESP defaultResp, int code, String message) {
        Descriptors.FieldDescriptor respHeaderFD = defaultResp.getDescriptorForType().getFields().get(0);
        return (RESP) defaultResp.toBuilder() //
            .setField(respHeaderFD, respHeader(code, message)) //
            .build(); //
    }

    private static <Req extends GeneratedMessageV3> Req appendRequestHeader(Req req, Map<String, String> headers) {
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
        GeneratedMessageV3 header = (GeneratedMessageV3) request.getField(request.getDescriptorForType().getFields().get(0));
        return (String) header.getField(header.getDescriptorForType().getFields().get(1));
    }

}
