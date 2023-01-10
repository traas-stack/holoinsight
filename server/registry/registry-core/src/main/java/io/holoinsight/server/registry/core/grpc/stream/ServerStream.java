/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc.stream;

import io.holoinsight.server.common.grpc.GenericRpcCommand;
import com.google.protobuf.ByteString;

import io.grpc.stub.StreamObserver;
import reactor.core.publisher.Mono;

/**
 * <p>
 * created at 2022/3/3
 *
 * @author zzhb101
 */
public interface ServerStream extends Stream {

  Mono<GenericRpcCommand> rpc(int bizType, ByteString data);

  void oneway(int bizType, ByteString data);

  StreamObserver<GenericRpcCommand> getReader();
}
