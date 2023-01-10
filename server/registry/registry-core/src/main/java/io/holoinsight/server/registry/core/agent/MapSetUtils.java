/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.agent;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * created at 2022/3/1
 *
 * @author zzhb101
 */
public class MapSetUtils {
  public static <T> Set<T> newCHS() {
    return ConcurrentHashMap.newKeySet();
  }

  public static <K1, K2> void addMapSetKey(Map<K1, Set<K2>> map, K1 k1, K2 k2) {
    map.computeIfAbsent(k1, ignored -> newCHS()).add(k2);
  }

  public static <K1, K2> void deleteMapSetKey(Map<K1, Set<K2>> map, K1 k1, K2 k2) {
    Set<K2> set = map.get(k1);
    if (set != null) {
      set.remove(k2);
    }
  }

}
