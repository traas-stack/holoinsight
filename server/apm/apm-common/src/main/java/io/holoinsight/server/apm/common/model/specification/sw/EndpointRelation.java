/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.specification.sw;

import io.holoinsight.server.apm.common.utils.IDManager;
import lombok.Getter;
import lombok.Setter;

public class EndpointRelation extends Source {

  @Override
  public String getEntityId() {
    return IDManager.EndpointID.buildRelationId(new IDManager.EndpointID.EndpointRelationDefine(
        sourceServiceId, sourceEndpointName, destServiceId, destEndpointName));
  }

  @Getter
  private String sourceServiceId;
  @Getter
  @Setter
  private String sourceServiceName;
  @Getter
  @Setter
  private String sourceServiceInstanceName;
  @Getter
  @Setter
  private String sourceEndpointName;
  @Getter
  @Setter
  private String sourceEndpointId;
  @Getter
  @Setter
  private Layer sourceLayer;
  @Getter
  private String destServiceId;
  @Getter
  @Setter
  private String destServiceName;
  @Getter
  @Setter
  private Layer destLayer;
  @Getter
  @Setter
  private String destServiceInstanceName;
  @Getter
  @Setter
  private String destEndpointName;
  @Getter
  @Setter
  private String destEndpointId;
  @Getter
  @Setter
  private String component;
  @Getter
  @Setter
  private int latency;
  @Getter
  @Setter
  private int traceStatus;
  @Getter
  @Setter
  private String httpResponseStatusCode;
  @Getter
  @Setter
  private String rpcStatusCode;
  @Getter
  @Setter
  private String type;
  @Getter
  @Setter
  private DetectPoint detectPoint;
  @Getter
  private String entityId;

  @Override
  public void prepare() {
    sourceServiceId = IDManager.ServiceID.buildId(sourceServiceName, sourceLayer.isNormal());
    destServiceId = IDManager.ServiceID.buildId(destServiceName, destLayer.isNormal());
    sourceEndpointId = IDManager.EndpointID.buildId(sourceServiceId, sourceEndpointName);
    destEndpointId = IDManager.EndpointID.buildId(destServiceId, destEndpointName);
    entityId = IDManager.EndpointID.buildRelationId(new IDManager.EndpointID.EndpointRelationDefine(
        sourceServiceId, sourceEndpointName, sourceServiceId, destEndpointName));
  }

}

