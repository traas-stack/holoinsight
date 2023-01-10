/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common;

import java.util.concurrent.ThreadFactory;

import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * <p>
 * created at 2022/11/25
 *
 * @author xzchaoo
 */
public class BoundedSchedulers {
  public static final Scheduler BOUNDED;

  static {
    ThreadFactory tf = new ThreadFactoryBuilder() //
        .setNameFormat("BOUNDED-%d") //
        .build(); //
    BOUNDED = Schedulers.newBoundedElastic(64, 65536, tf, 600);
  }

  private BoundedSchedulers() {}
}
