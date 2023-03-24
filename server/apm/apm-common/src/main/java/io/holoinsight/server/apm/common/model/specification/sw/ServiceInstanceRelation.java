/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.specification.sw;

import io.holoinsight.server.apm.common.utils.IDManager;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

public class ServiceInstanceRelation extends Source {
  @Getter
  private String entityId;

  @Override
  public String getEntityId() {
    if (StringUtils.isEmpty(entityId)) {
      entityId = IDManager.ServiceInstanceID.buildRelationId(
          new IDManager.ServiceInstanceID.ServiceInstanceRelationDefine(sourceServiceInstanceId,
              destServiceInstanceId));
    }
    return entityId;
  }

  @Getter
  private String sourceServiceInstanceId;
  @Getter
  private String sourceServiceId;
  @Getter
  @Setter
  private String sourceServiceName;
  @Getter
  @Setter
  private Layer sourceServiceLayer;
  @Getter
  @Setter
  private String sourceServiceInstanceName;
  @Getter
  private String destServiceInstanceId;
  @Getter
  private String destServiceId;
  @Getter
  @Setter
  private Layer destServiceLayer;
  @Getter
  @Setter
  private String destServiceName;
  @Getter
  @Setter
  private String destServiceInstanceName;
  @Getter
  @Setter
  private String endpoint;
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
  @Deprecated
  private int responseCode;
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
  @Setter
  private String tlsMode;

  @Override
  public void prepare() {
    sourceServiceId = IDManager.ServiceID.buildId(sourceServiceName, sourceServiceLayer.isNormal());
    destServiceId = IDManager.ServiceID.buildId(destServiceName, destServiceLayer.isNormal());
    sourceServiceInstanceId =
        IDManager.ServiceInstanceID.buildId(sourceServiceId, sourceServiceInstanceName);
    destServiceInstanceId =
        IDManager.ServiceInstanceID.buildId(destServiceId, destServiceInstanceName);
    entityId = IDManager.ServiceInstanceID.buildRelationId(
        new IDManager.ServiceInstanceID.ServiceInstanceRelationDefine(sourceServiceInstanceId,
            destServiceInstanceId));
  }

}
