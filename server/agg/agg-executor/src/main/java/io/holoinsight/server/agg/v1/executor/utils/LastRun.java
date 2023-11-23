/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>
 * created at 2023/10/10
 *
 * @author xzchaoo
 */
public class LastRun extends AsyncDrainLoop {
  private final AtomicReference<Runnable> ar = new AtomicReference<>();

  public LastRun(ExecutorService es) {
    super(es);
  }

  public void add(Runnable r) {
    ar.set(r);
    drainLoop();
  }

  @Override
  protected void drainLoop1() {
    Runnable r = ar.getAndSet(null);
    if (r != null) {
      r.run();
    }
  }
}
