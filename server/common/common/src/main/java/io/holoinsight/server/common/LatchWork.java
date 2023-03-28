/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.common;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

/**
 * @author jsy1001de
 * @version : LatchWork.java, Date: 2023-03-28 Time: 15:59
 */
@Slf4j
public abstract class LatchWork implements Runnable {
  CountDownLatch latch;

  String workerName;

  public LatchWork(String workerName, CountDownLatch latch) {
    super();
    this.latch = latch;
    this.workerName = workerName;
  }

  /**
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    try {
      doWork();
    } catch (Throwable t) {
      log.error("LatchWork,workerName: {}, catch exception {}", workerName, t.getMessage(), t);
      doException(t.getMessage());
    } finally {
      latch.countDown();
    }
  }

  public abstract void doWork();

  public abstract void doException(String msg);

  public CountDownLatch getLatch() {
    return latch;
  }

  public void setLatch(CountDownLatch latch) {
    this.latch = latch;
  }

  public String getWorkerName() {
    return workerName;
  }

  public void setWorkerName(String workerName) {
    this.workerName = workerName;
  }

}
