/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.agent;

import io.holoinsight.server.common.Pair;
import io.holoinsight.server.common.cache.SafeMapDataCache;
import io.holoinsight.server.common.UtilMisc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.holoinsight.server.meta.facade.model.MetaType;
import io.holoinsight.server.meta.facade.service.AgentHeartBeatService;

/**
 *
 * @author zzhb101
 * @version 1.0: DimDataWriteTask.java, v 0.1 2022年07月04日 11:37 上午 jinsong.yjs Exp $
 */
@Component
public class DimDataWriteTask implements InitializingBean {
  private static final Logger L = LoggerFactory.getLogger(DimDataWriteTask.class);
  private static final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(12);

  private static final SafeMapDataCache UPSERT_CACHE = new SafeMapDataCache(true, L);
  private static final SafeMapDataCache DELETE_CACHE = new SafeMapDataCache(true, L);

  @Autowired
  private AgentHeartBeatService agentHeartBeatService;

  @Override
  public void afterPropertiesSet() throws Exception {
    // *** 启动 WriteBack SyncTask ***/
    init();
  }

  /**
   * 这个类的处理不会对外抛出异常
   *
   * @param dimTableName
   * @param dimData
   */
  public static void upsertSubmit(String dimTableName, String type, Map<String, Object> dimData) {

    try {
      UPSERT_CACHE.offer(concatDestDim(type, dimTableName), dimData);
    } catch (Throwable e) {
      L.error("dimData submit to task error : " + dimTableName, e);
    }
  }

  public static void deleteSubmit(String dimTableName, String type, Map<String, Object> dimData) {

    try {
      DELETE_CACHE.offer(concatDestDim(type, dimTableName), dimData);
    } catch (Throwable e) {
      L.error("dimData submit to task error : " + dimTableName, e);
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
          for (String destDim : UPSERT_CACHE.getKeys()) {
            Pair<String, String> p = splitDestDim(destDim);
            executor.submit(() -> {
              sync2DB(p.right(), p.left());
            });

          }
        }
      };

      Runnable deleteTask = new Runnable() {
        @Override
        public void run() {
          // 每个提交一个TASK
          for (String destDim : DELETE_CACHE.getKeys()) {
            Pair<String, String> p = splitDestDim(destDim);
            executor.submit(new Runnable() {
              @Override
              public void run() {
                delete4DB(p.right(), p.left());
              }
            });
          }
        }
      };

      Runnable threadPoolStatusTask = new Runnable() {
        @Override
        public void run() {
          L.info(String.format(
              "async-executor monitor. taskCount:%s, completedTaskCount:%s, largestPoolSize:%s, poolSize:%s, "
                  + "activeCount:%s,queueSize:%s",
              executor.getTaskCount(), executor.getCompletedTaskCount(),
              executor.getLargestPoolSize(), executor.getPoolSize(), executor.getActiveCount(),
              executor.getQueue().size()));

          UPSERT_CACHE.logStatus();
          DELETE_CACHE.logStatus();
        }
      };

      // 1秒提交一次任务
      executor.scheduleAtFixedRate(syncTask, 0, 1, TimeUnit.SECONDS);
      executor.scheduleAtFixedRate(deleteTask, 0, 1, TimeUnit.SECONDS);
      executor.scheduleAtFixedRate(threadPoolStatusTask, 0, 30, TimeUnit.SECONDS);

    } catch (Throwable e) {
      // 防止有预期之外的异常
      L.error("dimData write to db error : " + e.getMessage(), e);
    }
  }

  // write to db
  private void sync2DB(String dimTableName, String type) {
    List<Map<String, Object>> rows = UPSERT_CACHE.poll(concatDestDim(type, dimTableName));
    String trace = "trace-sync2DB, " + dimTableName + ", " + type + ", rows: " + rows.size();

    if (CollectionUtils.isEmpty(rows)) {
      return;
    }
    L.info(trace);

    List<List<Map<String, Object>>> listList = UtilMisc.divideList(rows, 100);

    listList.forEach(list -> {
      L.info(trace + ", exec patch : " + list.size() + ", start");
      agentHeartBeatService.agentInsertOrUpdate(dimTableName, MetaType.valueOf(type.toUpperCase()),
          list);
      L.info(trace + ", exec patch : " + list.size() + ", end");
    });

  }

  // delete for db
  private void delete4DB(String dimTableName, String type) {
    List<Map<String, Object>> rows = DELETE_CACHE.poll(concatDestDim(type, dimTableName));
    String trace = "trace-delete4DB, " + dimTableName + ", " + type + ", rows: " + rows.size();
    if (CollectionUtils.isEmpty(rows)) {
      return;
    }
    L.info(trace);
    List<List<Map<String, Object>>> listList = UtilMisc.divideList(rows, 100);

    listList.forEach(list -> {
      L.info(trace + ", exec patch : " + list.size() + ", start");
      agentHeartBeatService.agentDelete(dimTableName, MetaType.valueOf(type.toUpperCase()), list);
      L.info(trace + ", exec patch : " + list.size() + ", end");
    });
  }

  public static Pair<String/* type */, String/* table */> splitDestDim(String destDim) {
    String[] lst = destDim.split(":");
    return new Pair<>(lst[0], lst[1]);
  }

  public static String concatDestDim(String dest, String dimTableName) {
    return String.format("%s:%s", dest, dimTableName);
  }
}
