/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc.stream;

import com.google.protobuf.ByteString;

import lombok.Data;

/**
 * biz cmd
 * <p>
 * created at 2022/3/3
 *
 * @author zzhb101
 */
@Data
public final class Cmd {
  final int type;
  final ByteString data;

  public static Cmd of(int type, ByteString data) {
    return new Cmd(type, data);
  }
}
