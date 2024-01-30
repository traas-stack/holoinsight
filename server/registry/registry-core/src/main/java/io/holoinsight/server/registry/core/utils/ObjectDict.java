/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.utils;

// import com.alipay.gaea.server.core.common.CaffeineScheduler;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Maps;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

/**
 * @author weibu
 * @version v 0.1 2020/5/15 1:10 AM xvyang.xy} Exp $
 */
public class ObjectDict {

  /**
   * 缓存的全局字典
   */
  private static final Cache<Object, Object> DICT = Caffeine.newBuilder() //
      .recordStats() //
      // .scheduler(CaffeineScheduler.SCHEDULER) //
      .expireAfterAccess(Duration.ofHours(1)) //
      .build(); //

  /**
   * 入参必须是可比较的值
   * 
   * @param key
   * @param <T>
   * @return
   */
  public static <T> T get(T key) {
    if (null == key) {
      return null;
    }
    return (T) DICT.get(key, t -> t);
  }

  public static <T> T[] replace(T[] keys) {
    if (keys == null) {
      return null;
    }
    for (int i = 0; i < keys.length; i++) {
      keys[i] = get(keys[i]);
    }
    return keys;
  }

  public static Map<String, String> hashMap(Map<String, String> map) {
    if (map == null) {
      return null;
    }
    if (map.isEmpty()) {
      return Collections.emptyMap();
    }
    Map<String, String> newMap = Maps.newHashMapWithExpectedSize(map.size());
    for (Map.Entry<String, String> e : map.entrySet()) {
      newMap.put(get(e.getKey()), get(e.getValue()));
    }
    return newMap;
  }

  public static Map<String, String> hashMapOrEmpty(Map<String, String> map) {
    if (map == null) {
      return Collections.emptyMap();
    }
    return hashMap(map);
  }
}
