/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.specification.sw;

import io.holoinsight.server.apm.common.utils.IDManager;
import lombok.Getter;
import lombok.Setter;

public class EndpointMeta extends Source {

  @Getter
  private String serviceId;
  @Getter
  @Setter
  private String serviceName;
  @Setter
  @Getter
  private boolean isServiceNormal;
  @Getter
  @Setter
  private String endpoint;

  @Override
  public String getEntityId() {
    return IDManager.EndpointID.buildId(serviceId, endpoint);
  }

  @Override
  public void prepare() {
    this.serviceId = IDManager.ServiceID.buildId(serviceName, isServiceNormal);
  }
}
