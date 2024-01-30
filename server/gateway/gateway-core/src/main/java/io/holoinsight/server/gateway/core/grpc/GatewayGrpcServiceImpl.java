/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.grpc;

import com.google.common.collect.Maps;
import com.google.protobuf.Empty;
import com.xzchaoo.commons.stat.StringsKey;
import io.grpc.stub.StreamObserver;
import io.holoinsight.server.common.TrafficTracer;
import io.holoinsight.server.common.auth.ApikeyAuthService;
import io.holoinsight.server.common.auth.AuthInfo;
import io.holoinsight.server.extension.MetricStorage;
import io.holoinsight.server.extension.model.WriteMetricsParam;
import io.holoinsight.server.gateway.core.utils.StatUtils;
import io.holoinsight.server.gateway.grpc.DataNode;
import io.holoinsight.server.gateway.grpc.GatewayServiceGrpc;
import io.holoinsight.server.gateway.grpc.GetControlConfigsRequest;
import io.holoinsight.server.gateway.grpc.GetControlConfigsResponse;
import io.holoinsight.server.gateway.grpc.Point;
import io.holoinsight.server.gateway.grpc.WriteMetricsRequestV1;
import io.holoinsight.server.gateway.grpc.WriteMetricsRequestV4;
import io.holoinsight.server.gateway.grpc.WriteMetricsResponse;
import io.holoinsight.server.gateway.grpc.common.CommonResponseHeader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * created at 2022/2/25
 *
 * @author sw1136562366
 */
@GatewayGrpcForAgent
@Component
public class GatewayGrpcServiceImpl extends GatewayServiceGrpc.GatewayServiceImplBase {
  private static final Logger LOGGER = LoggerFactory.getLogger(GatewayGrpcServiceImpl.class);

  @Autowired
  private MetricStorage metricStorage;

  @Autowired
  private ApikeyAuthService apikeyAuthService;

  @Autowired
  private GatewayHookManager gatewayHookManager;

  @Autowired
  private DetailsStorageService detailsStorageService;

  /** {@inheritDoc} */
  @Override
  public void ping(Empty request, StreamObserver<Empty> o) {
    o.onNext(Empty.getDefaultInstance());
    o.onCompleted();
  }

  /** {@inheritDoc} */
  @Override
  public void getControlConfigs(GetControlConfigsRequest request,
      StreamObserver<GetControlConfigsResponse> o) {
    o.onNext(GetControlConfigsResponse.getDefaultInstance());
    o.onCompleted();
  }

  /** {@inheritDoc} */
  @Override
  public void writeMetricsV1(WriteMetricsRequestV1 request,
      StreamObserver<WriteMetricsResponse> o) {

    String centralTenant = request.getHeader().getExtensionMap().get("tenant");
    request.getHeader().getExtensionMap().get("agentID");
    TrafficTracer tt = TrafficTracer.KEY.get();

    apikeyAuthService.get(request.getHeader().getApikey(), true) //
        .doOnNext(authInfo -> recordTraffic(authInfo, tt)) //
        .map(authInfo -> {
          // TODO 理论上 registry 这里需要校验合法性 centralAgentId
          if (StringUtils.isNotEmpty(centralTenant)) {
            AuthInfo ai = new AuthInfo();
            ai.setTenant(centralTenant);
            return ai;
          }
          return authInfo;
        }).flatMap(authInfo -> { //
          gatewayHookManager.writeMetricsV1(authInfo, request);
          return metricStorage.write(convertToWriteMetricsParam(authInfo, request));
        }).subscribe(null, error -> handleError(error, o), () -> handleSuccess(o));
  }

  /** {@inheritDoc} */
  @Override
  public void writeMetricsV4(WriteMetricsRequestV4 request,
      StreamObserver<WriteMetricsResponse> o) {

    // 1. 对 apikey 做鉴权, 可能是同步或异步的
    // 2. 计量
    // 3. 如果请求类型为独立模式, 那么自己作为一批立即写入ceresdb, 并同步返回写入结果, 要小心超时设置
    // 4. 如果请求是异步模式, 那么攒批写入ceresdb, 此时顺序完全无所谓了, 成功失败也无所谓了, 当然那服务端会尽量统计一下

    TrafficTracer tt = TrafficTracer.KEY.get();

    apikeyAuthService.get(request.getHeader().getApikey(), true) //
        .doOnNext(authInfo -> recordTraffic(authInfo, tt)) //
        .flatMap(authInfo -> { //
          // details
          Mono<Void> detailMono =
              detailsStorageService.write(authInfo, request).onErrorResume(error -> Mono.empty());

          gatewayHookManager.writeMetricsV4(authInfo, request);
          Mono<Void> metricsMono =
              metricStorage.write(convertToWriteMetricsParam(authInfo, request));
          return Flux.merge(detailMono, metricsMono).ignoreElements();
        }).subscribe(null, error -> handleError(error, o), () -> handleSuccess(o));
  }

  private WriteMetricsParam convertToWriteMetricsParam(AuthInfo authInfo,
      WriteMetricsRequestV1 request) {
    // 类型转换
    WriteMetricsParam param = new WriteMetricsParam();
    param.setTenant(authInfo.getTenant());

    List<WriteMetricsParam.Point> points = new ArrayList<>(request.getPointCount());
    for (Point p : request.getPointList()) {

      // if (!productCtlService.isMetricInWhiteList(p.getMetricName())
      // && productCtlService.productClosed(MonitorProductCode.METRIC, p.getTagsMap())) {
      // continue;
      // }

      p = gatewayHookManager.processV1(authInfo, p);
      if (p == null) {
        continue;
      }

      WriteMetricsParam.Point wmpp = new WriteMetricsParam.Point();
      wmpp.setMetricName(p.getMetricName());
      wmpp.setTimeStamp(p.getTimestamp());
      wmpp.setTags(p.getTagsMap());

      if (p.getNumberValuesCount() > 0) {
        wmpp.setValue(p.getNumberValuesOrThrow("value"));
      } else if (p.getStringValuesCount() > 0) {
        wmpp.setStrValue(p.getStringValuesOrThrow("value"));
      }
      points.add(wmpp);
    }
    param.setPoints(points);
    return param;
  }

  private WriteMetricsParam convertToWriteMetricsParam(AuthInfo authInfo,
      WriteMetricsRequestV4 request) {
    // 类型转换
    WriteMetricsParam param = new WriteMetricsParam();
    param.setTenant(authInfo.getTenant());

    int totalPointCount =
        request.getResultsList().stream().mapToInt(x -> x.getTable().getRowsCount()).sum();
    List<WriteMetricsParam.Point> points = new ArrayList<>(totalPointCount);
    param.setPoints(points);

    for (WriteMetricsRequestV4.TaskResult tr : request.getResultsList()) {
      if (tr.getTable().getRowsCount() == 0) {
        continue;
      }
      if (TaskResultUtils.isDetails(tr)) {
        continue;
      }

      WriteMetricsRequestV4.Table table = tr.getTable();
      WriteMetricsRequestV4.Header header = table.getHeader();

      for (WriteMetricsRequestV4.Row row : table.getRowsList()) {
        WriteMetricsParam.Point wmpp = new WriteMetricsParam.Point();
        wmpp.setMetricName(header.getMetricName());
        Map<String, String> tags = Maps.newHashMapWithExpectedSize(header.getTagKeysCount());
        for (int i = 0; i < header.getTagKeysCount(); i++) {
          tags.put(header.getTagKeys(i), row.getTagValues(i));
        }
        // if (!productCtlService.isMetricInWhiteList(header.getMetricName())
        // && productCtlService.productClosed(MonitorProductCode.METRIC, tags)) {
        // continue;
        // }
        wmpp.setTimeStamp(row.getTimestamp());
        wmpp.setTags(tags);
        for (DataNode dataNode : row.getValueValuesList()) {
          switch (dataNode.getType()) {
            case 2:
              wmpp.setStrValue(dataNode.getBytes().toStringUtf8());
              break;
            default:
              wmpp.setValue(dataNode.getValue());
              break;
          }
        }
        points.add(wmpp);
      }
    }
    return param;
  }

  private static void recordTraffic(AuthInfo authInfo, TrafficTracer tt) {
    if (tt == null) {
      return;
    }
    tt.setTenant(authInfo.getTenant());
    StatUtils.GRPC_TRAFFIC.add(StringsKey.of(authInfo.getTenant()), //
        new long[] {1, //
            tt.getInboundWireSize(), // TCP入流量
            tt.getInboundUncompressedSize() // 解压后入流量
        });
  }

  private static void handleSuccess(StreamObserver<WriteMetricsResponse> o) {
    o.onNext(WriteMetricsResponse.newBuilder() //
        .setHeader(CommonResponseHeader.newBuilder() //
            .setCode(Codes.OK) //
            .setMessage("OK") //
            .build())
        .build());
    o.onCompleted();
  }

  private static void handleError(Throwable error, StreamObserver<WriteMetricsResponse> o) {
    LOGGER.error("write error", error);
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
  }
}
