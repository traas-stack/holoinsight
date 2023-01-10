/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.cluster;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * created at 2022/4/17
 *
 * @author zzhb101
 */
public class HandlerRegistry {
  private Map<Integer, Handler> map = new HashMap<>();

  public Handler get(int type) {
    return map.get(type);
  }

  public void register(Handler h) {
    int[] types = h.types();
    for (int type : types) {
      if (map.put(type, h) != null) {
        throw new IllegalStateException("duplicated handler type " + type);
      }
    }
  }
}
