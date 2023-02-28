/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.receiver.trace;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.common.constants.Const;
import io.holoinsight.server.storage.common.model.specification.otel.*;
import io.holoinsight.server.storage.common.model.specification.sw.EndpointRelation;
import io.holoinsight.server.storage.common.model.specification.sw.ServiceInstanceRelation;
import io.holoinsight.server.storage.common.model.specification.sw.ServiceRelation;
import io.holoinsight.server.storage.engine.elasticsearch.model.*;
import io.holoinsight.server.storage.engine.model.*;
import io.holoinsight.server.storage.receiver.analysis.RelationAnalysis;
import io.holoinsight.server.storage.receiver.builder.RelationBuilder;
import io.holoinsight.server.storage.receiver.common.TransformAttr;
import io.holoinsight.server.storage.server.service.*;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.trace.v1.ScopeSpans;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author jiwliu
 * @version : SpanHandler.java, v 0.1 2022年11月03日 14:12 xiangwanpeng Exp $
 */
@Service
@ConditionalOnFeature("trace")
@Slf4j
public class SpanHandler {

  @Autowired
  private TraceService traceService;
  @Autowired
  private ServiceRelationService serviceRelationService;
  @Autowired
  private EndpointRelationService endpointRelationService;
  @Autowired
  private NetworkAddressMappingService networkAddressMappingService;
  @Autowired
  private RelationAnalysis relationAnalysis;
  @Autowired
  private ServiceInstanceRelationService serviceInstanceRelationService;

  @Autowired
  private SlowSqlService slowSqlService;

  public void handleResourceSpans(
      List<io.opentelemetry.proto.trace.v1.ResourceSpans> resourceSpansList) {
    StopWatch stopWatch = StopWatch.createStarted();
    List<SpanDO> spanEsDOList = new ArrayList<>();
    List<NetworkAddressMappingDO> networkAddressMappingList = new ArrayList<>();
    List<RelationBuilder> relationBuilders = new ArrayList<>();
    List<SlowSqlDO> slowSqlEsDOList = new ArrayList<>();
    boolean success = true;
    try {
      if (CollectionUtils.isEmpty(resourceSpansList)) {
        return;
      }
      resourceSpansList.forEach(resourceSpans -> {
        io.opentelemetry.proto.resource.v1.Resource resource = resourceSpans.getResource();
        Resource otelResource = new Resource();
        otelResource.setDroppedAttributesCount(resource.getDroppedAttributesCount());
        otelResource.setAttributes(resource.getAttributesList().stream()
            .map(attr -> new KeyValue(attr.getKey(), anyValueToString(attr.getValue())))
            .collect(Collectors.toList()));
        Map<String, AnyValue> resourceAttrMap =
            TransformAttr.attList2Map(resource.getAttributesList());

        List<ScopeSpans> scopeSpans = resourceSpans.getScopeSpansList();
        Map<String, String> spanIdEndpointMap = generateSpanIdEndpointMap(scopeSpans);
        if (CollectionUtils.isNotEmpty(scopeSpans)) {
          scopeSpans.forEach(scopeSpan -> {
            List<io.opentelemetry.proto.trace.v1.Span> spans = scopeSpan.getSpansList();
            if (CollectionUtils.isNotEmpty(spans)) {
              spans.forEach(span -> {
                Map<String, AnyValue> spanAttrMap =
                    TransformAttr.attList2Map(span.getAttributesList());

                if (span
                    .getKind() == io.opentelemetry.proto.trace.v1.Span.SpanKind.SPAN_KIND_SERVER) {
                  networkAddressMappingList.addAll(relationAnalysis
                      .generateNetworkAddressMapping(span, spanAttrMap, resourceAttrMap));
                }
                if (span.getKind() == io.opentelemetry.proto.trace.v1.Span.SpanKind.SPAN_KIND_SERVER
                    || span
                        .getKind() == io.opentelemetry.proto.trace.v1.Span.SpanKind.SPAN_KIND_CONSUMER) {
                  relationBuilders.addAll(
                      relationAnalysis.analysisServerSpan(span, spanAttrMap, resourceAttrMap));
                } else if (span
                    .getKind() == io.opentelemetry.proto.trace.v1.Span.SpanKind.SPAN_KIND_CLIENT
                    || span
                        .getKind() == io.opentelemetry.proto.trace.v1.Span.SpanKind.SPAN_KIND_PRODUCER) {
                  relationBuilders.addAll(relationAnalysis.analysisClientSpan(span, spanAttrMap,
                      resourceAttrMap, spanIdEndpointMap));
                  slowSqlEsDOList.addAll(
                      relationAnalysis.analysisClientSpan(span, spanAttrMap, resourceAttrMap));
                } else if (span
                    .getKind() == io.opentelemetry.proto.trace.v1.Span.SpanKind.SPAN_KIND_INTERNAL) {
                  relationBuilders.addAll(
                      relationAnalysis.analysisInternalSpan(span, spanAttrMap, resourceAttrMap));
                }

                spanEsDOList.add(SpanDO.fromSpan(transformSpan(span), otelResource));
              });
            }
          });
        }
      });
      storageSpan(spanEsDOList);
      storageNetworkMapping(networkAddressMappingList);
      storageSlowSql(slowSqlEsDOList);
      buildRelation(relationBuilders);
    } catch (Exception e) {
      success = false;
      log.error("[apm] handle span error", e);
    }
    log.info(
        "[apm] handle span finish, result={}, spans={}, networks={}, slowSqls={}, relations={}, cost={}",
        success, spanEsDOList.size(), networkAddressMappingList.size(), slowSqlEsDOList.size(),
        relationBuilders.size(), stopWatch.getTime());
  }

  private String anyValueToString(AnyValue anyValue) {
    switch (anyValue.getValueCase().getNumber()) {
      case AnyValue.STRING_VALUE_FIELD_NUMBER:
        return anyValue.getStringValue();
      case AnyValue.BOOL_VALUE_FIELD_NUMBER:
        return String.valueOf(anyValue.getBoolValue());
      case AnyValue.INT_VALUE_FIELD_NUMBER:
        return String.valueOf(anyValue.getIntValue());
      case AnyValue.DOUBLE_VALUE_FIELD_NUMBER:
        return String.valueOf(anyValue.getDoubleValue());
      case AnyValue.ARRAY_VALUE_FIELD_NUMBER:
        return String.valueOf(anyValue.getArrayValue());
      case AnyValue.KVLIST_VALUE_FIELD_NUMBER:
        return String.valueOf(anyValue.getKvlistValue());
      case AnyValue.BYTES_VALUE_FIELD_NUMBER:
        return String.valueOf(anyValue.getBytesValue());
      default:
        throw new UnsupportedOperationException("unsupported value type: " + anyValue);
    }
  }

  public void buildRelation(List<RelationBuilder> relationBuilders) throws IOException {
    List<ServiceRelationDO> serverRelationList = new ArrayList<>(relationBuilders.size());
    List<ServiceInstanceRelationDO> serverInstanceRelationList =
        new ArrayList<>(relationBuilders.size());
    List<EndpointRelationDO> endpointRelationList = new ArrayList<>(relationBuilders.size());

    relationBuilders.forEach(callingIn -> {
      ServiceRelation serviceRelation = callingIn.toServiceRelation();
      serverRelationList.add(ServiceRelationDO.fromServiceRelation(serviceRelation));

      ServiceInstanceRelation serviceInstanceRelation = callingIn.toServiceInstanceRelation();
      serverInstanceRelationList
          .add(ServiceInstanceRelationDO.fromServiceInstanceRelation(serviceInstanceRelation));

      EndpointRelation endpointRelation = callingIn.toEndpointRelation();
      if (endpointRelation != null) {
        endpointRelationList.add(EndpointRelationDO.fromEndpointRelation(endpointRelation));
      }
    });

    storageServiceRelation(serverRelationList);
    storageServiceInstanceRelation(serverInstanceRelationList);
    storageEndpointRelation(endpointRelationList);
  }

  private void storageSpan(List<SpanDO> spans) throws Exception {
    traceService.insertSpans(spans);
  }

  public void storageNetworkMapping(List<NetworkAddressMappingDO> networkAddressMappingList)
      throws IOException {
    networkAddressMappingService.insert(networkAddressMappingList);
  }

  public void storageServiceRelation(List<ServiceRelationDO> relationList) throws IOException {
    serviceRelationService.insert(relationList);
  }

  public void storageServiceInstanceRelation(List<ServiceInstanceRelationDO> relationList)
      throws IOException {
    serviceInstanceRelationService.insert(relationList);
  }

  public void storageEndpointRelation(List<EndpointRelationDO> relationList) throws IOException {
    endpointRelationService.insert(relationList);
  }

  public void storageSlowSql(List<SlowSqlDO> slowSqlEsDOList) throws IOException {
    slowSqlService.insert(slowSqlEsDOList);
  }

  /**
   * for client span endpoint topology
   *
   * @return Map<SpanId, endpointName>
   */
  private Map<String, String> generateSpanIdEndpointMap(List<ScopeSpans> scopeSpans) {
    Map<String, String> result = new HashMap<>();
    scopeSpans.forEach(scopeSpan -> {
      scopeSpan.getSpansList().forEach(span -> {
        result.put(Hex.encodeHexString(span.getSpanId().toByteArray()), span.getName());
      });
    });

    return result;
  }

  private Span transformSpan(io.opentelemetry.proto.trace.v1.Span span) {
    Span otelSpan = new Span();
    otelSpan.setTraceId(Hex.encodeHexString(span.getTraceId().toByteArray()));
    otelSpan.setSpanId(Hex.encodeHexString(span.getSpanId().toByteArray()));
    otelSpan.setParentSpanId(Hex.encodeHexString(span.getParentSpanId().toByteArray()));
    otelSpan.setName(span.getName());
    otelSpan.setTraceState(span.getTraceState());
    otelSpan.setKind(SpanKind.fromProto(span.getKind()));
    otelSpan.setStartTimeUnixNano(span.getStartTimeUnixNano());
    otelSpan.setEndTimeUnixNano(span.getEndTimeUnixNano());
    otelSpan.setAttributes(span.getAttributesList().stream()
        .map(attr -> new KeyValue(attr.getKey(), anyValueToString(attr.getValue())))
        .collect(Collectors.toList()));
    otelSpan.setDroppedAttributesCount(span.getDroppedAttributesCount());
    otelSpan.setEvents(span.getEventsList().stream().map(event -> {
      Event otelEvent = new Event();
      otelEvent.setName(event.getName());
      otelEvent.setTimeUnixNano(event.getTimeUnixNano());
      otelEvent.setDroppedAttributesCount(event.getDroppedAttributesCount());
      otelEvent.setAttributes(event.getAttributesList().stream()
          .map(attr -> new KeyValue(attr.getKey(), anyValueToString(attr.getValue())))
          .collect(Collectors.toList()));
      return otelEvent;
    }).collect(Collectors.toList()));
    otelSpan.setDroppedEventsCount(span.getDroppedEventsCount());
    otelSpan.setLinks(span.getLinksList().stream().map(link -> {
      Link otelLink = new Link();
      otelLink.setTraceId(Hex.encodeHexString(link.getTraceId().toByteArray()));
      otelLink.setSpanId(Hex.encodeHexString(link.getSpanId().toByteArray()));
      otelLink.setTraceState(link.getTraceState());
      otelLink.setDroppedAttributesCount(link.getDroppedAttributesCount());
      otelLink.setAttributes(link.getAttributesList().stream()
          .map(attr -> new KeyValue(attr.getKey(), anyValueToString(attr.getValue())))
          .collect(Collectors.toList()));
      return otelLink;
    }).collect(Collectors.toList()));
    otelSpan.setDroppedLinksCount(span.getDroppedLinksCount());
    otelSpan.setStatus(new Status(span.getStatus().getMessage(),
        StatusCode.fromProto(span.getStatus().getCode())));

    for (io.opentelemetry.proto.common.v1.KeyValue attr : span.getAttributesList()) {
      // bizops errorCode、rootErrorCode
      if ((Const.ERRORCODE.equals(attr.getKey()) || Const.ROOTERRORCODE.equals(attr.getKey()))
          && attr.getValue() != null) {
        otelSpan.getStatus().setStatusCode(StatusCode.ERROR);
        break;
      }
    }

    return otelSpan;
  }
}
