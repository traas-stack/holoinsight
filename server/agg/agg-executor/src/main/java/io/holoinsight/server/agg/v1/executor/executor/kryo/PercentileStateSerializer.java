/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import io.holoinsight.server.agg.v1.executor.executor.PercentileState;

/**
 * Kryo serializer for PercentileState
 * <p>
 * created at 2024/2/21
 *
 * @author xzchaoo
 */
public class PercentileStateSerializer extends Serializer<PercentileState> {
  @Override
  public void write(Kryo kryo, Output output, PercentileState obj) {
    byte[] bytes = obj.toByteArray(false);
    output.writeVarInt(bytes.length, true);
    output.writeBytes(bytes);
  }

  @Override
  public PercentileState read(Kryo kryo, Input input, Class<? extends PercentileState> type) {
    int length = input.readVarInt(true);
    byte[] bytes = input.readBytes(length);
    return new PercentileState(bytes);
  }
}
