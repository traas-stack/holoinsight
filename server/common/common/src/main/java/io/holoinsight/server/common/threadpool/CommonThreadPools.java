/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.xzchaoo.commons.basic.concurrent.DynamicScheduledExecutorService;

/**
 * 一些共用的线程池, 因为是公共的, 一般不要在里面做很重的事情
 * <p>
 * created at 2022/3/4
 *
 * @author xzchaoo
 */
public class CommonThreadPools {
  private static final Logger LOGGER = LoggerFactory.getLogger(CommonThreadPools.class);
  private static final Thread.UncaughtExceptionHandler HANDLER = new Log();
  private volatile DynamicScheduledExecutorService dynamicScheduler;
  private volatile ExecutorService io;
  private volatile ExecutorService rpcServer;
  private volatile ExecutorService rpcClient;
  private volatile ScheduledExecutorService scheduler;

  /**
   * <p>
   * Constructor for CommonThreadPools.
   * </p>
   */
  public CommonThreadPools() {}

  /**
   * <p>
   * Getter for the field <code>scheduler</code>.
   * </p>
   */
  public ScheduledExecutorService getScheduler() {
    if (scheduler == null) {
      synchronized (CommonThreadPools.class) {
        if (scheduler == null) {
          int cpu = cpu();
          scheduler = scheduler(cpu * 2, "common-scheduler-%d");
        }
      }
    }
    return scheduler;
  }

  /**
   * <p>
   * Getter for the field <code>dynamicScheduler</code>.
   * </p>
   */
  public DynamicScheduledExecutorService getDynamicScheduler() {
    if (dynamicScheduler == null) {
      synchronized (CommonThreadPools.class) {
        if (dynamicScheduler == null) {
          dynamicScheduler = DynamicScheduledExecutorService.wrap(getScheduler());
        }
      }
    }
    return dynamicScheduler;
  }

  /**
   * <p>
   * Getter for the field <code>rpcServer</code>.
   * </p>
   */
  public ExecutorService getRpcServer() {
    if (rpcServer == null) {
      synchronized (CommonThreadPools.class) {
        if (rpcServer == null) {
          int cpu = cpu();
          rpcServer = executor(cpu * 8, cpu * 8, 0, 65536, "rpc-server-%d");
        }
      }
    }
    return rpcServer;
  }

  /**
   * <p>
   * Getter for the field <code>rpcClient</code>.
   * </p>
   */
  public ExecutorService getRpcClient() {
    if (rpcClient == null) {
      synchronized (CommonThreadPools.class) {
        if (rpcClient == null) {
          int cpu = cpu();
          rpcClient = executor(cpu, cpu, 0, 4096, "rpc-client-%d");
        }
      }
    }
    return rpcClient;
  }

  /**
   * <p>
   * Getter for the field <code>io</code>.
   * </p>
   */
  public ExecutorService getIo() {
    if (io == null) {
      synchronized (CommonThreadPools.class) {
        if (io == null) {
          int cpu = cpu();
          io = executor(cpu * 4, cpu * 4, 0, 65536, "common-io-%d");
        }
      }
    }
    return io;
  }

  /**
   * <p>
   * close.
   * </p>
   */
  @PreDestroy
  public void close() {
    shutdown(io);
    shutdown(scheduler);
    shutdown(rpcClient);
    shutdown(rpcServer);
  }

  private static void shutdown(ExecutorService es) {
    if (es != null) {
      es.shutdown();
    }
  }

  private static int cpu() {
    // You will get wrong cpu count when running in virtualization environment, such as 'docker'.
    // You should pass an env 'CPU' to use as available cpu count.
    // Otherwise, you will get many threads in pool.
    String cpu = System.getenv("CPU");
    if (cpu != null) {
      return Integer.parseInt(cpu);
    }
    return Runtime.getRuntime().availableProcessors();
  }

  private static ExecutorService executor(int min, int max, int keepalive, int queue,
      String nameFormat) {
    ThreadFactory tf = new ThreadFactoryBuilder() //
        .setNameFormat(nameFormat) //
        .setUncaughtExceptionHandler(HANDLER) //
        .build(); //
    ThreadPoolExecutor tpe = new ThreadPoolExecutor(min, max, keepalive, TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(queue), tf, new ThreadPoolExecutor.AbortPolicy());
    return tpe;
  }

  private static ScheduledExecutorService scheduler(int size, String nameFormat) {
    ThreadFactory tf = new ThreadFactoryBuilder() //
        .setNameFormat(nameFormat) //
        .setUncaughtExceptionHandler(HANDLER) //
        .build(); //
    return new ScheduledThreadPoolExecutor(size, tf);
  }

  private static class Log implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
      LOGGER.error("uncaught exception thread=[{}]", t.getName(), e);
    }
  }
}
