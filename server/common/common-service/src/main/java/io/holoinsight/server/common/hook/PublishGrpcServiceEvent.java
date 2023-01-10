/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.hook;

import io.grpc.ServerBuilder;
import io.grpc.ServerServiceDefinition;
import lombok.Getter;

import org.springframework.context.ApplicationEvent;

/**
 * <p>
 * created at 2022/12/6
 *
 * @author xzchaoo
 */
@Getter
public class PublishGrpcServiceEvent extends ApplicationEvent {
  private final ServerBuilder<?> serverBuilder;
  private final ServerServiceDefinition serverServiceDefinition;

  public PublishGrpcServiceEvent(Object source, ServerBuilder<?> serverBuilder,
      ServerServiceDefinition serverServiceDefinition) {
    super(source);
    this.serverBuilder = serverBuilder;
    this.serverServiceDefinition = serverServiceDefinition;
  }
}
