/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc.stream;

import reactor.core.publisher.Mono;

/**
 * <p>
 * created at 2022/3/3
 *
 * @author zzhb101
 */
public interface Stream {
  int TYPE_ONEWAY = 0;
  int TYPE_REQ = 1;
  int TYPE_RESP = 2;
  int TYPE_CLIENT_HAND_SHAKE = 3;
  int TYPE_SERVER_HAND_SHAKE = 4;

  void start();

  void stop();

  Mono<Void> waitHandshakeDone();
}
