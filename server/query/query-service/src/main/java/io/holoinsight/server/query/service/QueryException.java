/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.query.service;

/**
 * <p>
 * created at 2022/11/26
 *
 * @author xiangwanpeng
 */
public class QueryException extends Exception {

  public QueryException(String message) {
    super(message);
  }

  public QueryException(String message, Throwable t) {
    super(message, t);
  }
}
