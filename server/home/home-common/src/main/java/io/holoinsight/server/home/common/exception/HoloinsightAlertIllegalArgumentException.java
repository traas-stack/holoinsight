/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.exception;

/**
 * alert illegal argument exception
 *
 * @author masaimu
 * @version 2023-01-18 14:54:00
 */
public class HoloinsightAlertIllegalArgumentException extends RuntimeException {
  public HoloinsightAlertIllegalArgumentException() {}

  public HoloinsightAlertIllegalArgumentException(String message) {
    super(message);
  }

  public HoloinsightAlertIllegalArgumentException(String message, Throwable cause) {
    super(message, cause);
  }
}
