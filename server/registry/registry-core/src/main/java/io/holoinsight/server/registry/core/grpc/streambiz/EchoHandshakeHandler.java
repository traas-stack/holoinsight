/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc.streambiz;

import io.holoinsight.server.common.grpc.CommonResponseHeader;
import io.holoinsight.server.common.grpc.GenericRpcCommand;
import io.holoinsight.server.registry.core.grpc.stream.HandshakeContext;
import io.holoinsight.server.registry.core.grpc.stream.HandshakeHandler;
import io.holoinsight.server.registry.core.grpc.stream.RpcCmds;
import io.holoinsight.server.registry.core.grpc.stream.Stream;
import io.holoinsight.server.registry.grpc.agent.BiStreamClientHandshakeRequest;
import io.holoinsight.server.registry.grpc.agent.BiStreamClientHandshakeResponse;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import reactor.core.publisher.Mono;

/**
 * <p>
 * created at 2022/3/3
 *
 * @author zzhb101
 */
public class EchoHandshakeHandler implements HandshakeHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(EchoHandshakeHandler.class);
  @Getter
  private BiStreamClientHandshakeRequest request;

  @Override
  public Mono<GenericRpcCommand> handle(GenericRpcCommand reqCmd, HandshakeContext ctx) {
    LOGGER.info("服务端收到客户端的握手请求 [{}]", reqCmd.getData().toStringUtf8());

    try {
      request = BiStreamClientHandshakeRequest.parseFrom(reqCmd.getData());
    } catch (InvalidProtocolBufferException e) {
      return Mono.error(e);
    }

    // TODO auth with req

    ByteString respData = BiStreamClientHandshakeResponse.newBuilder() //
        .setHeader(CommonResponseHeader.newBuilder() //
            .setCode(0) //
            .setMessage("OK") //
            .build())
        .build() //
        .toByteString(); //

    GenericRpcCommand resp = RpcCmds.create(Stream.TYPE_SERVER_HAND_SHAKE, 0, 0, respData);
    return Mono.just(resp);
  }
}
