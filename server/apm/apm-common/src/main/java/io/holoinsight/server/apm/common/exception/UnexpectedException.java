/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.exception;

public class UnexpectedException extends RuntimeException {
  public UnexpectedException(String message) {
    super(message);
  }

  public UnexpectedException(String message, Exception cause) {
    super(message, cause);
  }
}
