/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core.dict;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * created at 2023/9/21
 *
 * @author xzchaoo
 */
@Slf4j
public class Dict {
  private static final Map<String, String> DICT = new ConcurrentHashMap<>();

  static {
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(
        new ThreadFactoryBuilder().setDaemon(true).setNameFormat("AGG-DICT-%d").build());
    scheduler.scheduleWithFixedDelay(Dict::clearDict, 3, 3, TimeUnit.MINUTES);
  }

  public static void clearDict() {
    log.info("[agg-dict] clear dict {}", DICT.size());
    DICT.clear();
  }

  public static Map<String, String> get(Map<String, String> m) {
    if (m == null) {
      return null;
    }

    Map<String, String> newMaps = Maps.newHashMapWithExpectedSize(m.size());
    m.forEach((k, v) -> newMaps.put(DICT.get(k), DICT.get(v)));
    return newMaps;
  }

  public static String get(String str) {
    if (str == null) {
      return null;
    }
    // if (str.length() > 10) {
    // return str;
    // }
    return DICT.computeIfAbsent(str, i -> i);
  }

  public static void reuse(String[] s) {
    if (s == null) {
      return;
    }
    for (int i = 0; i < s.length; i++) {
      s[i] = get(s[i]);
    }
  }
}
