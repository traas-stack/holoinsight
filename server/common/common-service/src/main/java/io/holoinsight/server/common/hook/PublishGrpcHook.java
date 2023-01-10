/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.hook;

import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;

/**
 * <p>
 * created at 2022/12/6
 *
 * @author xzchaoo
 */
public interface PublishGrpcHook {
  void onPublish(ServerBuilder<?> b, ServerServiceDefinition ssd);
}
