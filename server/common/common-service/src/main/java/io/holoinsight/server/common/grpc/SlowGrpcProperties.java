/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.grpc;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * created at 2023/5/17
 *
 * @author xzchaoo
 */
@ConfigurationProperties(prefix = "grpc.slow")
@Getter
@Setter
public class SlowGrpcProperties {
  private boolean enabled = true;
  private long recvThreshold = 2500;
  private long handleThreshold = 2500;
}
