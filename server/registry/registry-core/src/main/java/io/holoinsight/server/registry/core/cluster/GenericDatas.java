/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.cluster;

import io.holoinsight.server.common.grpc.GenericData;
import com.google.protobuf.ByteString;

/**
 * <p>
 * created at 2022/4/17
 *
 * @author zzhb101
 */
public final class GenericDatas {
  private GenericDatas() {}

  public static GenericData of(int type, String str) {
    return GenericData.newBuilder() //
        .setType(type) //
        .setData(ByteString.copyFromUtf8(str)) //
        .build();
  }

  public static GenericData of(int type, byte[] bytes) {
    return GenericData.newBuilder() //
        .setType(type) //
        .setData(ByteString.copyFrom(bytes)) //
        .build();
  }
}
