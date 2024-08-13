/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.receiver.common;

import io.holoinsight.server.apm.common.constants.Const;
import io.holoinsight.server.apm.common.utils.TimeBucket;
import io.holoinsight.server.apm.common.utils.TimeUtils;
import io.holoinsight.server.apm.receiver.builder.RPCTrafficSourceBuilder;
import io.opentelemetry.proto.trace.v1.Span;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;
import org.apache.commons.codec.binary.Hex;

import java.util.Map;


public class PublicAttr implements IPublicAttr {

  @Override
  public RPCTrafficSourceBuilder setPublicAttrs(RPCTrafficSourceBuilder sourceBuilder, Span span,
      Map<String, String> spanAttrMap, Map<String, String> resourceAttrMap) {
    sourceBuilder.setTenant(resourceAttrMap.get(Const.TENANT));
    long latency = TimeUtils.unixNano2MS(span.getEndTimeUnixNano())
        - TimeUtils.unixNano2MS(span.getStartTimeUnixNano());

    String realTraceId =
        resourceAttrMap.containsKey(Const.REAL_TRACE_ID) ? resourceAttrMap.get(Const.REAL_TRACE_ID)
            : Hex.encodeHexString(span.getTraceId().toByteArray());
    sourceBuilder.setTraceId(realTraceId);
    sourceBuilder.setStartTime(TimeUtils.unixNano2MS(span.getStartTimeUnixNano()));
    sourceBuilder.setEndTime(TimeUtils.unixNano2MS(span.getEndTimeUnixNano()));
    sourceBuilder.setTimeBucket(
        TimeBucket.getRecordTimeBucket(TimeUtils.unixNano2MS(span.getStartTimeUnixNano())));
    sourceBuilder.setLatency((int) latency);
    sourceBuilder.setHttpResponseStatusCode(Const.NONE);
    sourceBuilder.setTraceStatus(span.getStatus().getCodeValue());

    String component = spanAttrMap.get(Const.SW_ATTR_COMPONENT);
    if (component != null) {
      sourceBuilder.setComponent(component);
    }

    String spanLayer = spanAttrMap.get(Const.OTLP_SPANLAYER);
    if (spanLayer != null) {
      sourceBuilder.setType(spanLayer.toUpperCase());
    }

    String httpCode = spanAttrMap.get(SemanticAttributes.HTTP_STATUS_CODE.getKey());
    if (httpCode != null) {
      sourceBuilder.setHttpResponseStatusCode(httpCode);
    }

    String grpcCode = spanAttrMap.get(SemanticAttributes.RPC_GRPC_STATUS_CODE.getKey());
    if (grpcCode != null) {
      sourceBuilder.setRpcStatusCode(grpcCode);
    }

    return sourceBuilder;
  }
}
