/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import io.holoinsight.server.agg.v1.core.conf.AggTask;
import io.holoinsight.server.agg.v1.executor.executor.XAggTask;
import io.holoinsight.server.agg.v1.executor.executor.XParserUtils;

/**
 * Kryo serializer for {@link XAggTask}
 * <p>
 * created at 2023/10/26
 *
 * @author xzchaoo
 */
public class XAggTaskSerializer extends Serializer<XAggTask> {
  @Override
  public void write(Kryo kryo, Output output, XAggTask object) {
    kryo.writeObject(output, object.getInner());
  }

  @Override
  public XAggTask read(Kryo kryo, Input input, Class<? extends XAggTask> type) {
    AggTask at = kryo.readObject(input, AggTask.class);
    return XParserUtils.parse(at);
  }
}
