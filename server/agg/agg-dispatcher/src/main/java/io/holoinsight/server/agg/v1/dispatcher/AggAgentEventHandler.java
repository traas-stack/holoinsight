/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.dispatcher;

import org.springframework.beans.factory.annotation.Autowired;

import io.holoinsight.server.common.auth.AuthInfo;
import io.holoinsight.server.registry.core.agent.AgentEventHandler;
import io.holoinsight.server.registry.grpc.agent.ReportEventRequest;

/**
 * <p>
 * created at 2023/9/30
 *
 * @author xzchaoo
 */
public class AggAgentEventHandler implements AgentEventHandler {
  @Autowired
  private AggDispatcher aggDispatcher;

  @Override
  public void handle(AuthInfo ai, ReportEventRequest request) {
    aggDispatcher.dispatchUpEvent(ai, request);
  }
}
