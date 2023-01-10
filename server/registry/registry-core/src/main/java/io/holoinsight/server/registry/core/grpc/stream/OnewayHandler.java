/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc.stream;

/**
 * <p>
 * created at 2022/3/3
 *
 * @author zzhb101
 */
public interface OnewayHandler {
  void handle(Cmd cmd);
}
