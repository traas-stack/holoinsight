/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.specification.sw;

import io.holoinsight.server.apm.grpc.trace.SpanType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DetectPoint {
  SERVER(0), CLIENT(1), PROXY(2), UNRECOGNIZED(3);

  private final int value;

  public static DetectPoint fromSpanType(SpanType spanType) {
    switch (spanType) {
      case Entry:
        return DetectPoint.SERVER;
      case Exit:
        return DetectPoint.CLIENT;
      case UNRECOGNIZED:
      case Local:
      default:
        return DetectPoint.UNRECOGNIZED;
    }
  }

  public static DetectPoint valueOf(int value) {
    switch (value) {
      case 0:
        return SERVER;
      case 1:
        return CLIENT;
      case 2:
        return PROXY;
      default:
        return UNRECOGNIZED;
    }
  }

  public int value() {
    return this.value;
  }
}
