/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.specification.sw;

import io.holoinsight.server.apm.common.utils.IDManager;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

public class ServiceRelation extends Source {

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
  @Getter
  @Setter
  private long timeBucket;
  @Getter
  private String entityId;

  @Override
  public String getEntityId() {
    if (StringUtils.isEmpty(entityId)) {
      entityId = IDManager.ServiceID.buildRelationId(
          new IDManager.ServiceID.ServiceRelationDefine(sourceServiceId, destServiceId));
    }
    return entityId;
  }

  @Override
  public void prepare() {
    sourceServiceId = IDManager.ServiceID.buildId(sourceServiceName, sourceLayer.isNormal());
    destServiceId = IDManager.ServiceID.buildId(destServiceName, destLayer.isNormal());
    entityId = IDManager.ServiceID.buildRelationId(
        new IDManager.ServiceID.ServiceRelationDefine(sourceServiceId, destServiceId));
  }

}
