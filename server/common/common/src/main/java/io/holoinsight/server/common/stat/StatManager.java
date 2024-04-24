/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.stat;

import com.xzchaoo.commons.basic.concurrent.OneThreadFactory;
import com.xzchaoo.commons.basic.concurrent.SchedulerUtils;
import com.xzchaoo.commons.basic.lang.TimeService;
import com.xzchaoo.commons.stat.Log;
import com.xzchaoo.commons.stat.StringPrinter;
import com.xzchaoo.commons.stat.StringsKey;
import lombok.AllArgsConstructor;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author masaimu
 * @version 2024-04-24 11:19:00
 */
public class StatManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(StatManager.class);

  /**
   * Default scheduler. This is enough for most cases.
   */
  private static volatile ScheduledExecutorService DEFAULT_SCHEDULER;

  private final ConcurrentLinkedQueue<StatManager.A> //
  statAccumulators = new ConcurrentLinkedQueue<>();
  //

  private final StringPrinter stringPrinter;

  private final ConcurrentLinkedQueue<Gauge<?>> g1 = new ConcurrentLinkedQueue<>();

  private final ConcurrentLinkedQueue<Gauges<?>> g2 = new ConcurrentLinkedQueue<>();

  private final ConcurrentLinkedQueue<StatManager.B> ms = new ConcurrentLinkedQueue<>();

  private final long intervalMs;
  private final SimpleDateFormat sdf;
  private final List<PrintItem> items = new ArrayList<>();
  private ScheduledFuture<?> future;

  private ScheduledFuture<?> cleanExpiredKeysFuture;
  private Duration cleanInterval = Duration.ofMinutes(1);
  private Duration expiredTime = Duration.ofMinutes(3);

  private StatManager(StatManager.Builder b) {
    this.intervalMs = b.interval;
    this.sdf = new SimpleDateFormat(b.datePattern);
    this.stringPrinter = new StringPrinter(b.attachTime, b.log);
    this.cleanInterval = b.cleanInterval;
    this.expiredTime = b.expiredTime;
  }

  public StatManager(long intervalMs, Log log) {
    this(intervalMs, "yyyy-MM-dd HH:mm:ss.SSS", log, true);
  }

  public StatManager(long intervalMs, Log log, boolean attachTime) {
    this(intervalMs, "yyyy-MM-dd HH:mm:ss.SSS", log, attachTime);
  }

  public StatManager(long intervalMs, String datePattern, Log log, boolean attachTime) {
    this.intervalMs = intervalMs;
    this.sdf = new SimpleDateFormat(datePattern);
    this.stringPrinter = new StringPrinter(attachTime, log);
  }

  public StatAccumulator<StringsKey> create(String prefix) {
    return create(StringsKey.of(prefix));
  }

  public StatAccumulator<StringsKey> create(StringsKey prefix) {
    DefaultStatAccumulator<StringsKey> a = new DefaultStatAccumulator<>(this);
    this.statAccumulators.add(new StatManager.A(a, prefix));
    return a;
  }

  public <T> void gauge(StringsKey key, //
      T state, //
      Function<? super T, ? extends Number> valueProvider) { //
    val g = new Gauge<>(key, state, valueProvider);
    g1.add(g);
  }

  public <T> void gauges(StringsKey key, //
      T state, //
      Function<? super T, long[]> valueProvider) { //
    val g = new Gauges<>(key, state, valueProvider);
    g2.add(g);
  }

  public <T> void func(Supplier<List<Measurement>> provider) { //
    this.func((StringsKey) null, provider);
  }

  public <T> void func(String prefix, Supplier<List<Measurement>> provider) { //
    func(StringsKey.of(prefix), provider);
  }

  public <T> void func(StringsKey prefix, Supplier<List<Measurement>> provider) { //
    ms.add(new StatManager.B(prefix, provider));
  }

  /**
   * Start instance with a builtin scheduler.
   */
  public void start() {
    start(getDefaultScheduler());
  }

  /**
   * Start instance with a user provided scheduler.
   *
   * @param scheduler scheduler
   */
  public synchronized void start(ScheduledExecutorService scheduler) {
    if (scheduler == null) {
      throw new IllegalArgumentException("scheduler is null");
    }
    future = SchedulerUtils.scheduleAtFixedRateAligned(scheduler, //
        this::print, //
        intervalMs, //
        TimeUnit.MILLISECONDS); //

    cleanExpiredKeysFuture = scheduler.scheduleWithFixedDelay(this::cleanExpiredKeys,
        cleanInterval.toMillis(), cleanInterval.toMillis(), TimeUnit.MILLISECONDS);
  }

  private void cleanExpiredKeys() {
    long expireTime = TimeService.getMillTime() - expiredTime.toMillis();
    for (StatManager.A a : statAccumulators) {
      a.a.clean(expireTime);
    }
  }

  public synchronized void stop() {
    ScheduledFuture<?> future = this.future;
    this.future = null;
    if (future != null) {
      future.cancel(false);
    }
    future = this.cleanExpiredKeysFuture;
    this.cleanExpiredKeysFuture = null;
    if (future != null) {
      future.cancel(false);
    }
  }

  private void print() {
    try {
      long now = System.currentTimeMillis();
      // 在上个周期结束之后记录上个周期的统计结果, 因此要使用上个周期开始的时间戳
      // long alignedTime = now / intervalMs * intervalMs - intervalMs;
      // 但这不适用于gauge 这意味着他们不适合打在同一个文件里.
      long alignedTime = now / intervalMs * intervalMs;
      items.clear();
      for (StatManager.A a : statAccumulators) {
        Map<StringsKey, long[]> map = a.a.getAndClear();
        for (Map.Entry<StringsKey, long[]> e : map.entrySet()) {
          items.add(new PrintItem(a.prefix, e.getKey(), e.getValue()));
        }

        Map<StringsKey, double[]> map2 = a.a.getAndClear2();
        for (Map.Entry<StringsKey, double[]> e : map2.entrySet()) {
          items.add(new PrintItem(a.prefix, e.getKey(), e.getValue()));
        }
      }
      for (Gauge<?> g : g1) {
        Object state = g.state;
        Function<Object, ? extends Number> p = (Function) g.provider;
        long value = p.apply(state).longValue();
        items.add(new PrintItem(null, g.key, new long[] {value}));
      }
      for (Gauges<?> g : g2) {
        Object state = g.state;
        Function<Object, long[]> p = (Function) g.provider;
        long[] values = p.apply(state);
        items.add(new PrintItem(null, g.key, values));
      }
      for (StatManager.B b : ms) {
        List<Measurement> list = b.s.get();
        for (Measurement m : list) {
          items.add(new PrintItem(b.prefix, m.key, m.values));
        }
      }
      print0(alignedTime, items);
      items.clear();
    } catch (Throwable e) {
      LOGGER.error("print stats error", e);
    }
  }

  private void print0(long alignedTime, List<PrintItem> items) {
    String time = sdf.format(new Date(alignedTime));
    for (val i : items) {
      if (i.values != null) {
        stringPrinter.print(time, i.prefix, i.key, i.values);
      } else {
        stringPrinter.print(time, i.prefix, i.key, i.values2);
      }
    }
  }

  private static ScheduledExecutorService getDefaultScheduler() {
    // lazy start
    if (DEFAULT_SCHEDULER == null) {
      synchronized (StatManager.class) {
        if (DEFAULT_SCHEDULER == null) {
          DEFAULT_SCHEDULER = createDefaultScheduler();
        }
      }
    }
    return DEFAULT_SCHEDULER;
  }

  private static ScheduledExecutorService createDefaultScheduler() {
    val tf = new OneThreadFactory("DefaultStatManager-S", true);
    return new ScheduledThreadPoolExecutor(1, tf);
  }

  @AllArgsConstructor
  private static class A {
    final DefaultStatAccumulator<StringsKey> a;
    final StringsKey prefix;
  }

  @AllArgsConstructor
  private static class B {
    final StringsKey prefix;
    final Supplier<List<Measurement>> s;
  }

  public static StatManager.Builder newBuilder() {
    return new StatManager.Builder();
  }

  public static class Builder {
    private long interval = 60_000L;
    private Log log = System.out::println;
    private String datePattern = "yyyy-MM-dd HH:mm:ss.SSS";
    private boolean attachTime = false;
    private Duration cleanInterval = Duration.ofMinutes(1);
    private Duration expiredTime = Duration.ofMinutes(3);

    public StatManager.Builder interval(long interval) {
      this.interval = interval;
      return this;
    }


    public StatManager.Builder log(Log log) {
      this.log = log;
      return this;
    }

    public StatManager.Builder datePattern(String datePattern) {
      this.datePattern = datePattern;
      return this;
    }

    public StatManager.Builder attachTime(boolean attachTime) {
      this.attachTime = attachTime;
      return this;
    }

    public StatManager.Builder cleanInterval(Duration cleanInterval) {
      this.cleanInterval = cleanInterval;
      return this;
    }

    public StatManager.Builder expiredTime(Duration expiredTime) {
      this.expiredTime = expiredTime;
      return this;
    }

    public StatManager build() {
      return new StatManager(this);
    }
  }
}
