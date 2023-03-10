/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.task;

import com.google.common.collect.Maps;
import io.holoinsight.server.home.common.model.TaskEnum;

import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: TaskFactoryHolder.java, v 0.1 2022年03月17日 7:52 下午 jinsong.yjs Exp $
 */
public class TaskFactoryHolder {

  // 普通的那些定制任务型的，叫做task
  static final Map<TaskEnum, AbstractMonitorTask> taskFactoryMap = Maps.newConcurrentMap();

  public static AbstractMonitorTask getExecutorTask(TaskEnum type) {
    synchronized (taskFactoryMap) {
      return taskFactoryMap.get(type);
    }
  }

  public static void setExecutorTask(TaskEnum type, AbstractMonitorTask task) {
    synchronized (taskFactoryMap) {
      taskFactoryMap.put(type, task);
    }
  }
}
