/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.receiver.analysis;

import io.holoinsight.server.apm.common.constants.Const;
import io.holoinsight.server.apm.common.utils.TimeUtils;
import io.holoinsight.server.apm.engine.model.ServiceErrorDO;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.trace.v1.Span;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import org.apache.commons.codec.binary.Hex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServiceErrorAnalysis {

  public ServiceErrorDO serviceErrorDO() {
    return new ServiceErrorDO();
  }

  public List<ServiceErrorDO> analysis(Span span, Map<String, String> spanAttrMap,
      Map<String, String> resourceAttrMap) {
    List<ServiceErrorDO> result = new ArrayList<>();
    if (span.getEventsList().size() > 0) {
      ServiceErrorDO errorInfo = serviceErrorDO();

      OUT: for (Span.Event event : span.getEventsList()) {
        for (KeyValue keyValue : event.getAttributesList()) {
          if ("error.kind".equals(keyValue.getKey())) {
            errorInfo.setErrorKind(keyValue.getValue().getStringValue());
            result.add(setPublicAttrs(errorInfo, span, spanAttrMap, resourceAttrMap));
            break OUT;
          }
        }
      }
    }

    return result;
  }

  public ServiceErrorDO setPublicAttrs(ServiceErrorDO errorInfo, Span span,
      Map<String, String> spanAttrMap, Map<String, String> resourceAttrMap) {
    String realTraceId =
        resourceAttrMap.containsKey(Const.REAL_TRACE_ID) ? resourceAttrMap.get(Const.REAL_TRACE_ID)
            : Hex.encodeHexString(span.getTraceId().toByteArray());
    String realSpanId =
        spanAttrMap.containsKey(Const.REAL_SPAN_ID) ? spanAttrMap.get(Const.REAL_SPAN_ID)
            : Hex.encodeHexString(span.getSpanId().toByteArray());
    errorInfo.setSpanId(realSpanId);
    errorInfo.setTraceId(realTraceId);
    errorInfo.setTenant(resourceAttrMap.get(Const.TENANT));
    errorInfo.setServiceName(resourceAttrMap.get(ResourceAttributes.SERVICE_NAME.getKey()));
    errorInfo.setEndpointName(span.getName());
    errorInfo
        .setServiceInstanceName(resourceAttrMap.get(Const.OTLP_RESOURCE_SERVICE_INSTANCE_NAME));
    errorInfo.setStartTime(TimeUtils.unixNano2MS(span.getStartTimeUnixNano()));
    errorInfo.setTimestamp(TimeUtils.unixNano2MS(span.getEndTimeUnixNano()));
    long latency = TimeUtils.unixNano2MS(span.getEndTimeUnixNano())
        - TimeUtils.unixNano2MS(span.getStartTimeUnixNano());
    errorInfo.setLatency((int) latency);
    return errorInfo;
  }

}
