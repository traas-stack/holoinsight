/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.grpc;

/**
 * <p>
 * created at 2022/3/4
 *
 * @author sw1136562366
 */
public final class Codes {
  /** Constant <code>OK=0</code> */
  public static final int OK = 0;
  /**
   * 非法apikey
   */
  public static final int UNAUTHENTICATED = 401;
  /**
   * 内部错误
   */
  public static final int INTERNAL_ERROR = 500;

  private Codes() {}
}
