/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core.data;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import com.google.protobuf.InvalidProtocolBufferException;

import io.holoinsight.server.agg.v1.pb.AggProtos;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * created at 2023/9/16
 *
 * @author xzchaoo
 */
@Slf4j
public class AggValuesSerdes {
  public static class S implements Serializer<AggProtos.AggTaskValue> {
    @Override
    public byte[] serialize(String topic, AggProtos.AggTaskValue data) {
      if (data == null) {
        return null;
      }
      return data.toByteArray();
    }
  }

  public static class D implements Deserializer<AggProtos.AggTaskValue> {
    @Override
    public AggProtos.AggTaskValue deserialize(String topic, byte[] data) {
      if (data == null) {
        return null;
      }
      try {
        return AggProtos.AggTaskValue.parseFrom(data);
      } catch (InvalidProtocolBufferException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
