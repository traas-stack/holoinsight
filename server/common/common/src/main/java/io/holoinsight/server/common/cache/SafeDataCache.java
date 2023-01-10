/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.cache;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


/**
 * <p>
 * SafeDataCache class.
 * </p>
 *
 * @author xzchaoo
 * @version $Id: SafeDataCache.java, v 0.1 2019年12月11日 19:37 jinsong.yjs Exp $
 */
public class SafeDataCache<T> {

  private static final int MAX_QUEUE = 240000;

  public final ConcurrentHashMap<String, SafeDataCacheQueue<T>> CACHES = new ConcurrentHashMap<>();

  // 是否过滤重复的数据
  private boolean isFilterRepeat;
  private Logger L;

  /**
   * <p>
   * Constructor for SafeDataCache.
   * </p>
   */
  public SafeDataCache(boolean isFilterRepeat, Logger L) {
    this.isFilterRepeat = isFilterRepeat;
    this.L = L;
  }

  /**
   * <p>
   * getKeys.
   * </p>
   */
  public List<String> getKeys() {
    return new ArrayList<>(CACHES.keySet());
  }

  /**
   * <p>
   * logStatus.
   * </p>
   */
  public void logStatus() {
    for (SafeDataCacheQueue queue : CACHES.values()) {
      queue.logStatus();
    }
  }

  /**
   * 获取指定key的数据
   *
   * @param key
   */
  public List<T> poll(String key) {
    return getCacheQueue(key).poll();
  }

  private SafeDataCacheQueue<T> getCacheQueue(String key) {

    if (!CACHES.containsKey(key)) {

      SafeDataCacheQueue<T> queue = new SafeDataCacheQueue<T>(key, MAX_QUEUE, isFilterRepeat, L);

      CACHES.putIfAbsent(key, queue);
    }

    return CACHES.get(key);
  }

  /**
   * 这个类的处理不会对外抛出异常,防止终端指标数据的获取
   *
   * @param key
   * @param dataList
   */
  public boolean offer(String key, T dataList) {

    return getCacheQueue(key).offer(dataList);
  }
}
