/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.receiver.common;

import io.holoinsight.server.apm.common.constants.Const;
import io.holoinsight.server.apm.common.model.specification.otel.StatusCode;
import io.holoinsight.server.apm.common.utils.TimeBucket;
import io.holoinsight.server.apm.common.utils.TimeUtils;
import io.holoinsight.server.apm.receiver.builder.RelationBuilder;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.trace.v1.Span;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;
import org.apache.commons.codec.binary.Hex;

import java.util.Map;

public class PublicAttr {

  public static void setPublicAttrs(RelationBuilder sourceBuilder, Span span,
      Map<String, AnyValue> spanAttrMap, Map<String, AnyValue> resourceAttrMap) {
    sourceBuilder.setTenant(resourceAttrMap.get(Const.TENANT).getStringValue());
    long latency = TimeUtils.unixNano2MS(span.getEndTimeUnixNano())
        - TimeUtils.unixNano2MS(span.getStartTimeUnixNano());
    sourceBuilder.setTraceId(Hex.encodeHexString(span.getTraceId().toByteArray()));
    sourceBuilder.setStartTime(TimeUtils.unixNano2MS(span.getStartTimeUnixNano()));
    sourceBuilder.setEndTime(TimeUtils.unixNano2MS(span.getEndTimeUnixNano()));
    sourceBuilder.setTimeBucket(
        TimeBucket.getRecordTimeBucket(TimeUtils.unixNano2MS(span.getStartTimeUnixNano())));
    sourceBuilder.setLatency((int) latency);
    sourceBuilder.setHttpResponseStatusCode(Const.NONE);
    sourceBuilder.setTraceStatus(span.getStatus().getCodeValue());

    // bizops errorCode、rootErrorCode
    if (spanAttrMap.get(Const.ERRORCODE) != null && spanAttrMap.get(Const.ROOTERRORCODE) != null) {
      sourceBuilder.setTraceStatus(StatusCode.ERROR.getCode());
    }

    AnyValue component = spanAttrMap.get(Const.SW_ATTR_COMPONENT);
    if (component != null) {
      sourceBuilder.setComponent(component.getStringValue());
    }

    AnyValue spanLayer = spanAttrMap.get(Const.OTLP_SPANLAYER);
    if (spanLayer != null) {
      sourceBuilder.setType(spanLayer.getStringValue().toUpperCase());
    }

    AnyValue httpCode = spanAttrMap.get(SemanticAttributes.HTTP_STATUS_CODE.getKey());
    if (httpCode != null) {
      sourceBuilder.setHttpResponseStatusCode(Integer.parseInt(httpCode.getStringValue()));
    }

    AnyValue grpcCode = spanAttrMap.get(SemanticAttributes.RPC_GRPC_STATUS_CODE.getKey());
    if (grpcCode != null) {
      sourceBuilder.setRpcStatusCode(grpcCode.getStringValue());
    }

    AnyValue appId = spanAttrMap.get(Const.APPID);
    if (appId != null) {
      sourceBuilder.setAppId(appId.getStringValue());
    }

    AnyValue envId = spanAttrMap.get(Const.ENVID);
    if (envId != null) {
      sourceBuilder.setEnvId(envId.getStringValue());
    }

    AnyValue stamp = spanAttrMap.get(Const.STAMP);
    if (stamp != null) {
      sourceBuilder.setStamp(stamp.getStringValue());
    }

  }
}
