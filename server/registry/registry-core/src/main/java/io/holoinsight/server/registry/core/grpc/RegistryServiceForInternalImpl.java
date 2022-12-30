/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.grpc.stub.StreamObserver;
import io.holoinsight.server.common.GrpcUtils;
import io.holoinsight.server.common.grpc.CommonResponseHeader;
import io.holoinsight.server.common.grpc.GenericData;
import io.holoinsight.server.common.grpc.GenericDataBatch;
import io.holoinsight.server.registry.core.cluster.Handler;
import io.holoinsight.server.registry.core.cluster.HandlerRegistry;
import io.holoinsight.server.registry.core.grpc.stream.ServerStream;
import io.holoinsight.server.registry.core.grpc.stream.ServerStreamManager;
import io.holoinsight.server.registry.core.grpc.streambiz.BizTypes;
import io.holoinsight.server.registry.grpc.internal.BiStreamProxyRequest;
import io.holoinsight.server.registry.grpc.internal.BiStreamProxyResponse;
import io.holoinsight.server.registry.grpc.internal.RegistryServiceForInternalGrpc;
import io.holoinsight.server.registry.grpc.prod.InspectResponse;
import io.holoinsight.server.registry.grpc.prod.ListFilesResponse;
import io.holoinsight.server.registry.grpc.prod.PreviewFileResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Empty;
import com.google.protobuf.GeneratedMessageV3;

/**
 * <p>created at 2022/3/12
 *
 * @author zzhb101
 */
public class RegistryServiceForInternalImpl extends RegistryServiceForInternalGrpc.RegistryServiceForInternalImplBase {
    private static final Logger                           LOGGER         = LoggerFactory.getLogger(RegistryServiceForInternalImpl.class);
    private static final Map<Integer, GeneratedMessageV3> defaultRespMap = new HashMap<>();

    static {
        defaultRespMap.put(BizTypes.INSPECT, InspectResponse.getDefaultInstance());
        defaultRespMap.put(BizTypes.LIST_FILES, ListFilesResponse.getDefaultInstance());
        defaultRespMap.put(BizTypes.PREVIEW_FILE, PreviewFileResponse.getDefaultInstance());
    }

    // TODO
    private final HandlerRegistry     registry;
    @Autowired
    private       ServerStreamManager manager;

    public RegistryServiceForInternalImpl(HandlerRegistry registry) {
        this.registry = Objects.requireNonNull(registry);
    }

    @Override
    public void sendEvents(GenericDataBatch batch, StreamObserver<Empty> o) {
        GrpcUtils.setCompressionGzip(o);
        for (GenericData data : batch.getDataList()) {
            Handler h = registry.get(data.getType());
            if (h != null) {
                h.handle(data);
            }
        }
        o.onNext(Empty.getDefaultInstance());
        o.onCompleted();
    }

    @Override
    public void bistreamProxy(BiStreamProxyRequest request, StreamObserver<BiStreamProxyResponse> o) {
        GeneratedMessageV3 defaultResp = defaultRespMap.get(request.getBizType());
        if (defaultResp == null) {
            throw new IllegalStateException("invalid bizType " + request.getBizType());
        }
        proxy0(request, defaultResp, o);
    }

    public <RESP extends GeneratedMessageV3> void proxy0(BiStreamProxyRequest request, RESP defaultResp, StreamObserver<BiStreamProxyResponse> o) {
        ServerStream s = manager.get(request.getAgentId());

        if (s == null) {
            String msg = "No connection to holoinsight-agent found. This may be caused by a temporary network failure between agent and server";
            RESP errResp = setRespHeader(defaultResp, RpcCodes.RESOURCE_NOT_FOUND, msg); //
            BiStreamProxyResponse resp = BiStreamProxyResponse.newBuilder() //
                .setType(request.getBizType() + 1) //
                .setPayload(errResp.toByteString()) //
                .build(); //
            o.onNext(resp);
            o.onCompleted();
            return;
        }

        GeneratedMessageV3 requestBak = request;
        s.rpc(request.getBizType(), request.getPayload()) //
            .timeout(Duration.ofSeconds(3)) //
            .subscribe(respCmd -> {
                if (respCmd.getBizType() == request.getBizType() + 1) {
                    try {
                        // RESP resp = (RESP) defaultResp.getParserForType().parseFrom(respCmd.getData());
                        BiStreamProxyResponse resp = BiStreamProxyResponse.newBuilder().setType(respCmd.getBizType()).setPayload(respCmd.getData())
                            .build();
                        o.onNext(resp);
                        o.onCompleted();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    RESP errorResp;
                    if (respCmd.getBizType() == BizTypes.BIZ_ERROR) {
                        errorResp = setRespHeader(defaultResp, RpcCodes.INTERNAL, respCmd.getData().toStringUtf8());
                    } else {
                        errorResp = setRespHeader(defaultResp, RpcCodes.INTERNAL, "internal error: wrong bizType " + respCmd.getBizType());
                    }
                    BiStreamProxyResponse resp = BiStreamProxyResponse.newBuilder().setType(request.getBizType() + 1).setPayload(
                        errorResp.toByteString()).build();
                    o.onNext(resp);
                    o.onCompleted();
                }
            }, error -> {
                LOGGER.error("[{}] rpc error", getTraceId(requestBak), error);
                RESP errorResp = setRespHeader(defaultResp, RpcCodes.INTERNAL, "internal error:" + error.getMessage());
                BiStreamProxyResponse resp = BiStreamProxyResponse.newBuilder().setType(request.getBizType() + 1).setPayload(errorResp.toByteString())
                    .build();
                o.onNext(resp);
                o.onCompleted();
            });
    }

    private static <RESP extends GeneratedMessageV3> RESP setRespHeader(RESP defaultResp, int code, String message) {
        Descriptors.FieldDescriptor respHeaderFD = defaultResp.getDescriptorForType().getFields().get(0);
        return (RESP) defaultResp.toBuilder() //
            .setField(respHeaderFD, respHeader(code, message)) //
            .build(); //
    }

    private static CommonResponseHeader respHeader(int code, String message) {
        return CommonResponseHeader.newBuilder() //
            .setCode(code) //
            .setMessage(message) //
            .build();
    }

    private static String getTraceId(GeneratedMessageV3 request) {
        GeneratedMessageV3 header = (GeneratedMessageV3) request.getField(request.getDescriptorForType().getFields().get(0));
        return (String) header.getField(header.getDescriptorForType().getFields().get(1));
    }

}
