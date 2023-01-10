/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.cache;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.springframework.util.CollectionUtils;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

/**
 * <p>
 * SafeDataCacheQueue class.
 * </p>
 *
 * @author xzchaoo
 * @version $Id: SafeDataCacheQueue.java, v 0.1 2019年12月11日 19:38 jinsong.yjs Exp $
 */
public class SafeDataCacheQueue<T> {

  // 忽略的条数计数器
  private final AtomicInteger ignoreCount = new AtomicInteger();
  private final AtomicInteger offerCount = new AtomicInteger();
  private final AtomicInteger successCount = new AtomicInteger();
  private final AtomicInteger pollCount = new AtomicInteger();
  private final AtomicInteger bloomFilterCount = new AtomicInteger();

  private long time = System.currentTimeMillis();

  private LinkedBlockingQueue<T> CACHE;

  // 使用BloomFilter来对可能出现的KEY进行去重:一千万个元素占用的空间也只有20M
  private BloomFilter<String> BLOOM_CACHES;

  private Logger L;

  private String name;

  // 默认可能插入数据大小
  private int maxSize = 50000;

  // 是否过滤重复的数据
  private boolean isFilterRepeat = true;

  /**
   * <p>
   * Constructor for SafeDataCacheQueue.
   * </p>
   */
  public SafeDataCacheQueue() {}

  /**
   * <p>
   * Constructor for SafeDataCacheQueue.
   * </p>
   */
  public SafeDataCacheQueue(String name, int maxSize, boolean isFilterRepeat, Logger L) {
    this.name = name;
    this.maxSize = maxSize;
    this.isFilterRepeat = isFilterRepeat;
    this.L = L;
  }

  /**
   * <p>
   * Constructor for SafeDataCacheQueue.
   * </p>
   */
  public SafeDataCacheQueue(String name, int maxSize) {
    this.name = name;
    this.maxSize = maxSize;
  }

  /**
   * 获取指定表名的数据
   */
  public List<T> poll() {
    List<T> datas = new ArrayList<>();

    if (CACHE == null) {
      return datas;
    }

    int batchCount = 8000;
    while ((batchCount--) > 0) {
      T data = CACHE.poll();

      if (data == null) {
        break;
      }

      pollCount.incrementAndGet();
      datas.add(data);
    }

    return datas;
  }

  /**
   * 这个类的处理不会对外抛出异常,防止终端指标数据的获取
   *
   * @param data
   */
  public boolean offer(T data) {
    try {
      offerCount.incrementAndGet();

      if (data == null) {
        return false;
      }

      if (data instanceof Collection && CollectionUtils.isEmpty((Collection) data)) {
        return false;
      }

      initCacheIfNeed();

      boolean success = CACHE.offer(data);
      if (success) {
        successCount.incrementAndGet();
      } else {
        ignoreCount.incrementAndGet();
      }
      return success;

    } catch (Throwable e) {
      L.error("meta offer to queue error, key : " + name + ", err msg: " + e.getMessage(), e);
    }

    return false;
  }

  /**
   * <p>
   * logStatus.
   * </p>
   */
  public void logStatus() {
    // 统计下缓存的消费情况,可以为后续调优做准备
    L.info(String.format(
        "[SafeDataCacheQueue=%s,offerCount=%s,ignoreCount=%s,successCount=%s,bloomFilterCount=%s,pollCount=%s]",
        name, offerCount.get(), ignoreCount.get(), successCount.get(), bloomFilterCount.get(),
        pollCount.get()));
    offerCount.set(0);
    ignoreCount.set(0);
    pollCount.set(0);
    successCount.set(0);
    bloomFilterCount.set(0);
  }

  /**
   * 初始化队列
   */
  private void initCacheIfNeed() {

    // 没有的使用初始化一个队列
    if (CACHE == null) {
      int MAX_TABLE_BUFFER_SIZE = 240000;
      CACHE = new LinkedBlockingQueue<>(MAX_TABLE_BUFFER_SIZE);
      BLOOM_CACHES = createBloomFilter();
    }

    // 重置BloomFilter
    resetBloomFilterIfNeed();

  }

  private void resetBloomFilterIfNeed() {

    int onHour = 3600 * 1000;

    // 一个小时清理一次BloomFilter,让数据消费
    if (System.currentTimeMillis() - time > onHour) {
      synchronized (BLOOM_CACHES) {
        if (System.currentTimeMillis() - time <= onHour) {
          return;
        }

        BLOOM_CACHES = createBloomFilter();
        time = System.currentTimeMillis();
      }

    }
  }

  private BloomFilter<String> createBloomFilter() {
    maxSize = Math.max(maxSize, 10000);
    int bloomSize = maxSize * 10;
    int MAX_BLOOM_BUFFER_SIZE = 10000000;
    return BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()),
        Math.min(bloomSize, MAX_BLOOM_BUFFER_SIZE));
  }

  /**
   * 做一定优化,对重复数据不发送,减少冗余
   */
  private boolean contains(T data) {

    // 不过滤重复数据,则每次都放入都队列
    if (!isFilterRepeat) {
      return false;
    }

    if (BLOOM_CACHES == null) {
      return false;
    }

    String key = data.toString();

    return BLOOM_CACHES.mightContain(key);
  }

  private boolean updateBloomFilter(T data) {
    if (BLOOM_CACHES == null) {
      return false;
    }

    String key = data.toString();
    BLOOM_CACHES.put(key);

    return true;
  }

  /**
   * <p>
   * size.
   * </p>
   */
  public int size() {
    return CACHE.size();
  }

}
