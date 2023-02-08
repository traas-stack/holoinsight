/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.exception;

/**
 * holoinsight alert internal exception
 *
 * @author masaimu
 * @version 2023-01-18 14:53:00
 */
public class HoloinsightAlertInternalException extends RuntimeException {
  public HoloinsightAlertInternalException() {}

  public HoloinsightAlertInternalException(String message) {
    super(message);
  }

  public HoloinsightAlertInternalException(String message, Throwable cause) {
    super(message, cause);
  }

  public HoloinsightAlertInternalException(Throwable cause) {
    super(cause);
  }
}
