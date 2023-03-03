/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.specification.sw;

import io.holoinsight.server.apm.common.utils.IDManager;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

public class Service extends Source {
  private volatile String entityId;

  @Override
  public String getEntityId() {
    if (entityId == null) {
      entityId = IDManager.ServiceID.buildId(name, layer.isNormal());
    }
    return entityId;
  }

  @Getter
  @Setter
  private String name;
  @Setter
  @Getter
  private Layer layer;
  @Getter
  @Setter
  private String serviceInstanceName;
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
  @Getter
  @Setter
  private long timeBucket;

  public String getTag(String key) {
    return originalTags.get(key);
  }
}
