/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util.http;

public class HttpException extends Exception {

  /**  */
  private static final long serialVersionUID = -7086098942444546040L;

  public HttpException(String msg) {
    super(msg);
  }

  public HttpException(String msg, Throwable e) {
    super(msg, e);
  }
}
