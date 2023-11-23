/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import java.nio.charset.StandardCharsets;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.google.common.hash.Hashing;

import io.holoinsight.server.agg.v1.executor.executor.kryo.HllStateSerializer;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import net.agkn.hll.HLL;

/**
 * <p>
 * created at 2023/10/17
 *
 * @author xzchaoo
 */
@Data
@DefaultSerializer(HllStateSerializer.class)
public class HllState {
  @Setter(AccessLevel.NONE)
  private HLL hll;

  public HllState() {
    hll = new HLL(15, 5);
  }

  public HllState(HLL hll) {
    this.hll = hll;
  }

  public void add(String str) {
    int x = Hashing.murmur3_32().newHasher().putString(str, StandardCharsets.UTF_8).hash().asInt();
    hll.addRaw(x);
  }

  public long cardinality() {
    return hll.cardinality();
  }
}
