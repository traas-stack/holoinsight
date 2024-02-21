/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.config;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 统一定时器控制任务
 * 
 * @author jsy1001de
 * @version 1.0: ScheduleLoadTask.java, v 0.1 2022年06月08日 12:12 下午 jinsong.yjs Exp $
 */
public abstract class ScheduleLoadTask implements Runnable {

  private static final ThreadFactory factory =
      new BasicThreadFactory.Builder().namingPattern("schedule-load-%d").daemon(true).build();
  private static final ScheduledExecutorService scheduledExecutor =
      Executors.newScheduledThreadPool(5, factory);
  private static final List<ScheduleLoadTask> REGISTERED_TASK = new ArrayList<>();

  /**
   * 注册任务
   *
   * @param task
   */
  public static synchronized void registerTask(ScheduleLoadTask task, Boolean now)
      throws Exception {
    if (task.periodInSeconds() <= 0) {
      throw new IllegalArgumentException("task period <= 0s");
    }

    task.init();

    // 立即load，失败后触发启动失败
    if (now) {
      task.load();
    }
    REGISTERED_TASK.add(task);

    scheduledExecutor.scheduleAtFixedRate(task, task.periodInSeconds(), task.periodInSeconds(),
        TimeUnit.SECONDS);
  }

  @Override
  public void run() {
    ProdLog.info("[ScheduleLoadTask] " + getTaskName() + " load start");
    final long start = System.currentTimeMillis();

    try {
      load();
      ProdLog.info("[ScheduleLoadTask] " + getTaskName() + " load end, cost="
          + (System.currentTimeMillis() - start));
    } catch (Throwable t) {
      // 不打印堆栈
      ProdLog.error("[ScheduleLoadTask] " + getTaskName() + " load fail, " + t);
    }
  }

  /**
   * 加载逻辑
   *
   * @throws Exception
   */
  public abstract void load() throws Exception;

  /**
   * 周期，以秒
   *
   * @return
   */
  public abstract int periodInSeconds();

  /**
   * 任务名
   * 
   * @return
   */
  public abstract String getTaskName();

  /**
   * 初始化任务
   */
  public void init() throws Exception {}

}
