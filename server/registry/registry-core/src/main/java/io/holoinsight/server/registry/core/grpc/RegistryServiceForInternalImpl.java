/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.grpc.stub.StreamObserver;
import io.holoinsight.server.common.GrpcUtils;
import io.holoinsight.server.common.grpc.GenericData;
import io.holoinsight.server.common.grpc.GenericDataBatch;
import io.holoinsight.server.registry.core.cluster.Handler;
import io.holoinsight.server.registry.core.cluster.HandlerRegistry;
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

import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import com.google.protobuf.GeneratedMessageV3;

/**
 * <p>created at 2022/3/12
 *
 * @author xzchaoo
 */
public class RegistryServiceForInternalImpl extends RegistryServiceForInternalGrpc.RegistryServiceForInternalImplBase {
    private static final Map<Integer, Object> defaultRespMap = new HashMap<>();

    static {
        defaultRespMap.put(BizTypes.ECHO, ByteString.EMPTY);
        defaultRespMap.put(BizTypes.INSPECT, InspectResponse.getDefaultInstance());
        defaultRespMap.put(BizTypes.LIST_FILES, ListFilesResponse.getDefaultInstance());
        defaultRespMap.put(BizTypes.PREVIEW_FILE, PreviewFileResponse.getDefaultInstance());
    }

    // TODO
    private final HandlerRegistry     registry;
    @Autowired
    private       BiStreamService     biStreamService;

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
        Object defaultResp = defaultRespMap.get(request.getBizType());
        if (defaultResp == null) {
            throw new IllegalStateException("invalid bizType " + request.getBizType());
        }
        proxy0(request, defaultResp, o);
    }

    public void proxy0(BiStreamProxyRequest request, Object defaultResp, StreamObserver<BiStreamProxyResponse> o) {
        biStreamService.proxy0(request, defaultResp, o);
    }

}
