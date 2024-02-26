/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.receiver.trace;

import static io.holoinsight.server.apm.receiver.common.TransformAttr.anyValueToString;
import static io.holoinsight.server.apm.receiver.common.TransformAttr.convertKeyValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;

import io.holoinsight.server.apm.common.constants.Const;
import io.holoinsight.server.apm.common.model.specification.otel.Event;
import io.holoinsight.server.apm.common.model.specification.otel.KeyValue;
import io.holoinsight.server.apm.common.model.specification.otel.Link;
import io.holoinsight.server.apm.common.model.specification.otel.Resource;
import io.holoinsight.server.apm.common.model.specification.otel.Span;
import io.holoinsight.server.apm.common.model.specification.otel.SpanKind;
import io.holoinsight.server.apm.common.model.specification.otel.Status;
import io.holoinsight.server.apm.common.model.specification.otel.StatusCode;
import io.holoinsight.server.apm.common.model.specification.sw.EndpointRelation;
import io.holoinsight.server.apm.common.model.specification.sw.ServiceInstanceRelation;
import io.holoinsight.server.apm.common.model.specification.sw.ServiceRelation;
import io.holoinsight.server.apm.engine.model.EndpointRelationDO;
import io.holoinsight.server.apm.engine.model.NetworkAddressMappingDO;
import io.holoinsight.server.apm.engine.model.ServiceErrorDO;
import io.holoinsight.server.apm.engine.model.ServiceInstanceRelationDO;
import io.holoinsight.server.apm.engine.model.ServiceRelationDO;
import io.holoinsight.server.apm.engine.model.SlowSqlDO;
import io.holoinsight.server.apm.engine.model.SpanDO;
import io.holoinsight.server.apm.engine.storage.SpanStorageHookManager;
import io.holoinsight.server.apm.receiver.analysis.RelationAnalysis;
import io.holoinsight.server.apm.receiver.analysis.ServiceErrorAnalysis;
import io.holoinsight.server.apm.receiver.analysis.SlowSqlAnalysis;
import io.holoinsight.server.apm.receiver.builder.RPCTrafficSourceBuilder;
import io.holoinsight.server.apm.receiver.common.TransformAttr;
import io.holoinsight.server.apm.server.service.EndpointRelationService;
import io.holoinsight.server.apm.server.service.NetworkAddressMappingService;
import io.holoinsight.server.apm.server.service.ServiceErrorService;
import io.holoinsight.server.apm.server.service.ServiceInstanceRelationService;
import io.holoinsight.server.apm.server.service.ServiceRelationService;
import io.holoinsight.server.apm.server.service.SlowSqlService;
import io.holoinsight.server.apm.server.service.TraceService;
import io.holoinsight.server.common.ctl.MonitorProductCode;
import io.holoinsight.server.common.ctl.ProductCtlService;
import io.opentelemetry.proto.trace.v1.ScopeSpans;
import lombok.extern.slf4j.Slf4j;

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
  private SlowSqlAnalysis slowSqlAnalysis;
  @Autowired
  private SlowSqlService slowSqlService;
  @Autowired
  private ServiceErrorAnalysis errorAnalysis;
  @Autowired
  private ServiceErrorService serviceErrorService;
  @Autowired
  private ProductCtlService productCtlService;
  @Autowired
  private SpanStorageHookManager spanStorageHookManager;

  public void handleResourceSpans(
      List<io.opentelemetry.proto.trace.v1.ResourceSpans> resourceSpansList) {
    StopWatch stopWatch = StopWatch.createStarted();
    List<SpanDO> spanEsDOList = new ArrayList<>();
    List<NetworkAddressMappingDO> networkAddressMappingList = new ArrayList<>();
    List<RPCTrafficSourceBuilder> relationBuilders = new ArrayList<>();
    List<SlowSqlDO> slowSqlEsDOList = new ArrayList<>();
    List<ServiceErrorDO> errorInfoList = new ArrayList<>();
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
        Map<String, String> resourceAttrMap =
            TransformAttr.attList2Map(resource.getAttributesList());

        List<ScopeSpans> scopeSpans = resourceSpans.getScopeSpansList();
        Map<String, String> spanIdEndpointMap = generateSpanIdEndpointMap(scopeSpans);
        if (CollectionUtils.isNotEmpty(scopeSpans)) {
          scopeSpans.forEach(scopeSpan -> {
            List<io.opentelemetry.proto.trace.v1.Span> spans = scopeSpan.getSpansList();
            if (CollectionUtils.isNotEmpty(spans)) {
              for (io.opentelemetry.proto.trace.v1.Span span : spans) {
                Map<String, String> spanAttrMap =
                    TransformAttr.attList2Map(span.getAttributesList());
                if (productCtlService.productClosed(MonitorProductCode.TRACE, spanAttrMap)) {
                  continue;
                }

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
                  slowSqlEsDOList
                      .addAll(slowSqlAnalysis.analysis(span, spanAttrMap, resourceAttrMap));
                } else if (span
                    .getKind() == io.opentelemetry.proto.trace.v1.Span.SpanKind.SPAN_KIND_INTERNAL) {
                  relationBuilders.addAll(
                      relationAnalysis.analysisInternalSpan(span, spanAttrMap, resourceAttrMap));
                }

                errorInfoList.addAll(errorAnalysis.analysis(span, spanAttrMap, resourceAttrMap));
                spanEsDOList.add(SpanDO.fromSpan(transformSpan(span, resourceAttrMap, spanAttrMap),
                    otelResource));
              }
            }
          });
        }
      });
      storageSpan(spanEsDOList);
      storageNetworkMapping(networkAddressMappingList);
      storageSlowSql(slowSqlEsDOList);
      storageServiceError(errorInfoList);
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

  public void buildRelation(List<RPCTrafficSourceBuilder> relationBuilders) throws Exception {
    try {
      List<ServiceRelationDO> serverRelationList = new ArrayList<>(relationBuilders.size());
      List<ServiceInstanceRelationDO> serverInstanceRelationList =
          new ArrayList<>(relationBuilders.size());
      List<EndpointRelationDO> endpointRelationList = new ArrayList<>(relationBuilders.size());

      relationBuilders.forEach(callingIn -> {
        ServiceRelation serviceRelation = callingIn.toServiceRelation();
        if (serviceRelation != null) {
          serverRelationList.add(ServiceRelationDO.fromServiceRelation(serviceRelation));
        }

        ServiceInstanceRelation serviceInstanceRelation = callingIn.toServiceInstanceRelation();
        if (serviceInstanceRelation != null) {
          serverInstanceRelationList
              .add(ServiceInstanceRelationDO.fromServiceInstanceRelation(serviceInstanceRelation));
        }

        EndpointRelation endpointRelation = callingIn.toEndpointRelation();
        if (endpointRelation != null) {
          endpointRelationList.add(EndpointRelationDO.fromEndpointRelation(endpointRelation));
        }
      });

      storageServiceRelation(serverRelationList);
      storageServiceInstanceRelation(serverInstanceRelationList);
      storageEndpointRelation(endpointRelationList);
    } catch (Exception e) {
      log.error("[apm] build relation error", e);
    }
  }

  private void storageSpan(List<SpanDO> spans) throws Exception {
    spanStorageHookManager.beforeStorage(spans);
    traceService.insertSpans(spans);
  }

  public void storageNetworkMapping(List<NetworkAddressMappingDO> networkAddressMappingList)
      throws Exception {
    networkAddressMappingService.insert(networkAddressMappingList);
  }

  public void storageServiceRelation(List<ServiceRelationDO> relationList) throws Exception {
    spanStorageHookManager.beforeStorageServiceRelation(relationList);
    serviceRelationService.insert(relationList);
  }

  public void storageServiceInstanceRelation(List<ServiceInstanceRelationDO> relationList)
      throws Exception {
    serviceInstanceRelationService.insert(relationList);
  }

  public void storageEndpointRelation(List<EndpointRelationDO> relationList) throws Exception {
    endpointRelationService.insert(relationList);
  }

  public void storageSlowSql(List<SlowSqlDO> slowSqlEsDOList) throws Exception {
    slowSqlService.insert(slowSqlEsDOList);
  }

  public void storageServiceError(List<ServiceErrorDO> errorInfoDOList) throws Exception {
    spanStorageHookManager.beforeStorageServiceError(errorInfoDOList);
    serviceErrorService.insert(errorInfoDOList);
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

  protected Span transformSpan(io.opentelemetry.proto.trace.v1.Span span,
      Map<String, String> resourceAttrMap, Map<String, String> spanAttrMap) {
    String realTraceId =
        resourceAttrMap.containsKey(Const.REAL_TRACE_ID) ? resourceAttrMap.get(Const.REAL_TRACE_ID)
            : Hex.encodeHexString(span.getTraceId().toByteArray());
    String realSpanId =
        spanAttrMap.containsKey(Const.REAL_SPAN_ID) ? spanAttrMap.get(Const.REAL_SPAN_ID)
            : Hex.encodeHexString(span.getSpanId().toByteArray());
    String realParentSpanId = spanAttrMap.containsKey(Const.REAL_PARENT_SPAN_ID)
        ? spanAttrMap.get(Const.REAL_PARENT_SPAN_ID)
        : Hex.encodeHexString(span.getParentSpanId().toByteArray());
    Span otelSpan = new Span();
    otelSpan.setTraceId(realTraceId);
    otelSpan.setSpanId(realSpanId);
    otelSpan.setParentSpanId(realParentSpanId);
    otelSpan.setName(span.getName());
    otelSpan.setTraceState(span.getTraceState());
    otelSpan.setKind(SpanKind.fromProto(span.getKind()));
    otelSpan.setStartTimeUnixNano(span.getStartTimeUnixNano());
    otelSpan.setEndTimeUnixNano(span.getEndTimeUnixNano());
    otelSpan.setAttributes(span.getAttributesList().stream().map(attr -> convertKeyValue(attr))
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

    return otelSpan;
  }
}
