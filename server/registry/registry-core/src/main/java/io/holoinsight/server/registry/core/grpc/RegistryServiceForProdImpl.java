/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc;

import io.grpc.stub.StreamObserver;
import io.holoinsight.server.common.grpc.CommonResponseHeader;
import io.holoinsight.server.registry.core.grpc.streambiz.BizTypes;
import io.holoinsight.server.registry.grpc.prod.InspectRequest;
import io.holoinsight.server.registry.grpc.prod.InspectResponse;
import io.holoinsight.server.registry.grpc.prod.ListFilesRequest;
import io.holoinsight.server.registry.grpc.prod.ListFilesResponse;
import io.holoinsight.server.registry.grpc.prod.PreviewFileRequest;
import io.holoinsight.server.registry.grpc.prod.PreviewFileResponse;
import io.holoinsight.server.registry.grpc.prod.RegistryServiceForProdGrpc;
import io.holoinsight.server.registry.grpc.prod.TargetIdentifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessageV3;

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
    private BiStreamService biStreamService;

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
        biStreamService.handleProxy(target, bizType, request, defaultResp, o);
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

}
