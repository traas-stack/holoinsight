/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.output;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import com.alibaba.fastjson.JSON;

/**
 * <p>
 * created at 2023/10/3
 *
 * @author xzchaoo
 */
public class BatchSerdes {
  public static class S implements Serializer<XOutput.Batch> {
    @Override
    public byte[] serialize(String topic, XOutput.Batch data) {
      if (data == null) {
        return null;
      }
      return JSON.toJSONBytes(data);
    }
  }

  public static class D implements Deserializer<XOutput.Batch> {
    @Override
    public XOutput.Batch deserialize(String topic, byte[] data) {
      if (data == null) {
        return null;
      }
      return JSON.parseObject(data, XOutput.Batch.class);
    }
  }
}
