/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor.kryo;

import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import io.holoinsight.server.agg.v1.core.conf.AggFunc;
import io.holoinsight.server.agg.v1.executor.executor.TopnState;

/**
 * Kryo serializer for {@link TopnState}
 * <p>
 * created at 2023/10/26
 *
 * @author xzchaoo
 */
public class TopnStateSerializer extends Serializer<TopnState> {
  @Override
  public void write(Kryo kryo, Output output, TopnState ts) {
    kryo.writeObject(output, ts.getParams());
    kryo.writeObject(output, new ArrayList<>(ts.getQ()));
  }

  @Override
  public TopnState read(Kryo kryo, Input input, Class<? extends TopnState> type) {
    AggFunc.TopnParams topnParams = kryo.readObject(input, AggFunc.TopnParams.class);
    List<TopnState.CostItem> list = kryo.readObject(input, ArrayList.class);
    TopnState ts = new TopnState(topnParams);
    ts.getQ().addAll(list);
    return ts;
  }
}
