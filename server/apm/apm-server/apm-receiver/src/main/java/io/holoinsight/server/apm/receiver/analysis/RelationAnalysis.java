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
import io.holoinsight.server.apm.grpc.trace.RefType;
import io.holoinsight.server.apm.receiver.builder.RPCTrafficSourceBuilder;
import io.holoinsight.server.apm.receiver.common.IPublicAttr;
import io.holoinsight.server.apm.receiver.common.TransformAttr;
import io.holoinsight.server.apm.server.cache.NetworkAddressAliasCache;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.trace.v1.Span;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class RelationAnalysis {

  @Autowired
  private NetworkAddressAliasCache networkAddressMappingCache;

  @Autowired
  private IPublicAttr publicAttr;

  public RPCTrafficSourceBuilder relationBuilder() {
    return new RPCTrafficSourceBuilder();
  }

  public List<RPCTrafficSourceBuilder> analysisServerSpan(Span span,
      Map<String, String> spanAttrMap, Map<String, String> resourceAttrMap) {
    List<RPCTrafficSourceBuilder> callingInTraffic = new ArrayList<>(10);

    String serviceName = resourceAttrMap.get(ResourceAttributes.SERVICE_NAME.getKey());
    String instanceName = resourceAttrMap.get(Const.OTLP_RESOURCE_SERVICE_INSTANCE_NAME);

    if (span.getLinksCount() > 0) {
      for (Span.Link link : span.getLinksList()) {
        RPCTrafficSourceBuilder sourceBuilder = relationBuilder();

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
        callingInTraffic
            .add(publicAttr.setPublicAttrs(sourceBuilder, span, spanAttrMap, resourceAttrMap));
      }
    } else {
      RPCTrafficSourceBuilder sourceBuilder = relationBuilder();
      sourceBuilder.setSourceServiceName(Const.USER_SERVICE_NAME);
      sourceBuilder.setSourceServiceInstanceName(Const.USER_INSTANCE_NAME);
      sourceBuilder.setSourceEndpointName(Const.USER_ENDPOINT_NAME);
      sourceBuilder.setSourceLayer(Layer.UNDEFINED);
      sourceBuilder.setDestServiceInstanceName(instanceName);
      sourceBuilder.setDestServiceName(serviceName);
      sourceBuilder.setDestLayer(Layer.GENERAL);
      sourceBuilder.setDestEndpointName(span.getName());
      sourceBuilder.setDetectPoint(DetectPoint.SERVER);

      callingInTraffic
          .add(publicAttr.setPublicAttrs(sourceBuilder, span, spanAttrMap, resourceAttrMap));
    }

    return callingInTraffic;
  }

  public List<RPCTrafficSourceBuilder> analysisClientSpan(Span span,
      Map<String, String> spanAttrMap, Map<String, String> resourceAttrMap,
      Map<String, String> endpointMap) {
    List<RPCTrafficSourceBuilder> callingOutTraffic = new ArrayList<>(10);
    RPCTrafficSourceBuilder sourceBuilder = relationBuilder();

    String peerName = spanAttrMap.get(SemanticAttributes.NET_PEER_NAME.getKey());
    String peerPort = spanAttrMap.get(SemanticAttributes.NET_PEER_PORT.getKey());

    if (peerName == null || StringUtils.isEmpty(peerName)) {
      return callingOutTraffic;
    }

    String spanLayer = "Unknown";
    if (spanAttrMap.get(Const.OTLP_SPANLAYER) != null) {
      spanLayer = spanAttrMap.get(Const.OTLP_SPANLAYER);
    }
    String serviceName = resourceAttrMap.get(ResourceAttributes.SERVICE_NAME.getKey());
    String instanceName = resourceAttrMap.get(Const.OTLP_RESOURCE_SERVICE_INSTANCE_NAME);

    String networkAddress = peerPort == null ? peerName : peerName + ":" + peerPort;
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
    callingOutTraffic
        .add(publicAttr.setPublicAttrs(sourceBuilder, span, spanAttrMap, resourceAttrMap));

    return callingOutTraffic;
  }


  public List<RPCTrafficSourceBuilder> analysisInternalSpan(Span span,
      Map<String, String> spanAttrMap, Map<String, String> resourceAttrMap) {
    List<RPCTrafficSourceBuilder> result = new ArrayList<>(10);

    if (span.getLinksCount() > 0) {
      for (int i = 0; i < span.getLinksCount(); i++) {
        Span.Link link = span.getLinks(i);
        RPCTrafficSourceBuilder sourceBuilder = relationBuilder();

        String serviceName = resourceAttrMap.get(ResourceAttributes.SERVICE_NAME.getKey());
        String instanceName = resourceAttrMap.get(Const.OTLP_RESOURCE_SERVICE_INSTANCE_NAME);
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
        result.add(publicAttr.setPublicAttrs(sourceBuilder, span, spanAttrMap, resourceAttrMap));
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
      Map<String, String> spanAttrMap, Map<String, String> resourceAttrMap) {
    List<NetworkAddressMappingDO> networkAddressMappings = new ArrayList<>(10);

    for (Span.Link link : span.getLinksList()) {
      Map<String, String> linkAttrMap = TransformAttr.attList2Map(link.getAttributesList());
      String refType = linkAttrMap.get(Const.SW_REF_REFTYPE);
      if (!RefType.CrossProcess.name().equals(refType)) {
        continue;
      }
      String networkAddress = linkAttrMap.get(Const.SW_REF_NETWORK_ADDRESSUSEDATPEER);
      String serviceInstance = resourceAttrMap.get(Const.OTLP_RESOURCE_SERVICE_INSTANCE_NAME);

      if (networkAddress == null || serviceInstance == null) {
        continue;
      }
      String networkAddressUsedAtPeer = networkAddress;
      String instanceName = serviceInstance;
      String serviceName = resourceAttrMap.get(ResourceAttributes.SERVICE_NAME.getKey());


      final NetworkAddressMapping networkAddressMapping = new NetworkAddressMapping();
      networkAddressMapping.setAddress(networkAddressUsedAtPeer);
      networkAddressMapping.setServiceName(serviceName);
      networkAddressMapping.setServiceNormal(true);
      networkAddressMapping.setServiceInstanceName(instanceName);
      networkAddressMapping.setTimeBucket(
          TimeBucket.getRecordTimeBucket(TimeUtils.unixNano2MS(span.getEndTimeUnixNano())));
      networkAddressMapping.setStartTime(TimeUtils.unixNano2MS(span.getStartTimeUnixNano()));
      networkAddressMapping.setEndTime(TimeUtils.unixNano2MS(span.getEndTimeUnixNano()));
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
