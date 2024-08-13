/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import io.holoinsight.server.agg.v1.executor.executor.HllState;
import net.agkn.hll.HLL;

/**
 * Kryo serializer {@link HllState}
 * <p>
 * created at 2023/10/26
 *
 * @author xzchaoo
 */
public class HllStateSerializer extends Serializer<HllState> {
  @Override
  public void write(Kryo kryo, Output output, HllState object) {
    byte[] bytes = object.getHll().toBytes();
    output.writeVarInt(bytes.length, true);
    output.writeBytes(bytes);
  }

  @Override
  public HllState read(Kryo kryo, Input input, Class<? extends HllState> type) {
    int length = input.readVarInt(true);
    byte[] bytes = input.readBytes(length);
    return new HllState(HLL.fromBytes(bytes));
  }
}
