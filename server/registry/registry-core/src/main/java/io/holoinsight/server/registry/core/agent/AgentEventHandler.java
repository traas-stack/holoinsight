/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.agent;

import io.holoinsight.server.common.auth.AuthInfo;
import io.holoinsight.server.registry.grpc.agent.ReportEventRequest;

/**
 * <p>
 * created at 2023/9/30
 *
 * @author xzchaoo
 */
public interface AgentEventHandler {
  void handle(AuthInfo ai, ReportEventRequest request);
}
