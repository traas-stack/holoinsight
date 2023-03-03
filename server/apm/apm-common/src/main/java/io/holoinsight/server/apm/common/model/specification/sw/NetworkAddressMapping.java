/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.specification.sw;

import io.holoinsight.server.apm.common.utils.IDManager;
import lombok.Getter;
import lombok.Setter;

public class NetworkAddressMapping extends Source {

  @Setter
  @Getter
  private String address;
  @Getter
  @Setter
  private String serviceName;
  @Getter
  @Setter
  private boolean isServiceNormal;
  @Getter
  @Setter
  private String serviceInstanceName;
  @Getter
  private String serviceId;
  @Getter
  private String serviceInstanceId;

  @Override
  public String getEntityId() {
    return null;
  }

  @Override
  public void prepare() {
    serviceId = IDManager.ServiceID.buildId(serviceName, isServiceNormal);
    serviceInstanceId = IDManager.ServiceInstanceID.buildId(serviceId, serviceInstanceName);
  }

}
