/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.facade.model;

/**
 *
 * @author jsy1001de
 * @version 1.0: ClientException.java, v 0.1 2022年03月07日 5:43 下午 jinsong.yjs Exp $
 */
public class ClientException extends RuntimeException {
  private static final long serialVersionUID = 7313883634332771808L;

  public ClientException() {
    super();
  }

  public ClientException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public ClientException(String message, Throwable cause) {
    super(message, cause);
  }

  public ClientException(String message) {
    super(message);
  }

  public ClientException(String fmt, Object... args) {
    super(String.format(fmt, args));
  }

  public ClientException(Throwable cause) {
    super(cause);
  }

}
