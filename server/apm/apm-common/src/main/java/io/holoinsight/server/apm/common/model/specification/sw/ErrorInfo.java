/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.specification.sw;

import lombok.Data;

@Data
public class ErrorInfo extends Source {

  private String spanId;
  private String errorKind;
  private String serviceName;
  private String serviceInstanceName;

  @Override
  public String getEntityId() {
    return null;
  }

  @Override
  public void prepare() {
    super.prepare();
  }
}
