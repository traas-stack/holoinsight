/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.grpc;

import io.grpc.stub.StreamObserver;
import io.holoinsight.server.common.auth.ApikeyAuthService;
import io.holoinsight.server.common.auth.AuthInfo;
import io.holoinsight.server.gateway.core.storage.MetricStorage;
import io.holoinsight.server.gateway.core.utils.StatUtils;
import io.holoinsight.server.gateway.grpc.GatewayServiceGrpc;
import io.holoinsight.server.gateway.grpc.GetControlConfigsRequest;
import io.holoinsight.server.gateway.grpc.GetControlConfigsResponse;
import io.holoinsight.server.gateway.grpc.WriteMetricsRequestV1;
import io.holoinsight.server.gateway.grpc.WriteMetricsRequestV4;
import io.holoinsight.server.gateway.grpc.WriteMetricsResponse;
import io.holoinsight.server.gateway.grpc.common.CommonResponseHeader;
import reactor.core.publisher.Mono;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.protobuf.Empty;
import com.xzchaoo.commons.stat.StringsKey;

/**
 * <p>created at 2022/2/25
 *
 * @author sw1136562366
 */
@GatewayGrpcForAgent
@Component
public class GatewayGrpcServiceImpl extends GatewayServiceGrpc.GatewayServiceImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayGrpcServiceImpl.class);

    @Autowired
    private MetricStorage     metricStorage;
    @Autowired
    private ApikeyAuthService apikeyAuthService;

    /** {@inheritDoc} */
    @Override
    public void ping(Empty request, StreamObserver<Empty> o) {
        o.onNext(Empty.getDefaultInstance());
        o.onCompleted();
    }

    /** {@inheritDoc} */
    @Override
    public void getControlConfigs(GetControlConfigsRequest request, StreamObserver<GetControlConfigsResponse> o) {
        o.onNext(GetControlConfigsResponse.getDefaultInstance());
        o.onCompleted();
    }

    /** {@inheritDoc} */
    @Override
    public void writeMetricsV1(WriteMetricsRequestV1 request, StreamObserver<WriteMetricsResponse> o) {
        // TODO 异步处理?
        // request.getSync()

        String apikey = request.getHeader().getApikey();

        // TODO traffic
        // TrafficTracer tt = TrafficTracer.KEY.get();
        String centralTenant = request.getHeader().getExtensionMap().get("tenant");
        String centralAgentId = request.getHeader().getExtensionMap().get("agentID");

        Mono<AuthInfo> authMono = apikeyAuthService.get(apikey, true);
        TrafficTracer tt = TrafficTracer.KEY.get();
        authMono.doOnNext(authInfo -> recordTraffic(authInfo, tt)) //
            .map(authInfo -> {
                // TODO 理论上 registry 这里需要校验合法性 centralAgentId
                if (StringUtils.isNotEmpty(centralTenant)) {
                    AuthInfo ai = new AuthInfo();
                    ai.setTenant(centralTenant);
                    return ai;
                }
                return authInfo;
            }).flatMap(authInfo -> { //

                return metricStorage.write(authInfo, request);
            }).subscribe(null, error -> {
                LOGGER.error("write error", error);
                // TODO 代码重复
                // TODO 识别error, 并返回不同的 code & message
                if (StringUtils.contains(error.getMessage(), "apikey")) {
                    WriteMetricsResponse resp = WriteMetricsResponse.newBuilder() //
                        .setHeader(CommonResponseHeader.newBuilder() //
                            .setCode(Codes.UNAUTHENTICATED) //
                            .setMessage(error.getMessage()) //
                            .build())
                        .build();
                    o.onNext(resp);
                    o.onCompleted();
                } else {
                    WriteMetricsResponse resp = WriteMetricsResponse.newBuilder() //
                        .setHeader(CommonResponseHeader.newBuilder() //
                            .setCode(Codes.INTERNAL_ERROR) //
                            .setMessage(error.getMessage()) //
                            .build())
                        .build();
                    o.onNext(resp);
                    o.onCompleted();
                }
            }, () -> {
                // 成功
                o.onNext(WriteMetricsResponse.newBuilder() //
                    .setHeader(CommonResponseHeader.newBuilder() //
                        .setCode(Codes.OK) //
                        .setMessage("OK") //
                        .build())
                    .build());
                o.onCompleted();
            });
    }

    /** {@inheritDoc} */
    @Override
    public void writeMetricsV4(WriteMetricsRequestV4 request, StreamObserver<WriteMetricsResponse> o) {
        // TODO auth

        // 1. 对 apikey 做鉴权, 可能是同步或异步的
        // 2. 计量
        // 3. 如果请求类型为独立模式, 那么自己作为一批立即写入ceresdb, 并同步返回写入结果, 要小心超时设置
        // 4. 如果请求是异步模式, 那么攒批写入ceresdb, 此时顺序完全无所谓了, 成功失败也无所谓了, 当然那服务端会尽量统计一下

        String apikey = request.getHeader().getApikey();

        TrafficTracer tt = TrafficTracer.KEY.get();

        apikeyAuthService.get(apikey, true) //
            .doOnNext(authInfo -> recordTraffic(authInfo, tt)) //
            .flatMap(authInfo -> { //
                return metricStorage.write(authInfo, request);
            }).subscribe(null, error -> {
                LOGGER.error("write error", error);

                // TODO 识别error, 并返回不同的 code & message
                WriteMetricsResponse resp = WriteMetricsResponse.newBuilder() //
                    .setHeader(CommonResponseHeader.newBuilder() //
                        .setCode(Codes.UNAUTHENTICATED) //
                        .setMessage("UNAUTHENTICATED") //
                        .build())
                    .build();
                o.onNext(resp);
                o.onCompleted();
            }, () -> {
                // 成功
                o.onNext(WriteMetricsResponse.newBuilder() //
                    .setHeader(CommonResponseHeader.newBuilder() //
                        .setCode(Codes.OK) //
                        .setMessage("OK") //
                        .build())
                    .build());
                o.onCompleted();
            });
    }

    private void recordTraffic(AuthInfo authInfo, TrafficTracer tt) {
        if (tt == null) {
            return;
        }
        StatUtils.GRPC_TRAFFIC.add(StringsKey.of(authInfo.getTenant()), //
            new long[] {
                1, //
                tt.getInboundWireSize(), // TCP入流量
                tt.getInboundUncompressedSize() // 解压后入流量
            });
    }
}
