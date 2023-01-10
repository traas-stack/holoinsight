/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.common;

import io.holoinsight.server.common.J;
import io.holoinsight.server.common.cache.SafeStringDataCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author jsy1001de
 * @version 1.0: SampleEventWriteTask.java, v 0.1 2022年03月21日 8:04 下午 jinsong.yjs Exp $
 */
@Component
@Slf4j
public class SampleEventWriteTask {

  // 后续任务增加，扩大线程池
  private static final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
  private static final SafeStringDataCache CACHE = new SafeStringDataCache(true, log);

  /**
   * 这个类的处理不会对外抛出异常
   *
   * @param key
   * @param List
   */
  public static void submit(String key, Object event) {

    try {
      CACHE.offer(key, J.toJson(event));
    } catch (Throwable e) {
      log.error(key + " submit to task error : " + e.getMessage(), e);
    }
  }

  /**
   * 任务执行线程池
   */

  public void init() {
    try {
      Runnable syncTask = new Runnable() {
        @Override
        public void run() {
          // 每个提交一个TASK
          for (String key : CACHE.getKeys()) {
            executor.submit(new Runnable() {
              @Override
              public void run() {
                doAction(key);
              }
            });
          }
        }
      };

      // 5秒提交一次任务
      executor.scheduleAtFixedRate(syncTask, 0, 5, TimeUnit.SECONDS);

    } catch (Throwable e) {
      // 防止有预期之外的异常
      log.error("dimData write to db error : " + e.getMessage(), e);
    }
  }

  private void doAction(String key) {

  }
}
