/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.receiver.analysis;

import io.holoinsight.server.apm.common.constants.Const;
import io.holoinsight.server.apm.common.utils.TimeUtils;
import io.holoinsight.server.apm.engine.model.SlowSqlDO;
import io.holoinsight.server.common.trace.TraceAgentConfiguration;
import io.holoinsight.server.common.trace.TraceAgentConfigurationScheduler;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.trace.v1.Span;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlowSqlAnalysis {
  @Autowired
  private TraceAgentConfigurationScheduler agentConfigurationScheduler;

  public SlowSqlDO slowSqlDO() {
    return new SlowSqlDO();
  }

  public List<SlowSqlDO> analysis(Span span, Map<String, AnyValue> spanAttrMap,
      Map<String, AnyValue> resourceAttrMap) {
    List<SlowSqlDO> result = new ArrayList<>(10);

    AnyValue statement = spanAttrMap.get(SemanticAttributes.DB_STATEMENT.getKey());
    AnyValue peerName = spanAttrMap.get(SemanticAttributes.NET_PEER_NAME.getKey());
    AnyValue peerPort = spanAttrMap.get(SemanticAttributes.NET_PEER_PORT.getKey());
    String tenant = resourceAttrMap.get(Const.TENANT).getStringValue();
    String service = resourceAttrMap.get(ResourceAttributes.SERVICE_NAME.getKey()).getStringValue();

    if (peerName == null || StringUtils.isEmpty(peerName.getStringValue()) || statement == null) {
      return result;
    }

    long latency = TimeUtils.unixNano2MS(span.getEndTimeUnixNano())
        - TimeUtils.unixNano2MS(span.getStartTimeUnixNano());
    long slowSqlThreshold = Const.SLOW_SQL_THRESHOLD;
    // Get slow sql dynamic threshold from cache
    TraceAgentConfiguration configuration =
        agentConfigurationScheduler.getValue(tenant, service, getExtendInfo(spanAttrMap));
    if (configuration != null) {
      slowSqlThreshold = Long.parseLong(configuration.getConfiguration()
          .getOrDefault(Const.SLOW_SQL_THRESHOLD_CONFIG, String.valueOf(slowSqlThreshold)));
    }
    if (latency < slowSqlThreshold) {
      return result;
    }

    String dbAddress = peerPort == null ? peerName.getStringValue()
        : peerName.getStringValue() + ":" + peerPort.getStringValue();

    SlowSqlDO slowSqlEsDO = slowSqlDO();
    result.add(setPublicAttrs(slowSqlEsDO, span, spanAttrMap, resourceAttrMap, dbAddress));
    return result;
  }

  public SlowSqlDO setPublicAttrs(SlowSqlDO slowSqlDO, Span span, Map<String, AnyValue> spanAttrMap,
      Map<String, AnyValue> resourceAttrMap, String address) {
    long latency = TimeUtils.unixNano2MS(span.getEndTimeUnixNano())
        - TimeUtils.unixNano2MS(span.getStartTimeUnixNano());
    slowSqlDO.setTenant(resourceAttrMap.get(Const.TENANT).getStringValue());
    slowSqlDO.setServiceName(
        resourceAttrMap.get(ResourceAttributes.SERVICE_NAME.getKey()).getStringValue());
    slowSqlDO.setAddress(address);
    slowSqlDO.setLatency((int) latency);
    slowSqlDO.setStartTime(TimeUtils.unixNano2MS(span.getStartTimeUnixNano()));
    slowSqlDO.setTimestamp(TimeUtils.unixNano2MS(span.getEndTimeUnixNano()));
    String realTraceId = resourceAttrMap.containsKey(Const.REAL_TRACE_ID)
        ? resourceAttrMap.get(Const.REAL_TRACE_ID).getStringValue()
        : Hex.encodeHexString(span.getTraceId().toByteArray());
    slowSqlDO.setTraceId(realTraceId);
    slowSqlDO
        .setStatement(spanAttrMap.get(SemanticAttributes.DB_STATEMENT.getKey()).getStringValue());
    return slowSqlDO;
  }

  public Map<String, String> getExtendInfo(Map<String, AnyValue> spanAttrMap) {
    Map<String, String> extendInfo = new HashMap<>(spanAttrMap.size());
    spanAttrMap.forEach((k, v) -> {
      extendInfo.put(k, v.getStringValue());
    });
    extendInfo.put("type", "*");
    extendInfo.put("language", "*");
    return extendInfo;
  }

}
