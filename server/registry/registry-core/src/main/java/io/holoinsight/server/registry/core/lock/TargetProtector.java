/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.lock;

/**
 * <p>
 * created at 2022/3/1
 *
 * @author zzhb101
 */

import com.xzchaoo.commons.basic.Ack;
import com.xzchaoo.commons.syncexclusiveexecutor.SyncExclusiveExecutorImpl;

import java.util.function.BooleanSupplier;

/**
 * 用于保证对target的不同类型的修改是串行的, 但相同类型的可以并发.
 *
 * @author zzhb101
 * @date 2020-06-17
 */
public class TargetProtector {
  public static final int READ = 1;
  public static final int WRITE = 2;
  /**
   * agent resource的修改与读可以并发
   */
  public static final int AGENT_RESOURCE = 1;

  private static final com.xzchaoo.commons.syncexclusiveexecutor.SyncExclusiveExecutor EXCLUSIVE_EXECUTOR = //
      new SyncExclusiveExecutorImpl(1000, -1, 1000);

  public static Ack acquire(int type) {
    // long begin = System.currentTimeMillis();
    Ack ack = EXCLUSIVE_EXECUTOR.acquire(type);
    // long end = System.currentTimeMillis();
    // MetricsUtils.LOCK_ACQUIRE.add(StringsKey.of(Integer.toString(type)), //
    // new long[] { 1, end - begin }); //
    return ack;
  }

  public static Ack read() {
    return acquire(READ);
  }

  public static Ack write() {
    return acquire(WRITE);
  }

  public static void executeInWriteLock(Runnable r) {
    Ack ack = write();
    try {
      r.run();
    } finally {
      ack.ack();
    }
  }

  /**
   * 加锁的代价是比较大的, 尽量减少加锁
   *
   * @param b 只有通过test判断后才需要执行r
   * @param r
   */
  public static void maybeExecuteInWriteLock(BooleanSupplier b, Runnable r) {
    if (!b.getAsBoolean()) {
      return;
    }

    Ack ack = write();
    try {
      r.run();
    } finally {
      ack.ack();
    }
  }

  public static Ack agentResource() {
    return acquire(AGENT_RESOURCE);
  }
}
