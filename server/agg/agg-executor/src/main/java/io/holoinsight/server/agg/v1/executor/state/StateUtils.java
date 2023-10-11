/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.state;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import io.holoinsight.server.agg.v1.pb.StateProtos;

/**
 * <p>
 * created at 2023/9/25
 *
 * @author xzchaoo
 */
public class StateUtils {
  public static byte[] serialize(PartitionState state) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (GZIPOutputStream gos = new GZIPOutputStream(baos)) {
      StatePbConverter.toPb(state).writeTo(gos);
    }
    return baos.toByteArray();
  }

  public static PartitionState deserialize(byte[] data) throws IOException {
    try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(data))) {
      StateProtos.PartitionState pb = StateProtos.PartitionState.parseFrom(gis);
      return StatePbConverter.fromPb(pb);
    }
  }
}
