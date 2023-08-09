/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util.cache.local;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 公用本地缓存
 * 
 * @author jsy1001de
 * @version $Id: CommonLocalCache.java, v 0.1 2020年03月18日 08:00 jinsong.yjs Exp $
 */
@Component
@Slf4j
public class CommonLocalCache implements LocalCache {

  private static ConcurrentHashMap<String, LocalCacheItem> cache = new ConcurrentHashMap<>();

  public static boolean containsKey(String key) {
    return cache.containsKey(key);
  }

  /**
   * 缓存值
   * 
   * @param key
   * @param value
   */
  public static void put(String key, Object value) {
    LocalCacheItem item = new LocalCacheItem(0, System.currentTimeMillis(), value);
    cache.put(key, item);
  }

  /**
   * 缓存值-指定缓存时间
   * 
   * @param key
   * @param value
   * @param cacheTime 缓存时间
   * @param unit 缓存时间单位
   */
  public static void put(String key, Object value, long cacheTime, TimeUnit unit) {
    LocalCacheItem item =
        new LocalCacheItem(unit.toMillis(cacheTime), System.currentTimeMillis(), value);
    cache.put(key, item);
  }

  /**
   * 获取值
   * 
   * @param key
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <T> T get(String key) {
    LocalCacheItem item = cache.get(key);
    if (item == null) {
      log.info("[CommonLocalCache],[{}],[N]", key);
      return null;
    }
    log.info("[CommonLocalCache],[{}],[Y]", key);
    return (T) item.getValue();
  }

  @Override
  public void refresh() {
    for (String key : cache.keySet()) {
      LocalCacheItem item = cache.get(key);
      // 过期了移除缓存
      long currentTime = System.currentTimeMillis();
      if (item.getCacheTime() > 0 && currentTime - item.getCreateTime() > item.getCacheTime()) {
        cache.remove(key);
      }
    }
  }
}
