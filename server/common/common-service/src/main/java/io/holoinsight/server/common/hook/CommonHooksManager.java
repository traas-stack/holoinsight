/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.hook;

import java.util.ArrayList;
import java.util.List;

import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Common hooks(callbacks) manager. Hooks can be used for extensions.
 * <p>
 * created at 2022/12/6
 *
 * @author xzchaoo
 */
public class CommonHooksManager {
  @Autowired(required = false)
  private List<PublishGrpcHook> publishGrpcHooks = new ArrayList<>();

  public void triggerPublishGrpcHooks(ServerBuilder<?> b, ServerServiceDefinition ssd) {
    for (PublishGrpcHook hook : publishGrpcHooks) {
      hook.onPublish(b, ssd);
    }
  }
}
