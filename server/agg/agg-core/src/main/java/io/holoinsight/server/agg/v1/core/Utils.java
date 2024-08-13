/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.ThreadFactory;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import io.holoinsight.server.agg.v1.core.conf.Window;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * created at 2023/9/19
 *
 * @author xzchaoo
 */
@Slf4j
public final class Utils {
  private static final ThreadLocal<SimpleDateFormat> SDF_TL =
      ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

  private static final ThreadLocal<SimpleDateFormat> SHORT_SDF_TL =
      ThreadLocal.withInitial(() -> new SimpleDateFormat("HH:mm:ss"));
  private static final long ALIGN_DAY_ADJUST;
  private static final long DAY_MILLS = 86400000L;

  static {
    Instant now = Instant.now();
    ALIGN_DAY_ADJUST =
        now.atZone(ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS).toInstant().toEpochMilli()
            - now.toEpochMilli() / DAY_MILLS * DAY_MILLS;
  }

  private Utils() {}

  public static String formatTime(Date date) {
    return SDF_TL.get().format(date);
  }

  public static String formatTime(long ts) {
    return formatTime(new Date(ts));
  }

  public static String formatTimeShort(Date date) {
    return SHORT_SDF_TL.get().format(date);
  }

  public static String formatTimeShort(long ts) {
    return formatTimeShort(new Date(ts));
  }

  /**
   * <p>
   * Align timestamp to window.interval .
   * <p>
   * If window.interval equals to {@link #DAY_MILLS}; then timestamp is aligned using local
   * timezone.
   * 
   * @param ts
   * @param window
   * @return
   */
  public static long align(long ts, Window window) {
    long i = window.getInterval();
    if (i == DAY_MILLS) {
      return ts / i * i + ALIGN_DAY_ADJUST;
    } else {
      return ts / i * i;
    }
  }

  public static ExecutorService createThreadPool(String namePrefix, int size) {
    ThreadFactory computingTF =
        new ThreadFactoryBuilder().setNameFormat(namePrefix + "-%d").build();
    return Executors.newFixedThreadPool(size, computingTF);
  }

  public static ForkJoinPool createForkJoinPool(String namePrefix, int size) {
    ForkJoinPool.ForkJoinWorkerThreadFactory f = new ForkJoinPool.ForkJoinWorkerThreadFactory() {
      @Override
      public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
        ForkJoinWorkerThread thread =
            ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);

        // thread.getName() : ForkJoinPool-1-worker-${index}
        // replace to ${namePrefix}-${index}

        String name = namePrefix + "-" + thread.getPoolIndex();
        thread.setName(name);
        return thread;
      }
    };
    return new ForkJoinPool(size, f, (t, e) -> log.error("[{}] uncaughtException", t.getName(), e),
        false);
  }

  /**
   * Copy a map, keeping only the specified keys
   * 
   * @param map
   * @param keys
   * @return
   */
  public static <K, V> Map<K, V> copyMapWithKeys(Map<K, V> map, List<K> keys) {
    if (map == null || map.isEmpty() || keys == null || keys.isEmpty()) {
      return Collections.emptyMap();
    }
    Map<K, V> r = Maps.newHashMapWithExpectedSize(keys.size());
    for (K k : keys) {
      V v = map.get(k);
      if (v != null) {
        r.put(k, v);
      }
    }
    return r;
  }
}
