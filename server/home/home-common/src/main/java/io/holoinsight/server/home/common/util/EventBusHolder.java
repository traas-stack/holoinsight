/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util;

import com.google.common.eventbus.AsyncEventBus;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 *
 * @author jsy1001de
 * @version 1.0: EventBusHolder.java, v 0.1 2021年06月17日 2:54 下午 jinsong.yjs Exp $
 */
public class EventBusHolder {

  public static AsyncEventBus asyncEventBus;

  private static Executor executor = Executors.newFixedThreadPool(2);

  // 双重锁单例模式
  private static AsyncEventBus getAsynEventBus() {
    if (asyncEventBus == null) {
      synchronized (AsyncEventBus.class) {
        if (asyncEventBus == null) {
          asyncEventBus = new AsyncEventBus(executor);
        }
      }
    }
    return asyncEventBus;
  }

  private EventBusHolder() {}

  public static void post(Object event) {
    getAsynEventBus().post(event);
  }

  public static void register(Object listener) {
    getAsynEventBus().register(listener);
  }

  public static void unregister(Object listener) {
    getAsynEventBus().unregister(listener);
  }

}
