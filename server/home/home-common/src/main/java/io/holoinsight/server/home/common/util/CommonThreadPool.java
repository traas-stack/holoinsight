/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author jsy1001de
 * @version 1.0: CommonThreadPool.java, v 0.1 2022年03月17日 7:54 下午 jinsong.yjs Exp $
 */
public class CommonThreadPool {
  public static ThreadPoolTaskExecutor createThreadPool(int coreSize, int maxSize,
      int queueCapacity, String tpname) {
    return createThreadPool(coreSize, maxSize, queueCapacity, tpname,
        new ThreadPoolExecutor.AbortPolicy());
  }

  public static ThreadPoolTaskExecutor createThreadPool(int coreSize, int maxSize,
      int queueCapacity, String tpname, RejectedExecutionHandler rejectedExecutionHandler) {
    ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();
    threadPool.setThreadNamePrefix(tpname);
    threadPool.setMaxPoolSize(maxSize);
    threadPool.setCorePoolSize(coreSize);
    threadPool.setQueueCapacity(queueCapacity);
    threadPool.afterPropertiesSet();
    threadPool.setRejectedExecutionHandler(rejectedExecutionHandler);
    return threadPool;
  }

  public static TaskScheduler newFixedThreadPoolTaskScheduler(int size, int awaitTerminationSeconds,
      final String poolName) {
    ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    taskScheduler.setPoolSize(size);
    taskScheduler.setThreadNamePrefix(poolName + "-");
    taskScheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
    taskScheduler.setAwaitTerminationSeconds(awaitTerminationSeconds);
    taskScheduler.initialize();
    return taskScheduler;
  }
}
