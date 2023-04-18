/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.specification.sw;

import lombok.Getter;
import lombok.Setter;

public abstract class Source implements ISource {

  @Getter
  @Setter
  private long timeBucket;
  @Getter
  @Setter
  private long startTime;
  @Getter
  @Setter
  private long endTime;
  @Getter
  @Setter
  private String tenant;
  @Getter
  @Setter
  private String traceId;
}
