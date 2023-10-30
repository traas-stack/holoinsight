/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.state;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.google.common.base.Preconditions;

import io.holoinsight.server.agg.v1.core.data.AggTaskKey;
import io.holoinsight.server.agg.v1.executor.executor.FixedSizeTags;
import io.holoinsight.server.agg.v1.executor.executor.Group;
import io.holoinsight.server.agg.v1.executor.executor.GroupField;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * created at 2023/9/25
 *
 * @author xzchaoo
 */
@Slf4j
public class StateUtils {

  public static byte[] serialize(PartitionState state) throws IOException {
    Kryo k = createKryo();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (GZIPOutputStream gos = new GZIPOutputStream(baos); Output output = new Output(gos)) {
      k.writeObject(output, state);
    }
    return baos.toByteArray();
  }

  public static PartitionState deserialize(byte[] data) throws IOException {
    Kryo k = createKryo();

    try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(data));
        Input is = new Input(gis)) {
      return k.readObject(is, PartitionState.class);
    }
  }

  /**
   * Check if two PartitionState instances are equal.
   * 
   * @param s0
   * @param s1
   */
  public static void checkEqual(PartitionState s0, PartitionState s1) {
    Preconditions.checkArgument(s0.getVersion() == s1.getVersion());
    Preconditions.checkArgument(s0.getOffset() == s1.getOffset());
    Preconditions.checkArgument(s0.getMaxEventTimestamp() == s1.getMaxEventTimestamp());
    Preconditions.checkArgument(s0.getAggTaskStates().size() == s1.getAggTaskStates().size());

    for (AggTaskKey aggTaskKey : s0.getAggTaskStates().keySet()) {
      AggTaskState ats0 = s0.getAggTaskStates().get(aggTaskKey);
      AggTaskState ats1 = s1.getAggTaskStates().get(aggTaskKey);
      Preconditions.checkNotNull(ats0);
      Preconditions.checkNotNull(ats1);

      Preconditions.checkArgument(aggTaskKey.equals(ats0.getKey()));
      Preconditions.checkArgument(aggTaskKey.equals(ats1.getKey()));

      Preconditions.checkArgument(ats0.getMaxEventTimestamp() == ats1.getMaxEventTimestamp());
      Preconditions.checkArgument(ats0.getWatermark() == ats1.getWatermark());

      Preconditions
          .checkArgument(ats0.getAggWindowStateMap().size() == ats1.getAggWindowStateMap().size());

      for (Long ts : ats0.getAggWindowStateMap().keySet()) {
        AggWindowState aws0 = ats0.getAggWindowStateMap().get(ts);
        AggWindowState aws1 = ats1.getAggWindowStateMap().get(ts);

        Preconditions.checkNotNull(aws0);
        Preconditions.checkNotNull(aws1);

        Preconditions.checkArgument(ts == aws0.getTimestamp());
        Preconditions.checkArgument(ts == aws1.getTimestamp());
        Preconditions.checkArgument(aws0.getStat().equals(aws1.getStat()));
        Preconditions
            .checkArgument(aws0.getWindowCompleteness().equals(aws1.getWindowCompleteness()));
        Preconditions.checkArgument(aws0.getGroupMap().size() == aws1.getGroupMap().size());
        Preconditions
            .checkArgument(aws0.getAggTask().getInner().equals(aws1.getAggTask().getInner()));

        for (FixedSizeTags tags : aws0.getGroupMap().keySet()) {
          Group g0 = aws0.getGroupMap().get(tags);
          Group g1 = aws1.getGroupMap().get(tags);
          Preconditions.checkNotNull(g0);
          Preconditions.checkNotNull(g1);

          Preconditions.checkArgument(tags.equals(g0.getTags()));
          Preconditions.checkArgument(tags.equals(g1.getTags()));

          Preconditions.checkArgument(g0.getFields().length == g1.getFields().length);
          for (int i = 0; i < g0.getFields().length; i++) {
            GroupField gf0 = g0.getFields()[i];
            GroupField gf1 = g1.getFields()[i];
            Preconditions.checkArgument(gf0.getAgg().equals(gf1.getAgg()));
            Preconditions.checkArgument(gf0.getInput() == gf1.getInput());
            Preconditions.checkArgument(gf0.getCount() == gf1.getCount());
            Preconditions.checkArgument(gf0.getValue() == gf1.getValue());

            if (gf0.getTopn() != null) {
              Preconditions.checkNotNull(gf1.getTopn());
            }

            if (gf0.getHll() != null) {
              Preconditions.checkNotNull(gf1.getHll());
              byte[] b0 = gf0.getHll().getHll().toBytes();
              byte[] b1 = gf1.getHll().getHll().toBytes();
              Preconditions.checkArgument(Arrays.equals(b0, b1));
            }

            if (gf0.getLogSamples() != null) {
              Preconditions.checkNotNull(gf1.getLogSamples());
              Preconditions.checkArgument(gf0.getLogSamples().equals(gf1.getLogSamples()));
            }

          }
        }
      }
    }
  }

  static Kryo createKryo() {
    Kryo k = new Kryo();
    k.setReferences(true);
    k.setRegistrationRequired(false);
    k.setDefaultSerializer(CompatibleFieldSerializer.class);
    return k;
  }

}
