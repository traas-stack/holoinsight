/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import com.google.protobuf.ByteString;

import io.holoinsight.server.agg.v1.pb.AggProtos;

/**
 * <p>
 * created at 2023/10/17
 *
 * @author xzchaoo
 */
public class BasicFieldAccessors {
  public static Accessor direct(AggProtos.InDataNode in) {
    return new Accessor() {
      @Override
      public int count() {
        return in.getCount();
      }

      @Override
      public double float64() {
        return in.getFloat64Value();
      }

      @Override
      public ByteString bytes() {
        return in.getBytesValue();
      }
    };
  }

  public static Accessor field(AggProtos.BasicField bf) {
    return new Accessor() {
      @Override
      public int count() {
        return bf.getCount();
      }

      @Override
      public double float64() {
        return bf.getFloat64Value();
      }

      @Override
      public ByteString bytes() {
        return bf.getBytesValue();
      }
    };
  }

  public interface Accessor {
    int count();

    double float64();

    ByteString bytes();
  }
}
