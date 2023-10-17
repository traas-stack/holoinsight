/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import lombok.Data;
import net.agkn.hll.HLL;

/**
 * <p>
 * created at 2023/10/17
 *
 * @author xzchaoo
 */
@Data
public class HllState {
  HLL hll;

  public HllState() {
    hll = new HLL(15, 5);
  }
}
