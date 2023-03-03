/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.specification.otel;

import io.opentelemetry.proto.trace.v1.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author jiwliu
 * @version : StatusCode.java, v 0.1 2022年10月29日 17:22 xiangwanpeng Exp $
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum StatusCode {
  /**
   * The default status.
   */
  UNSET(0),

  /**
   * The operation has been validated by an Application developers or Operator to have completed
   * successfully.
   */
  OK(1),

  /**
   * The operation contains an error.
   */
  ERROR(2);

  private int code;

  public static StatusCode fromProto(Status.StatusCode code) {
    switch (code) {
      case STATUS_CODE_OK:
        return OK;
      case STATUS_CODE_ERROR:
        return ERROR;
      default:
        return UNSET;
    }
  }
}
