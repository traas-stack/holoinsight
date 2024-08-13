/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.receiver.builder;

import io.holoinsight.server.apm.common.model.specification.sw.Layer;
import io.holoinsight.server.apm.common.model.specification.sw.EndpointRelation;
import io.holoinsight.server.apm.common.model.specification.sw.ServiceInstanceRelation;
import io.holoinsight.server.apm.common.model.specification.sw.ServiceRelation;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class RPCTrafficSourceBuilder extends EndpointSourceBuilder {
  @Getter
  @Setter
  private String sourceServiceName;

  @Getter
  @Setter
  private Layer sourceLayer;
  @Getter
  @Setter
  private String sourceServiceInstanceName;
  @Getter
  @Setter
  private Layer sourceEndpointOwnerServiceLayer;
  @Getter
  @Setter
  private String sourceEndpointOwnerServiceName;
  @Getter
  @Setter
  private String sourceEndpointName;
  @Getter
  @Setter
  private String component;

  public ServiceRelation serviceRelation() {
    return new ServiceRelation();
  }

  public void setServiceRelation(ServiceRelation serviceRelation) {
    serviceRelation.setTraceId(traceId);
    serviceRelation.setSourceServiceName(sourceServiceName);
    serviceRelation.setSourceServiceInstanceName(sourceServiceInstanceName);
    serviceRelation.setSourceLayer(sourceLayer);
    serviceRelation.setDestServiceName(destServiceName);
    serviceRelation.setDestServiceInstanceName(destServiceInstanceName);
    serviceRelation.setDestLayer(destLayer);
    serviceRelation.setEndpoint(destEndpointName);
    serviceRelation.setComponent(component);
    serviceRelation.setLatency(latency);
    serviceRelation.setTraceStatus(traceStatus);
    serviceRelation.setHttpResponseStatusCode(httpResponseStatusCode);
    serviceRelation.setRpcStatusCode(rpcStatusCode);
    serviceRelation.setType(type);
    serviceRelation.setDetectPoint(detectPoint);
    serviceRelation.setTimeBucket(timeBucket);
    serviceRelation.setTenant(tenant);
    serviceRelation.setStartTime(startTime);
    serviceRelation.setEndTime(endTime);
  }

  public ServiceRelation toServiceRelation() {
    if (StringUtils.isEmpty(sourceServiceName) || StringUtils.isEmpty(destServiceName)) {
      log.debug(
          "[apm] build service relation error, sourceServiceName or destServiceName is empty, traceId: {},"
              + " sourceServiceName: {}, destServiceName: {}",
          traceId, sourceServiceName, destServiceName);
      return null;
    }

    ServiceRelation serviceRelation = serviceRelation();
    setServiceRelation(serviceRelation);
    serviceRelation.prepare();
    return serviceRelation;
  }

  public ServiceInstanceRelation serviceInstanceRelation() {
    return new ServiceInstanceRelation();
  }

  public void setServiceInstanceRelation(ServiceInstanceRelation serviceInstanceRelation) {
    serviceInstanceRelation.setTraceId(traceId);
    serviceInstanceRelation.setSourceServiceName(sourceServiceName);
    serviceInstanceRelation.setSourceServiceInstanceName(sourceServiceInstanceName);
    serviceInstanceRelation.setSourceServiceLayer(sourceLayer);
    serviceInstanceRelation.setDestServiceName(destServiceName);
    serviceInstanceRelation.setDestServiceInstanceName(destServiceInstanceName);
    serviceInstanceRelation.setDestServiceLayer(destLayer);
    serviceInstanceRelation.setEndpoint(destEndpointName);
    serviceInstanceRelation.setComponent(component);
    serviceInstanceRelation.setLatency(latency);
    serviceInstanceRelation.setTraceStatus(traceStatus);
    serviceInstanceRelation.setHttpResponseStatusCode(httpResponseStatusCode);
    serviceInstanceRelation.setRpcStatusCode(rpcStatusCode);
    serviceInstanceRelation.setType(type);
    serviceInstanceRelation.setDetectPoint(detectPoint);
    serviceInstanceRelation.setTimeBucket(timeBucket);
    serviceInstanceRelation.setTenant(tenant);
    serviceInstanceRelation.setStartTime(startTime);
    serviceInstanceRelation.setEndTime(endTime);
  }

  public ServiceInstanceRelation toServiceInstanceRelation() {
    if (StringUtils.isEmpty(sourceServiceName) || StringUtils.isEmpty(destServiceName)
        || StringUtils.isEmpty(sourceServiceInstanceName)
        || StringUtils.isEmpty(destServiceInstanceName)) {
      log.debug(
          "[apm] build service instance relation error, sourceServiceInstanceName or destServiceInstanceName is empty, traceId: {},"
              + " sourceServiceName: {}, destServiceName: {}"
              + " sourceServiceInstanceName: {}, destServiceInstanceName: {}",
          traceId, sourceServiceName, destServiceName, sourceServiceInstanceName,
          destServiceInstanceName);
      return null;
    }
    ServiceInstanceRelation serviceInstanceRelation = serviceInstanceRelation();
    setServiceInstanceRelation(serviceInstanceRelation);
    serviceInstanceRelation.prepare();
    return serviceInstanceRelation;
  }

  public EndpointRelation endpointRelation() {
    return new EndpointRelation();
  }

  public void setEndpointRelation(EndpointRelation endpointRelation) {
    endpointRelation.setTraceId(traceId);
    endpointRelation.setSourceEndpointName(sourceEndpointName);
    if (sourceEndpointOwnerServiceName == null) {
      endpointRelation.setSourceServiceName(sourceServiceName);
      endpointRelation.setSourceLayer(sourceLayer);
    } else {
      endpointRelation.setSourceServiceName(sourceEndpointOwnerServiceName);
      endpointRelation.setSourceLayer(sourceEndpointOwnerServiceLayer);
    }
    endpointRelation.setSourceServiceInstanceName(sourceServiceInstanceName);
    endpointRelation.setDestEndpointName(destEndpointName);
    endpointRelation.setDestServiceName(destServiceName);
    endpointRelation.setDestLayer(destLayer);
    endpointRelation.setDestServiceInstanceName(destServiceInstanceName);
    endpointRelation.setComponent(component);
    endpointRelation.setLatency(latency);
    endpointRelation.setTraceStatus(traceStatus);
    endpointRelation.setHttpResponseStatusCode(httpResponseStatusCode);
    endpointRelation.setRpcStatusCode(rpcStatusCode);
    endpointRelation.setType(type);
    endpointRelation.setDetectPoint(detectPoint);
    endpointRelation.setTimeBucket(timeBucket);
    endpointRelation.setTenant(tenant);
    endpointRelation.setStartTime(startTime);
    endpointRelation.setEndTime(endTime);
  }

  public EndpointRelation toEndpointRelation() {
    if (StringUtils.isEmpty(sourceServiceName) || StringUtils.isEmpty(destServiceName)
        || StringUtils.isEmpty(sourceEndpointName) || StringUtils.isEmpty(destEndpointName)) {
      log.debug(
          "[apm] build endpoint relation error, sourceEndpointName or destEndpointName is empty, traceId: {},"
              + " sourceServiceName: {}, destServiceName: {}"
              + " sourceEndpointName: {}, destEndpointName: {}",
          traceId, sourceServiceName, destServiceName, sourceEndpointName, destEndpointName);
      return null;
    }
    EndpointRelation endpointRelation = endpointRelation();
    setEndpointRelation(endpointRelation);
    endpointRelation.prepare();
    return endpointRelation;
  }

}
