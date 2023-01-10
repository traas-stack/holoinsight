/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc;

/**
 * <p>
 * created at 2022/3/11
 *
 * @author zzhb101
 */
public final class RpcCodes {
  public static final int OK = 0;
  public static final int RESOURCE_NOT_FOUND = 404;
  public static final int INTERNAL = 500;

  private RpcCodes() {}
}
