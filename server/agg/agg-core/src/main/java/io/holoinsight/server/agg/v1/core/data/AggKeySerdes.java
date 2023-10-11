/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core.data;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import com.google.protobuf.InvalidProtocolBufferException;

import io.holoinsight.server.agg.v1.pb.AggProtos;

/**
 * <p>
 * created at 2023/9/16
 *
 * @author xzchaoo
 */
public class AggKeySerdes {
  public static class S implements Serializer<AggTaskKey> {
    @Override
    public byte[] serialize(String topic, AggTaskKey data) {
      if (data == null) {
        return null;
      }
      return AggProtos.AggTaskKey.newBuilder() //
          .setTenant(data.getTenant()) //
          .setAggId(data.getAggId()) //
          .setPartition(data.getPartition()) //
          .build() //
          .toByteArray(); //
    }
  }

  public static class D implements Deserializer<AggTaskKey> {
    @Override
    public AggTaskKey deserialize(String topic, byte[] data) {
      if (data == null) {
        return null;
      }
      try {
        AggProtos.AggTaskKey pb = AggProtos.AggTaskKey.parseFrom(data);
        return new AggTaskKey(pb.getTenant(), pb.getAggId(), pb.getPartition());
      } catch (InvalidProtocolBufferException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
