/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.receiver.analysis;

import io.holoinsight.server.apm.common.constants.Const;
import io.holoinsight.server.apm.common.exception.UnexpectedException;
import io.holoinsight.server.apm.common.model.specification.sw.DetectPoint;
import io.holoinsight.server.apm.common.model.specification.sw.Layer;
import io.holoinsight.server.apm.common.model.specification.sw.NetworkAddressMapping;
import io.holoinsight.server.apm.common.utils.TimeBucket;
import io.holoinsight.server.apm.common.utils.TimeUtils;
import io.holoinsight.server.apm.engine.model.NetworkAddressMappingDO;
import io.holoinsight.server.apm.engine.model.SlowSqlDO;
import io.holoinsight.server.apm.grpc.trace.RefType;
import io.holoinsight.server.apm.receiver.builder.RelationBuilder;
import io.holoinsight.server.apm.receiver.common.IPublicAttr;
import io.holoinsight.server.apm.receiver.common.TransformAttr;
import io.holoinsight.server.apm.server.cache.NetworkAddressMappingCache;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.trace.v1.Span;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class RelationAnalysis {

  @Autowired
  private NetworkAddressMappingCache networkAddressMappingCache;

  @Autowired
  private IPublicAttr publicAttr;

  public List<RelationBuilder> analysisServerSpan(Span span, Map<String, AnyValue> spanAttrMap,
      Map<String, AnyValue> resourceAttrMap) {
    List<RelationBuilder> callingInTraffic = new ArrayList<>(10);

    String serviceName =
        resourceAttrMap.get(ResourceAttributes.SERVICE_NAME.getKey()).getStringValue();
    String instanceName =
        resourceAttrMap.get(Const.OTLP_RESOURCE_SERVICE_INSTANCE_NAME).getStringValue();

    if (span.getLinksCount() > 0) {
      for (Span.Link link : span.getLinksList()) {
        RelationBuilder sourceBuilder = new RelationBuilder();

        String networkAddressUsedAtPeer = null;
        String parentServiceName = null;
        String parentInstanceName = null;
        String parentEndpointName = Const.USER_ENDPOINT_NAME;

        for (KeyValue keyValue : link.getAttributesList()) {
          if (Const.SW_REF_NETWORK_ADDRESSUSEDATPEER.equals(keyValue.getKey())) {
            networkAddressUsedAtPeer = keyValue.getValue().getStringValue();
          } else if (Const.SW_REF_PARENT_SERVICE.equals(keyValue.getKey())) {
            parentServiceName = keyValue.getValue().getStringValue();
          } else if (Const.SW_REF_PARENT_SERVICE_INSTANCE_NAME.equals(keyValue.getKey())) {
            parentInstanceName = keyValue.getValue().getStringValue();
          } else if (Const.SW_REF_PARENT_ENDPOINT.equals(keyValue.getKey())) {
            parentEndpointName = keyValue.getValue().getStringValue();
          }
        }

        if (Span.SpanKind.SPAN_KIND_CONSUMER == span.getKind()) {
          sourceBuilder.setSourceServiceName(networkAddressUsedAtPeer);
          sourceBuilder.setSourceEndpointOwnerServiceName(parentServiceName);
          sourceBuilder.setSourceServiceInstanceName(networkAddressUsedAtPeer);
          sourceBuilder.setSourceLayer(Layer.VIRTUAL_MQ);
          sourceBuilder.setSourceEndpointOwnerServiceLayer(Layer.GENERAL);
        } else {
          sourceBuilder.setSourceServiceName(parentServiceName);
          sourceBuilder.setSourceServiceInstanceName(parentInstanceName);
          sourceBuilder.setSourceLayer(Layer.GENERAL);
        }

        sourceBuilder.setSourceEndpointName(parentEndpointName);
        sourceBuilder.setDestEndpointName(span.getName());
        sourceBuilder.setDestServiceInstanceName(instanceName);
        sourceBuilder.setDestServiceName(serviceName);
        sourceBuilder.setDestLayer(Layer.GENERAL);
        sourceBuilder.setDetectPoint(DetectPoint.SERVER);
        publicAttr.setPublicAttrs(sourceBuilder, span, spanAttrMap, resourceAttrMap);
        callingInTraffic.add(sourceBuilder);
      }
    } else {
      RelationBuilder sourceBuilder = new RelationBuilder();
      sourceBuilder.setSourceServiceName(Const.USER_SERVICE_NAME);
      sourceBuilder.setSourceServiceInstanceName(Const.USER_INSTANCE_NAME);
      sourceBuilder.setSourceEndpointName(Const.USER_ENDPOINT_NAME);
      sourceBuilder.setSourceLayer(Layer.UNDEFINED);
      sourceBuilder.setDestServiceInstanceName(instanceName);
      sourceBuilder.setDestServiceName(serviceName);
      sourceBuilder.setDestLayer(Layer.GENERAL);
      sourceBuilder.setDestEndpointName(span.getName());
      sourceBuilder.setDetectPoint(DetectPoint.SERVER);

      publicAttr.setPublicAttrs(sourceBuilder, span, spanAttrMap, resourceAttrMap);
      callingInTraffic.add(sourceBuilder);
    }

    return callingInTraffic;
  }

  public List<RelationBuilder> analysisClientSpan(Span span, Map<String, AnyValue> spanAttrMap,
      Map<String, AnyValue> resourceAttrMap, Map<String, String> endpointMap) {
    List<RelationBuilder> callingOutTraffic = new ArrayList<>(10);
    RelationBuilder sourceBuilder = new RelationBuilder();

    AnyValue peerName = spanAttrMap.get(SemanticAttributes.NET_PEER_NAME.getKey());
    AnyValue peerPort = spanAttrMap.get(SemanticAttributes.NET_PEER_PORT.getKey());

    if (peerName == null && peerPort == null) {
      return callingOutTraffic;
    }

    String spanLayer = "Unknown";
    if (spanAttrMap.get(Const.OTLP_SPANLAYER) != null) {
      spanLayer = spanAttrMap.get(Const.OTLP_SPANLAYER).getStringValue();
    }
    String serviceName =
        resourceAttrMap.get(ResourceAttributes.SERVICE_NAME.getKey()).getStringValue();
    String instanceName =
        resourceAttrMap.get(Const.OTLP_RESOURCE_SERVICE_INSTANCE_NAME).getStringValue();;

    String networkAddress = peerPort == null ? peerName.getStringValue()
        : peerName.getStringValue() + ":" + peerPort.getStringValue();
    sourceBuilder.setSourceServiceName(serviceName);
    sourceBuilder.setSourceServiceInstanceName(instanceName);
    sourceBuilder.setSourceLayer(Layer.GENERAL);
    sourceBuilder.setSourceEndpointName(
        endpointMap.getOrDefault(Hex.encodeHexString(span.getParentSpanId().toByteArray()), ""));

    final NetworkAddressMappingDO networkAddressMapping =
        networkAddressMappingCache.get(networkAddress);
    if (networkAddressMapping == null) {
      sourceBuilder.setDestServiceName(networkAddress);
      sourceBuilder.setDestServiceInstanceName(networkAddress);
      sourceBuilder.setDestLayer(identifyRemoteServiceLayer(spanLayer));
      sourceBuilder.setDestEndpointName(span.getName());
    } else {
      return callingOutTraffic;
    }

    sourceBuilder.setDetectPoint(DetectPoint.CLIENT);
    publicAttr.setPublicAttrs(sourceBuilder, span, spanAttrMap, resourceAttrMap);
    callingOutTraffic.add(sourceBuilder);

    return callingOutTraffic;
  }


  public List<SlowSqlDO> analysisClientSpan(Span span, Map<String, AnyValue> spanAttrMap,
      Map<String, AnyValue> resourceAttrMap) {
    List<SlowSqlDO> result = new ArrayList<>(10);

    String serviceName =
        resourceAttrMap.get(ResourceAttributes.SERVICE_NAME.getKey()).getStringValue();
    AnyValue statement = spanAttrMap.get(SemanticAttributes.DB_STATEMENT.getKey());
    AnyValue peerName = spanAttrMap.get(SemanticAttributes.NET_PEER_NAME.getKey());
    AnyValue peerPort = spanAttrMap.get(SemanticAttributes.NET_PEER_PORT.getKey());
    long latency = TimeUtils.unixNano2MS(span.getEndTimeUnixNano())
        - TimeUtils.unixNano2MS(span.getStartTimeUnixNano());

    if ((peerName == null && peerPort == null) || statement == null
        || latency < Const.SLOW_SQL_THRESHOLD) {
      return result;
    }

    String dbAddress = peerPort == null ? peerName.getStringValue()
        : peerName.getStringValue() + ":" + peerPort.getStringValue();
    SlowSqlDO slowSqlEsDO = new SlowSqlDO();
    slowSqlEsDO.setTenant(resourceAttrMap.get(Const.TENANT).getStringValue());
    slowSqlEsDO.setServiceName(serviceName);
    slowSqlEsDO.setAddress(dbAddress);
    slowSqlEsDO.setLatency((int) latency);
    slowSqlEsDO.setStartTime(TimeUtils.unixNano2MS(span.getStartTimeUnixNano()));
    slowSqlEsDO.setTimeBucket(
        TimeBucket.getRecordTimeBucket(TimeUtils.unixNano2MS(span.getEndTimeUnixNano())));
    slowSqlEsDO.setTraceId(Hex.encodeHexString(span.getTraceId().toByteArray()));
    slowSqlEsDO.setStatement(statement.getStringValue());

    result.add(slowSqlEsDO);
    return result;
  }

  public List<RelationBuilder> analysisInternalSpan(Span span, Map<String, AnyValue> spanAttrMap,
      Map<String, AnyValue> resourceAttrMap) {
    List<RelationBuilder> result = new ArrayList<>(10);

    if (span.getLinksCount() > 0) {
      for (int i = 0; i < span.getLinksCount(); i++) {
        Span.Link link = span.getLinks(i);
        RelationBuilder sourceBuilder = new RelationBuilder();

        String serviceName =
            resourceAttrMap.get(ResourceAttributes.SERVICE_NAME.getKey()).getStringValue();
        String instanceName =
            resourceAttrMap.get(Const.OTLP_RESOURCE_SERVICE_INSTANCE_NAME).getStringValue();
        String networkAddressUsedAtPeer = null;
        String parentServiceName = null;
        String parentInstanceName = null;
        String parentEndpointName = Const.USER_ENDPOINT_NAME;

        for (KeyValue keyValue : link.getAttributesList()) {
          if (Const.SW_REF_NETWORK_ADDRESSUSEDATPEER.equals(keyValue.getKey())) {
            networkAddressUsedAtPeer = keyValue.getValue().getStringValue();
          } else if (Const.SW_REF_PARENT_SERVICE.equals(keyValue.getKey())) {
            parentServiceName = keyValue.getValue().getStringValue();
          } else if (Const.SW_REF_PARENT_SERVICE_INSTANCE_NAME.equals(keyValue.getKey())) {
            parentInstanceName = keyValue.getValue().getStringValue();
          } else if (Const.SW_REF_PARENT_ENDPOINT.equals(keyValue.getKey())) {
            parentEndpointName = keyValue.getValue().getStringValue();
          }
        }

        if (Span.SpanKind.SPAN_KIND_CONSUMER == span.getKind()) {
          sourceBuilder.setSourceServiceName(networkAddressUsedAtPeer);
          sourceBuilder.setSourceEndpointOwnerServiceName(parentServiceName);
          sourceBuilder.setSourceServiceInstanceName(networkAddressUsedAtPeer);
          sourceBuilder.setSourceLayer(Layer.VIRTUAL_MQ);
          sourceBuilder.setSourceEndpointOwnerServiceLayer(Layer.GENERAL);
        } else {
          sourceBuilder.setSourceServiceName(parentServiceName);
          sourceBuilder.setSourceServiceInstanceName(parentInstanceName);
          sourceBuilder.setSourceLayer(Layer.GENERAL);
        }

        sourceBuilder.setSourceEndpointName(parentEndpointName);
        sourceBuilder.setDestEndpointName(span.getName());
        sourceBuilder.setDestServiceInstanceName(instanceName);
        sourceBuilder.setDestServiceName(serviceName);
        sourceBuilder.setDestLayer(Layer.GENERAL);
        sourceBuilder.setDetectPoint(DetectPoint.SERVER);
        publicAttr.setPublicAttrs(sourceBuilder, span, spanAttrMap, resourceAttrMap);
        result.add(sourceBuilder);
      }
    }

    return result;
  }


  /**
   * get a mapping ip:port -> serviceName use for analysis service relation from exit span
   *
   * @param span
   * @return
   */
  public List<NetworkAddressMappingDO> generateNetworkAddressMapping(Span span,
      Map<String, AnyValue> spanAttrMap, Map<String, AnyValue> resourceAttrMap) {
    List<NetworkAddressMappingDO> networkAddressMappings = new ArrayList<>(10);

    for (Span.Link link : span.getLinksList()) {
      Map<String, AnyValue> linkAttrMap = TransformAttr.attList2Map(link.getAttributesList());
      String refType = linkAttrMap.get(Const.SW_REF_REFTYPE).getStringValue();
      if (!RefType.CrossProcess.name().equals(refType)) {
        continue;
      }

      String networkAddressUsedAtPeer =
          linkAttrMap.get(Const.SW_REF_NETWORK_ADDRESSUSEDATPEER).getStringValue();
      String serviceName =
          resourceAttrMap.get(ResourceAttributes.SERVICE_NAME.getKey()).getStringValue();
      String instanceName =
          resourceAttrMap.get(Const.OTLP_RESOURCE_SERVICE_INSTANCE_NAME).getStringValue();

      final NetworkAddressMapping networkAddressMapping = new NetworkAddressMapping();
      networkAddressMapping.setAddress(networkAddressUsedAtPeer);
      networkAddressMapping.setServiceName(serviceName);
      networkAddressMapping.setServiceNormal(true);
      networkAddressMapping.setServiceInstanceName(instanceName);
      networkAddressMapping.setTimeBucket(
          TimeBucket.getRecordTimeBucket(TimeUtils.unixNano2MS(span.getStartTimeUnixNano())));
      networkAddressMapping.prepare();

      // address mapping has existed
      NetworkAddressMappingDO old = networkAddressMappingCache.get(networkAddressUsedAtPeer);
      if (old != null && networkAddressMapping.getServiceName().equals(old.getServiceName())) {
        continue;
      }

      networkAddressMappings
          .add(NetworkAddressMappingDO.fromNetworkAddressMapping(networkAddressMapping));
    }

    return networkAddressMappings;
  }



  public Layer identifyRemoteServiceLayer(String spanLayer) {
    switch (spanLayer) {
      case "Unknown":
      case "UNRECOGNIZED":
        return Layer.UNDEFINED;
      case "Database":
        return Layer.VIRTUAL_DATABASE;
      case "RPCFramework":
      case "Http":
        return Layer.GENERAL;
      case "MQ":
        return Layer.VIRTUAL_MQ;
      case "Cache":
        return Layer.VIRTUAL_CACHE;
      case "FAAS":
        return Layer.FAAS;
      default:
        throw new UnexpectedException("Can't transfer to the Layer. SpanLayer=" + spanLayer);
    }
  }

}
