/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc.stream;


import io.holoinsight.server.common.grpc.GenericRpcCommand;

import reactor.core.publisher.Mono;

/**
 * <p>
 * created at 2022/3/3
 *
 * @author zzhb101
 */
public interface HandshakeHandler {
  Mono<GenericRpcCommand> handle(GenericRpcCommand cmd, HandshakeContext ctx);
}
