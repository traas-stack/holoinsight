/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.task;

import io.holoinsight.server.common.AddressUtil;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.config.ProdLog;
import io.holoinsight.server.common.config.ScheduleLoadTask;
import io.holoinsight.server.home.biz.common.MetaDictUtil;
import io.holoinsight.server.home.biz.service.ClusterService;
import io.holoinsight.server.home.biz.service.ClusterTaskService;
import io.holoinsight.server.home.biz.service.impl.ClusterTaskServiceImpl;
import io.holoinsight.server.home.common.util.CLUSTER_ROLE_CONST;
import io.holoinsight.server.home.common.util.Debugger;
import io.holoinsight.server.home.dal.model.ClusterTask;
import io.holoinsight.server.home.dal.model.dto.ClusterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: MonitorTaskManager.java, v 0.1 2022-03-17 19:40 jinsong.yjs Exp $
 */
@Service
public class MonitorTaskManager extends ScheduleLoadTask {

  @Autowired
  private ClusterTaskService clusterTaskService;

  @Autowired
  private ClusterService clusterService;

  @Override
  public void load() {
    try {
      // 这里任务周期
      long current = System.currentTimeMillis();
      current = current - current % 1000;
      // 强制执行时间比下发时间延后一秒，避免任务来不及执行
      long period = current;

      List<ClusterDTO> clusters =
          clusterService.getClusterAliveSortedByRole(CLUSTER_ROLE_CONST.PROD);
      ProdLog.info("start work.");

      masterWork(clusters, period);
      slaveWork(current);
    } catch (Exception e) {
      ProdLog.error("execute task error", e);
    }
  }

  private void masterWork(List<ClusterDTO> metaClusters, long period) {
    if (clusterService.checkBrain(metaClusters)) {
      ProdLog.info(String.format("[master][%s][%s]", AddressUtil.getLocalHostIPV4(),
          AddressUtil.getLocalHostName()));

      // 开始这个时间戳的工作， 初始化任务元数据
      ProdLog.info("Brain,  begin init Task Meta," + period);
      ProdLog.info("Brain,  begin schedule Task," + period);
      // 开始分配任务
      schedule(metaClusters, period);
    } else {
      ProdLog.info(String.format("[slave][%s][%s]", AddressUtil.getLocalHostIPV4(),
          AddressUtil.getLocalHostName()));
    }
  }

  private void schedule(List<ClusterDTO> metaClusters, long period) {
    List<ClusterTask> cts = new ArrayList<>();
    List<String> ignoreTasks = MetaDictUtil.getIgnoreTasks();

    try {

      for (AbstractMonitorTask task : TaskFactoryHolder.taskFactoryMap.values()) {
        if (!task.needRun()) {
          continue;
        }
        String taskId = task.getTaskId();
        long taskPeriod = task.getTaskPeriod(); // 任务周期

        if (period % taskPeriod != 0) {
          // 不是我的调度周期，直接跳过
          Debugger.print("MonitorTaskManager",
              "period not match task, continue." + taskId + "," + period + "/" + taskPeriod);
          continue;
        }

        if (!CollectionUtils.isEmpty(ignoreTasks) && ignoreTasks.contains(taskId)) {
          Debugger.print("MonitorTaskManager",
              "ignore task, continue." + taskId + "," + period + "/" + taskPeriod);
          continue;
        }

        ClusterDTO c = getExeCluster(taskId, metaClusters);
        ClusterTask ct = new ClusterTask();
        ct.setClusterIp(c.getIp());
        ct.setPeriod(period);
        ct.setTaskId(taskId);
        ct.setStatus(ClusterTaskServiceImpl.TASK_UNDONE);
        ct.setGmtCreate(new Date());
        ct.setGmtModified(new Date());
        cts.add(ct);
        ProdLog.info("task detail:" + ct);
      }

      if (!CollectionUtils.isEmpty(cts)) {
        clusterTaskService.batchInsert(cts);
      }

    } catch (Exception e) {
      ProdLog.error("schedule error, maybe something wrong", e);
    }
  }

  private ClusterDTO getExeCluster(String taskId, List<ClusterDTO> clusters) {
    int index = Math.abs(taskId.hashCode()) % clusters.size();
    return clusters.get(index);
  }

  private void slaveWork(long period) {
    List<ClusterTask> cts = clusterTaskService.getMyTask(period);
    if (CollectionUtils.isEmpty(cts)) {
      return;
    }
    ProdLog.info("get my task by period:" + period + ", task size:" + cts.size());
    // 开始执行
    for (ClusterTask ct : cts) {
      // 得到task
      AbstractMonitorTask task = TaskFactoryHolder.getExecutorTask(ct.getTaskId());
      if (task == null) {
        ProdLog.info("unknown task:" + J.toJson(ct));
        continue;
      }
      boolean needRun = task.needRun();

      if (!needRun) {
        ProdLog.info(task.getTaskId() + "-" + period + ", not need run, just return");
        continue;
      }
      if (task.RUNNING_JOBS.get() == 0) {
        // 开始执行了（保障单机永远只有一个任务执行）
        ProdLog.info(task.getTaskId() + "-" + period + ",task begin run");
      } else {
        ProdLog.info(task.getTaskId() + "-" + period + ", jobs-" + task.RUNNING_JOBS.get()
            + ",task is running, give up");
        continue; // 不然直接返回
      }

      ProdLog.info(task.getTaskId() + "-" + period + ", begin build jobs");

      if (task.CORE_JOBS.get() > 0) {
        // 上一个还在积压
        ProdLog.info(task.getTaskId() + "-" + period + ", last task is build jobs still, give up");
        continue;
      }

      task.getCorePool().submit(() -> {
        try {
          task.CORE_JOBS.incrementAndGet();
          taskJobBuild(task, period, ct);
          clusterTaskService.doneTask(ct, true, null);
        } catch (Throwable e) {
          ProdLog.error(task.getTaskId() + "-" + period + ", build jobs error", e);
          clusterTaskService.doneTask(ct, false, e.getMessage());
        } finally {
          task.CORE_JOBS.decrementAndGet();
        }
      });
    }
  }

  public void taskJobBuild(AbstractMonitorTask task, long period, ClusterTask ct) throws Throwable {
    ProdLog.info(task.getTaskId() + "-" + period + ", begin build jobs");
    long s = System.currentTimeMillis();
    List<MonitorTaskJob> jobs = task.buildJobs(period);
    if (jobs == null || jobs.size() == 0) {
      jobs = task.buildJobs(period, ct);
    }
    if (jobs == null || jobs.size() == 0) {
      ProdLog.info(task.getTaskId() + "-" + period + ", has no job, just return");
      return;
    } else {
      ProdLog.info(task.getTaskId() + "-" + period + ", has job size: " + jobs.size() + ", cost:"
          + (System.currentTimeMillis() - s));
    }
    for (MonitorTaskJob job : jobs) {
      try {
        task.getPool().submit(() -> {
          task.RUNNING_JOBS.incrementAndGet(); // 增加一个Job
          long s1 = System.currentTimeMillis();
          long end;
          try {
            job.run();
            end = System.currentTimeMillis();
            ProdLog.monitor("TASK_MANAGER", task.getTaskId(), job.id(), "Y", "", true, end - s1, 1);
            ProdLog.info(task.getTaskId() + "-" + period + "-" + job.id() + ", work success, cost:"
                + (end - s1));
          } catch (Throwable e) {
            end = System.currentTimeMillis();
            ProdLog.monitor("TASK_MANAGER", task.getTaskId(), job.id(), "N", "", false, end - s1,
                1);
            ProdLog.error(task.getTaskId() + "-" + period + "-" + job.id() + ", work failed, cost:"
                + (end - s1), e);
          } finally {
            // 计数减1,Job
            task.RUNNING_JOBS.decrementAndGet();
            ProdLog.info(task.getTaskId() + "-" + period + ", close job:" + job.id());
          }
        });
      } catch (Exception e) {
        ProdLog.error("task submit error, CRITICAL with" + task.getTaskId(), e);
      }
    }
  }

  @Override
  public int periodInSeconds() {
    return 1;
  }

  @Override
  public String getTaskName() {
    return "MonitorTaskManager";
  }
}
