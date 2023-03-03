/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.specification.sw;

import io.holoinsight.server.apm.common.utils.IDManager;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceMeta extends Source {

  @Override
  public String getEntityId() {
    return IDManager.ServiceID.buildId(name, layer.isNormal());
  }

  private String name;
  private Layer layer;

}
