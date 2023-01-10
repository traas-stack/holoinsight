/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc.streambiz;

import io.holoinsight.server.registry.core.grpc.stream.Cmd;
import io.holoinsight.server.registry.core.grpc.stream.RpcHandler;

import reactor.core.publisher.Mono;

/**
 * <p>
 * created at 2022/3/3
 *
 * @author zzhb101
 */
public class EchoRpcHandler implements RpcHandler {
  @Override
  public Mono<Cmd> handle(Cmd req) {
    return Mono.just(Cmd.of(BizTypes.ECHO, req.getData()));
  }
}
