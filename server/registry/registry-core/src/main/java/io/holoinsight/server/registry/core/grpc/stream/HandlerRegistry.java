/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc.stream;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * created at 2022/3/3
 *
 * @author zzhb101
 */
public class HandlerRegistry {
  private Map<Integer, OnewayHandler> oneway = new HashMap<>();
  private Map<Integer, RpcHandler> rpc = new HashMap<>();

  public void registerOneway(int bizType, OnewayHandler h) {
    oneway.put(bizType, h);
  }

  public void registerRpc(int bizType, RpcHandler h) {
    rpc.put(bizType, h);
  }

  public OnewayHandler lookupOneway(int bizType) {
    return oneway.get(bizType);
  }

  public RpcHandler lookupRpc(int bizType) {
    return rpc.get(bizType);
  }
}
