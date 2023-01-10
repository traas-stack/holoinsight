/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ThreadFactory;

import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * <p>
 * created at 2022/3/2
 *
 * @author zzhb101
 */
public class BoundedSchedulers {
  public static final Scheduler BOUNDED;

  static {
    ThreadFactory tf = new ThreadFactoryBuilder() //
        .setNameFormat("BOUNDED-%d") //
        .build(); //
    BOUNDED = Schedulers.newBoundedElastic(64, 65536, tf, 600);
  }
}
