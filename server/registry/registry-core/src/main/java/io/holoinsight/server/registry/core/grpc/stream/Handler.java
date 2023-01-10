/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc.stream;

import java.util.concurrent.Executor;

/**
 * <p>
 * created at 2022/3/3
 *
 * @author zzhb101
 */
public interface Handler {
  int executeType();

  Executor executor();
}
