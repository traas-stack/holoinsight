/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.specification.sw;

import io.holoinsight.server.apm.common.constants.Const;
import lombok.Getter;
import lombok.Setter;

public class SlowSql extends Source {
  @Getter
  @Setter
  private String serviceName;
  @Getter
  @Setter
  private String address;
  @Getter
  @Setter
  private String statement;
  @Getter
  @Setter
  private long latency;

  @Override
  public String getEntityId() {
    return Const.EMPTY_STRING;
  }

}
