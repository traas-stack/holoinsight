/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.specification.sw;

import io.holoinsight.server.apm.common.utils.IDManager;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;


public class ServiceInstance extends Source {
  private volatile String entityId;

  @Override
  public String getEntityId() {
    if (entityId == null) {
      entityId = IDManager.ServiceInstanceID.buildId(serviceId, name);
    }
    return entityId;
  }

  @Getter
  private String serviceId;
  @Getter
  @Setter
  private String name;
  @Getter
  @Setter
  private String serviceName;
  @Getter
  @Setter
  private Layer serviceLayer;
  @Getter
  @Setter
  private String endpointName;
  @Getter
  @Setter
  private int latency;
  @Getter
  @Setter
  private int traceStatus;
  @Getter
  @Setter
  private int httpResponseStatusCode;
  @Getter
  @Setter
  private String rpcStatusCode;
  @Getter
  @Setter
  private String type;
  @Getter
  @Setter
  private List<String> tags;
  @Setter
  private Map<String, String> originalTags;

  @Override
  public void prepare() {
    serviceId = IDManager.ServiceID.buildId(serviceName, serviceLayer.isNormal());
  }

  public String getTag(String key) {
    return originalTags.get(key);
  }
}
