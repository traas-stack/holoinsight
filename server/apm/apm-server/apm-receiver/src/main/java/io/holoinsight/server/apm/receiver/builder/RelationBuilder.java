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
import org.apache.commons.lang3.StringUtils;

public class RelationBuilder extends DetailInfo {
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

  public ServiceRelation toServiceRelation() {
    ServiceRelation serviceRelation = new ServiceRelation();
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
    if (!StringUtils.isEmpty(appId)) {
      serviceRelation.setAppId(appId);
    }
    if (!StringUtils.isEmpty(envId)) {
      serviceRelation.setEnvId(envId);
    }
    if (!StringUtils.isEmpty(stamp)) {
      serviceRelation.setStamp(stamp);
    }
    serviceRelation.setEnvId(envId);
    serviceRelation.prepare();
    return serviceRelation;
  }


  public ServiceInstanceRelation toServiceInstanceRelation() {
    if (StringUtils.isEmpty(sourceServiceInstanceName)
        || StringUtils.isEmpty(destServiceInstanceName)) {
      return null;
    }
    ServiceInstanceRelation serviceInstanceRelation = new ServiceInstanceRelation();
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
    if (!StringUtils.isEmpty(appId)) {
      serviceInstanceRelation.setAppId(appId);
    }
    if (!StringUtils.isEmpty(envId)) {
      serviceInstanceRelation.setEnvId(envId);
    }
    if (!StringUtils.isEmpty(stamp)) {
      serviceInstanceRelation.setStamp(stamp);
    }
    serviceInstanceRelation.prepare();
    return serviceInstanceRelation;
  }

  public EndpointRelation toEndpointRelation() {
    if (StringUtils.isEmpty(sourceEndpointName) || StringUtils.isEmpty(destEndpointName)) {
      return null;
    }
    EndpointRelation endpointRelation = new EndpointRelation();
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
    if (!StringUtils.isEmpty(appId)) {
      endpointRelation.setAppId(appId);
    }
    if (!StringUtils.isEmpty(envId)) {
      endpointRelation.setEnvId(envId);
    }
    if (!StringUtils.isEmpty(stamp)) {
      endpointRelation.setStamp(stamp);
    }
    endpointRelation.prepare();
    return endpointRelation;
  }

}
