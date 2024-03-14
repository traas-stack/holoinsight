/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.task.eventengine.wheeltimer;

import lombok.extern.slf4j.Slf4j;
import org.jboss.netty.util.ThreadRenamingRunnable;
import org.jboss.netty.util.internal.ConcurrentIdentityHashMap;
import org.jboss.netty.util.internal.DetectionUtil;
import org.jboss.netty.util.internal.ReusableIterator;
import org.jboss.netty.util.internal.SharedResourceMisuseDetector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 基于一纬时间轮的线程安全定时器
 * 
 * @author jsy1001de
 * @version 1.0: WheelTimerImpl.java, v 0.1 2022年04月07日 11:43 上午 jinsong.yjs Exp $
 */
@Slf4j
public class WheelTimerImpl implements WheelTimer {
  private static final AtomicInteger id = new AtomicInteger();

  final Thread workerThread;
  final AtomicInteger workerState = new AtomicInteger(); // 0 - init, 1 - started, 2 - shut down
  final long tickDuration;
  final Set<HashedWheelTimeout>[] wheel;
  final ReusableIterator<HashedWheelTimeout>[] iterators;
  final int mask;
  final ReadWriteLock lock = new ReentrantReadWriteLock();
  private final Worker worker = new Worker();
  private final long roundDuration;
  volatile int wheelCursor;
  private static final SharedResourceMisuseDetector misuseDetector =
      new SharedResourceMisuseDetector(WheelTimerImpl.class);

  /**
   * 默认构造器
   */
  public WheelTimerImpl() {
    this(Executors.defaultThreadFactory());
  }

  /**
   * 指定每次tick时间的构造器
   *
   * @param tickDuration tick单位时间
   * @param unit tick时间的时间单位
   */
  public WheelTimerImpl(long tickDuration, TimeUnit unit) {
    this(Executors.defaultThreadFactory(), tickDuration, unit);
  }

  /**
   * 指定每次tick时间，时间轮大小的构造器
   *
   * @param tickDuration tick单位时间
   * @param unit tick时间的时间单位
   * @param ticksPerWheel 时间轮大小
   */
  public WheelTimerImpl(long tickDuration, TimeUnit unit, int ticksPerWheel) {
    this(Executors.defaultThreadFactory(), tickDuration, unit, ticksPerWheel);
  }

  /**
   * 指定thread factory的构造器
   *
   * @param threadFactory 线程工程创建的线程将用于执行timer task的任务
   */
  public WheelTimerImpl(ThreadFactory threadFactory) {
    this(threadFactory, 100, TimeUnit.MILLISECONDS);
  }

  /**
   * 指定thread factory，tick单位时间的构造器
   *
   * @param threadFactory 线程工程创建的线程将用于执行timer task的任务
   * @param tickDuration tick单位时间
   * @param unit tick时间的时间单位
   */
  public WheelTimerImpl(ThreadFactory threadFactory, long tickDuration, TimeUnit unit) {
    this(threadFactory, tickDuration, unit, 512);
  }

  /**
   * 指定thread factory，tick单位时间，时间轮大小的构造器
   *
   * @param threadFactory 线程工程创建的线程将用于执行timer task的任务
   * @param tickDuration tick单位时间
   * @param unit tick时间的时间单位
   * @param ticksPerWheel 时间轮大小
   */
  public WheelTimerImpl(ThreadFactory threadFactory, long tickDuration, TimeUnit unit,
      int ticksPerWheel) {

    if (threadFactory == null) {
      throw new NullPointerException("threadFactory");
    }

    if (unit == null) {
      throw new NullPointerException("unit");
    }

    if (tickDuration <= 0) {
      throw new IllegalArgumentException("tickDuration must be greater than 0: " + tickDuration);
    }

    if (ticksPerWheel <= 0) {
      throw new IllegalArgumentException("ticksPerWheel must be greater than 0: " + ticksPerWheel);
    }

    // 初始化时间轮
    wheel = createWheel(ticksPerWheel); // 创建时间轮的环形数组结构
    iterators = createIterators(wheel);
    mask = wheel.length - 1; // 用于快速取模的掩码

    // 将tick单位时间转化为毫秒
    this.tickDuration = tickDuration = unit.toMillis(tickDuration);

    // 防止越界
    if (tickDuration == Long.MAX_VALUE || tickDuration >= Long.MAX_VALUE / wheel.length) {

      throw new IllegalArgumentException("tickDuration is too long: " + tickDuration + ' ' + unit);
    }

    roundDuration = tickDuration * wheel.length;
    // 创建工作线程
    workerThread = threadFactory.newThread(
        new ThreadRenamingRunnable(worker, "Hashed wheel timer #" + id.incrementAndGet()));

    // 检查资源使用
    misuseDetector.increase();
  }

  @SuppressWarnings("unchecked")
  private static Set<HashedWheelTimeout>[] createWheel(int ticksPerWheel) {

    if (ticksPerWheel <= 0) {
      throw new IllegalArgumentException("ticksPerWheel must be greater than 0: " + ticksPerWheel);
    }

    if (ticksPerWheel > 1073741824) {
      throw new IllegalArgumentException(
          "ticksPerWheel may not be greater than 2^30: " + ticksPerWheel);
    }

    ticksPerWheel = normalizeTicksPerWheel(ticksPerWheel);
    Set<HashedWheelTimeout>[] wheel = new Set[ticksPerWheel];

    for (int i = 0; i < wheel.length; i++) {
      wheel[i] = new MapBackedSet<HashedWheelTimeout>(
          new ConcurrentIdentityHashMap<HashedWheelTimeout, Boolean>(16, 0.95f, 4));
    }

    return wheel;
  }

  @SuppressWarnings("unchecked")
  private static ReusableIterator<HashedWheelTimeout>[] createIterators(
      Set<HashedWheelTimeout>[] wheel) {
    ReusableIterator<HashedWheelTimeout>[] iterators = new ReusableIterator[wheel.length];

    for (int i = 0; i < wheel.length; i++) {
      iterators[i] = (ReusableIterator<HashedWheelTimeout>) wheel[i].iterator();
    }

    return iterators;
  }

  /**
   * 将时间轮大小整形到2的次方
   *
   * @param ticksPerWheel
   * @return
   */
  private static int normalizeTicksPerWheel(int ticksPerWheel) {
    int normalizedTicksPerWheel = 1;

    while (normalizedTicksPerWheel < ticksPerWheel) {
      normalizedTicksPerWheel <<= 1;
    }

    return normalizedTicksPerWheel;
  }

  /**
   * 启动工作线程
   *
   * @throws IllegalStateException 如果timer已终止则抛出此异常
   */
  public void start() {
    switch (workerState.get()) {
      case 0:
        if (workerState.compareAndSet(0, 1)) {
          workerThread.start();
        }
        break;
      case 1:
        break;
      case 2:
        throw new IllegalStateException("cannot be started once stopped");
      default:
        throw new Error();
    }
  }

  public Set<WheelTimeout> stop() {

    if (Thread.currentThread() == workerThread) {

      throw new IllegalStateException(WheelTimerImpl.class.getSimpleName()
          + ".stop() cannot be called from " + TimerTask.class.getSimpleName());
    }

    if (workerState.getAndSet(2) != 1) {
      // workerState不是1，则返回空set
      return Collections.emptySet();
    }

    boolean interrupted = false;

    while (workerThread.isAlive()) {
      workerThread.interrupt();
      try {
        workerThread.join(100);
      } catch (InterruptedException e) {
        interrupted = true;
      }
    }

    if (interrupted) {
      Thread.currentThread().interrupt();
    }

    misuseDetector.decrease();

    Set<WheelTimeout> unprocessedWheelTimeouts = new HashSet<WheelTimeout>();

    for (Set<HashedWheelTimeout> bucket : wheel) {
      unprocessedWheelTimeouts.addAll(bucket);
      bucket.clear();
    }

    return Collections.unmodifiableSet(unprocessedWheelTimeouts);
  }

  public WheelTimeout newTimeout(TimerTask task, long delay, TimeUnit unit) {
    final long currentTime = System.currentTimeMillis();

    if (task == null) {
      throw new NullPointerException("task");
    }
    if (unit == null) {
      throw new NullPointerException("unit");
    }

    start();

    delay = unit.toMillis(delay);
    HashedWheelTimeout timeout = new HashedWheelTimeout(task, currentTime + delay);
    scheduleTimeout(timeout, delay);

    return timeout;
  }

  void scheduleTimeout(HashedWheelTimeout timeout, long delay) {
    // delay时间必须大于等于tick单位时间
    if (delay < tickDuration) {
      delay = tickDuration;
    }

    final long lastRoundDelay = delay % roundDuration;
    final long lastTickDelay = delay % tickDuration;
    final long relativeIndex = lastRoundDelay / tickDuration + (lastTickDelay != 0 ? 1 : 0);

    final long remainingRounds = delay / roundDuration - (delay % roundDuration == 0 ? 1 : 0);

    // 将timeout加入时间轮子
    lock.readLock().lock();

    try {
      int stopIndex = (int) (wheelCursor + relativeIndex & mask);
      timeout.stopIndex = stopIndex;
      timeout.remainingRounds = remainingRounds;

      wheel[stopIndex].add(timeout);
    } finally {
      lock.readLock().unlock();
    }
  }

  private final class Worker implements Runnable {

    private long startTime;
    private long tick;

    Worker() {}

    /**
     *
     */
    public void run() {
      List<HashedWheelTimeout> expiredTimeouts = new ArrayList<HashedWheelTimeout>();

      startTime = System.currentTimeMillis();
      tick = 1;

      while (workerState.get() == 1) {
        final long deadline = waitForNextTick();
        if (deadline > 0) {
          fetchExpiredTimeouts(expiredTimeouts, deadline);
          notifyExpiredTimeouts(expiredTimeouts);
        }
      }
    }

    private void fetchExpiredTimeouts(List<HashedWheelTimeout> expiredTimeouts, long deadline) {

      // 找出超时并减少round counter. 超时触发在锁外执行
      lock.writeLock().lock();
      try {
        int newWheelCursor = wheelCursor = wheelCursor + 1 & mask;
        ReusableIterator<HashedWheelTimeout> i = iterators[newWheelCursor];
        fetchExpiredTimeouts(expiredTimeouts, i, deadline);
      } finally {
        lock.writeLock().unlock();
      }
    }

    private void fetchExpiredTimeouts(List<HashedWheelTimeout> expiredTimeouts,
        ReusableIterator<HashedWheelTimeout> i, long deadline) {

      List<HashedWheelTimeout> slipped = null;
      i.rewind();

      while (i.hasNext()) {
        HashedWheelTimeout timeout = i.next();

        if (timeout.remainingRounds <= 0) {
          i.remove();

          if (timeout.deadline <= deadline) {
            expiredTimeouts.add(timeout);
          } else {
            // 收集放置错误的timeout，将其放入临时队列并重新定时
            if (slipped == null) {
              slipped = new ArrayList<HashedWheelTimeout>();
            }
            slipped.add(timeout);
          }
        } else {
          timeout.remainingRounds--;
        }
      }

      // 重新定时放置错误的timeout
      if (slipped != null) {
        for (HashedWheelTimeout timeout : slipped) {
          scheduleTimeout(timeout, timeout.deadline - deadline);
        }
      }
    }

    private void notifyExpiredTimeouts(List<HashedWheelTimeout> expiredTimeouts) {
      // 执行超时任务
      for (int i = expiredTimeouts.size() - 1; i >= 0; i--) {
        expiredTimeouts.get(i).expire();
      }

      expiredTimeouts.clear();
    }

    private long waitForNextTick() {
      long deadline = startTime + tickDuration * tick;

      for (;;) {
        final long currentTime = System.currentTimeMillis();
        long sleepTime = tickDuration * tick - (currentTime - startTime);

        // windows需要特殊处理
        if (DetectionUtil.isWindows()) {
          sleepTime = sleepTime / 10 * 10;
        }

        if (sleepTime <= 0) {
          break;
        }
        try {
          Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
          if (workerState.get() != 1) {
            return -1;
          }
        }
      }

      // 增加一个tick
      tick++;
      return deadline;
    }
  }

  private final class HashedWheelTimeout implements WheelTimeout {

    private static final int ST_INIT = 0;
    private static final int ST_CANCELLED = 1;
    private static final int ST_EXPIRED = 2;
    final long deadline;
    private final TimerTask task;
    private final AtomicInteger state = new AtomicInteger(ST_INIT);
    volatile int stopIndex;
    volatile long remainingRounds;

    HashedWheelTimeout(TimerTask task, long deadline) {
      this.task = task;
      this.deadline = deadline;
    }

    /**
     *
     */
    public WheelTimer getTimer() {
      return WheelTimerImpl.this;
    }

    /**
     *
     */
    public TimerTask getTask() {
      return task;
    }

    /**
     *
     */
    public void cancel() {
      if (!state.compareAndSet(ST_INIT, ST_CANCELLED)) {
        // TODO return false
        return;
      }

      wheel[stopIndex].remove(this);
    }

    /**
     *
     */
    public boolean isCancelled() {
      return state.get() == ST_CANCELLED;
    }

    /**
     *
     */
    public boolean isExpired() {
      return state.get() != ST_INIT;
    }

    /**
     *
     */
    public void expire() {
      if (!state.compareAndSet(ST_INIT, ST_EXPIRED)) {
        return;
      }

      try {
        task.run(this);
      } catch (Throwable t) {
        log.warn("An exception was thrown by " + TimerTask.class.getSimpleName() + '.');
      }
    }

    /**
     *
     */
    @Override
    public String toString() {
      long currentTime = System.currentTimeMillis();
      long remaining = deadline - currentTime;

      StringBuilder buf = new StringBuilder(192);
      buf.append(getClass().getSimpleName());
      buf.append('(');

      buf.append("deadline: ");
      if (remaining > 0) {
        buf.append(remaining);
        buf.append(" ms later, ");
      } else if (remaining < 0) {
        buf.append(-remaining);
        buf.append(" ms ago, ");
      } else {
        buf.append("now, ");
      }

      if (isCancelled()) {
        buf.append(", cancelled");
      }

      return buf.append(')').toString();
    }
  }

}
