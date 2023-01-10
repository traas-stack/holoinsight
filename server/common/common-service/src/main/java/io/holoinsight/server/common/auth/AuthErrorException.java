/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.auth;

/**
 * <p>
 * created at 2022/6/21
 *
 * @author xzchaoo
 */
public class AuthErrorException extends RuntimeException {
  public AuthErrorException(String message) {
    super(message);
  }
}
