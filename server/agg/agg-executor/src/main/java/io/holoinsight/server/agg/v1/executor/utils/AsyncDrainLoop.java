/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.utils;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * created at 2023/10/10
 *
 * @author xzchaoo
 */
public abstract class AsyncDrainLoop {
  private final AtomicInteger drainLoopWip = new AtomicInteger();
  private final ExecutorService es;

  public AsyncDrainLoop(ExecutorService es) {
    this.es = Objects.requireNonNull(es);
  }

  protected final void drainLoop() {
    if (drainLoopWip.getAndIncrement() != 0) {
      return;
    }

    try {
      es.execute(this::drainLoop0);
    } catch (RejectedExecutionException e) {
      drainLoop0();
    }
  }

  protected void drainLoop0() {
    int delta = drainLoopWip.get();
    do {
      drainLoop1();
      delta = drainLoopWip.addAndGet(-delta);
    } while (delta != 0);
  }

  protected abstract void drainLoop1();
}
