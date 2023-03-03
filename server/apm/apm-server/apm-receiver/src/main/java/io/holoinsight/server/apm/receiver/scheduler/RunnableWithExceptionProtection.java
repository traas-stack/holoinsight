/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.receiver.scheduler;

public class RunnableWithExceptionProtection implements Runnable {
  private Runnable run;
  private CallbackWhenException callback;

  public RunnableWithExceptionProtection(Runnable run, CallbackWhenException callback) {
    this.run = run;
    this.callback = callback;
  }

  @Override
  public void run() {
    try {
      run.run();
    } catch (Throwable t) {
      callback.handle(t);
    }
  }

  public interface CallbackWhenException {
    void handle(Throwable t);
  }
}
