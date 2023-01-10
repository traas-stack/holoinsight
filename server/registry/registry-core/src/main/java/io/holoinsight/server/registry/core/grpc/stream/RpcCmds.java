/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc.stream;

import io.holoinsight.server.common.grpc.GenericRpcCommand;
import com.google.protobuf.ByteString;

/**
 * <p>
 * created at 2022/3/3
 *
 * @author zzhb101
 */
public final class RpcCmds {
  private RpcCmds() {}

  public static GenericRpcCommand oneway(int bizType, ByteString data) {
    return create(Stream.TYPE_ONEWAY, 0, bizType, data);
  }

  public static GenericRpcCommand req(long reqId, int bizType, ByteString data) {
    return create(Stream.TYPE_REQ, reqId, bizType, data);
  }

  public static GenericRpcCommand resp(long reqId, int bizType, ByteString data) {
    return create(Stream.TYPE_RESP, reqId, bizType, data);
  }

  public static GenericRpcCommand create(int rpcType, long reqId, int bizType, ByteString data) {
    return GenericRpcCommand.newBuilder() //
        .setRpcType(rpcType) //
        .setReqId(reqId) //
        .setBizType(bizType) //
        .setData(data) //
        .build(); //
  }
}
