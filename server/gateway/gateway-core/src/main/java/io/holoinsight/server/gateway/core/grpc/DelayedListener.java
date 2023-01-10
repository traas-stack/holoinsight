/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.grpc;

import java.util.ArrayList;
import java.util.List;

import io.grpc.ServerCall;

/**
 * see io.grpc.internal.DelayedStream
 * <p>
 * created at 2022/2/25
 *
 * @author sw1136562366
 */
public class DelayedListener<ReqT> extends ServerCall.Listener<ReqT> {
  private volatile boolean passThrough;
  private ServerCall.Listener<ReqT> delegate;
  private List<Runnable> pendings = new ArrayList<>();

  /**
   * <p>
   * Constructor for DelayedListener.
   * </p>
   */
  public DelayedListener() {}

  private void delayOrExecute(Runnable r) {
    synchronized (this) {
      if (!passThrough) {
        pendings.add(r);
        return;
      }
    }
    r.run();
  }

  /** {@inheritDoc} */
  @Override
  public void onMessage(ReqT message) {
    if (passThrough) {
      delegate.onMessage(message);
    } else {
      delayOrExecute(() -> delegate.onMessage(message));
    }
  }

  /** {@inheritDoc} */
  @Override
  public void onHalfClose() {
    if (passThrough) {
      delegate.onHalfClose();
    } else {
      delayOrExecute(() -> delegate.onHalfClose());
    }
  }

  /** {@inheritDoc} */
  @Override
  public void onCancel() {
    if (passThrough) {
      delegate.onCancel();
    } else {
      delayOrExecute(() -> delegate.onCancel());
    }
  }

  /** {@inheritDoc} */
  @Override
  public void onComplete() {
    if (passThrough) {
      delegate.onComplete();
    } else {
      delayOrExecute(() -> delegate.onComplete());
    }
  }

  /** {@inheritDoc} */
  @Override
  public void onReady() {
    if (passThrough) {
      delegate.onReady();
    } else {
      delayOrExecute(() -> delegate.onReady());
    }
  }

  /**
   * <p>
   * Setter for the field <code>delegate</code>.
   * </p>
   */
  public void setDelegate(ServerCall.Listener<ReqT> delegate) {
    this.delegate = delegate;

    List<Runnable> toRun = new ArrayList<>();

    while (true) {
      // 这里要加锁防止与 delayOrExecute 冲突
      synchronized (this) {
        if (pendings.isEmpty()) {
          // 释放应用
          pendings = null;
          this.passThrough = true;
          return;
        }
        List<Runnable> temp = toRun;
        toRun = this.pendings;
        this.pendings = temp;
      }
      for (Runnable r : toRun) {
        r.run();
      }
      toRun.clear();
    }
  }
}
