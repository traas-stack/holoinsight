/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.task;

import com.google.common.collect.Maps;
import io.holoinsight.server.home.common.model.TaskEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: TaskFactoryHolder.java, v 0.1 2022年03月17日 7:52 下午 jinsong.yjs Exp $
 */
public class TaskFactoryHolder {


  static final Map<String, AbstractMonitorTask> taskFactoryMap = Maps.newConcurrentMap();
  static final Map<String, MetricCrawlerBuilder> metricCrawlerBuilderMap = Maps.newConcurrentMap();

  public static AbstractMonitorTask getExecutorTask(String taskCode) {
    synchronized (taskFactoryMap) {
      return taskFactoryMap.get(taskCode);
    }
  }

  public static void setExecutorTask(TaskHandler handler, AbstractMonitorTask task) {
    synchronized (taskFactoryMap) {
      if (handler.value() != TaskEnum.UNKNOWN_TASK) {
        taskFactoryMap.put(handler.value().getCode(), task);
      } else if (StringUtils.isNotEmpty(handler.code())) {
        taskFactoryMap.put(handler.code(), task);
      }
    }
  }

  public static MetricCrawlerBuilder getCrawlerTask(String builderTask) {
    synchronized (metricCrawlerBuilderMap) {
      return metricCrawlerBuilderMap.get(builderTask);
    }
  }

  public static void setCrawlerTask(MetricCrawler handler, MetricCrawlerBuilder builder) {
    synchronized (metricCrawlerBuilderMap) {
      metricCrawlerBuilderMap.put(handler.code(), builder);
    }
  }
}
