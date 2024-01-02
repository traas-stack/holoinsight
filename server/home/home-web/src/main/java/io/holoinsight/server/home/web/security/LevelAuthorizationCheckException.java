/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.security;

/**
 * @author masaimu
 * @version 2023-12-28 19:11:00
 */
public class LevelAuthorizationCheckException extends RuntimeException {

  public LevelAuthorizationCheckException(String message) {
    super("SecurityCheckFailed:" + message);
  }
}
