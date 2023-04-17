/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.task;

import io.holoinsight.server.home.common.model.TaskEnum;
import io.holoinsight.server.home.common.util.CLUSTER_ROLE_CONST;
import io.holoinsight.server.home.common.util.CommonThreadPool;
import io.holoinsight.server.home.dal.model.ClusterTask;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author jsy1001de
 * @version 1.0: AbstractMonitorTask.java, v 0.1 2022年03月17日 7:49 下午 jinsong.yjs Exp $
 */
public abstract class AbstractMonitorTask {

  public static long SECOND = 1000;
  public static long FIVE_SECOND = 5000;
  public static long MINUTE = 60000;
  public static long FIVE_MINUTE = 5 * 60000;
  public static long TEN_MINUTE = 10 * 60000;
  protected AtomicInteger CORE_JOBS = new AtomicInteger(0);
  protected AtomicInteger RUNNING_JOBS = new AtomicInteger(0);

  // 默认是否开启
  public boolean defaultSwitch() {
    return true;
  }

  public abstract boolean needRun();

  // 任务周期是多少
  public long getTaskPeriod() {
    return MINUTE;
  }

  // 任务的超时时间
  public long expire() {
    return 2 * MINUTE;
  }

  // 构造子任务
  public abstract List<MonitorTaskJob> buildJobs(long period) throws Throwable;

  public List<MonitorTaskJob> buildJobs(long period, ClusterTask ct) {
    return null;
  }

  private ThreadPoolTaskExecutor corePool; // 构建任务用的
  private ThreadPoolTaskExecutor pool;
  protected String taskCode;

  public AbstractMonitorTask(int thread, int queueSize, TaskEnum taskEnum) {
    pool = CommonThreadPool.createThreadPool(thread, thread, queueSize, taskEnum.getCode());
    corePool = CommonThreadPool.createThreadPool(2, 2, 5, "core-" + taskEnum.getCode());
    this.taskCode = taskEnum.getCode();
  }

  public AbstractMonitorTask(int thread, int queueSize, String taskCode) {
    pool = CommonThreadPool.createThreadPool(thread, thread, queueSize, taskCode);
    corePool = CommonThreadPool.createThreadPool(2, 2, 5, "core-" + taskCode);
    this.taskCode = taskCode;
  }

  public String getTaskId() {
    return taskCode;
  }

  public ThreadPoolTaskExecutor getCorePool() {
    return corePool;
  }

  public ThreadPoolTaskExecutor getPool() {
    return pool;
  }

  public String getRole() {
    // 代表执行本任务的具体Role
    return CLUSTER_ROLE_CONST.PROD;
  }

}
