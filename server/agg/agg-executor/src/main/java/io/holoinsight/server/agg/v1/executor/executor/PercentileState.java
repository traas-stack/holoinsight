/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import org.apache.datasketches.memory.Memory;
import org.apache.datasketches.quantiles.DoublesSketch;
import org.apache.datasketches.quantiles.UpdateDoublesSketch;

import com.esotericsoftware.kryo.DefaultSerializer;

import io.holoinsight.server.agg.v1.executor.executor.kryo.PercentileStateSerializer;

/**
 * <p>
 * created at 2024/2/21
 *
 * @author xzchaoo
 */
@DefaultSerializer(PercentileStateSerializer.class)
public class PercentileState {
  private UpdateDoublesSketch dk;

  public PercentileState() {
    dk = DoublesSketch.builder().build();
  }

  public PercentileState(byte[] bytes) {
    dk = UpdateDoublesSketch.heapify(Memory.wrap(bytes));
  }

  public void add(double value) {
    dk.update(value);
  }

  public double getQuantile(double rank) {
    return dk.getQuantile(rank);
  }

  public double[] getQuantiles(double[] ranks) {
    return dk.getQuantiles(ranks);
  }

  public byte[] toByteArray(boolean compact) {
    return dk.toByteArray(compact);
  }
}
