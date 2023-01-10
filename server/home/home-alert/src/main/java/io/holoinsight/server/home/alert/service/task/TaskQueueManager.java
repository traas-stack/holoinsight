/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.task;

import io.holoinsight.server.home.alert.model.compute.ComputeTaskPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author wangsiyuan
 * @date 2022/2/24 3:14 下午
 */
public class TaskQueueManager {

  private static final Logger logger = LoggerFactory.getLogger(TaskQueueManager.class);

  private final LinkedBlockingQueue<ComputeTaskPackage> taskJobLinkedBlockingQueue =
      new LinkedBlockingQueue<>(500_000);

  private static TaskQueueManager instance;

  public static TaskQueueManager getInstance() {
    if (null == instance) {
      synchronized (TaskQueueManager.class) {
        if (null == instance) {
          try {
            instance = new TaskQueueManager();
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        }
      }
    }
    return instance;
  }

  public boolean offer(ComputeTaskPackage computeTaskPackage) {
    return taskJobLinkedBlockingQueue.offer(computeTaskPackage);
  }

  public ComputeTaskPackage peek() {
    return taskJobLinkedBlockingQueue.peek();
  }

  public ComputeTaskPackage poll() {
    return taskJobLinkedBlockingQueue.poll();
  }

  public ComputeTaskPackage take() {
    try {
      return taskJobLinkedBlockingQueue.take();
    } catch (InterruptedException e) {
      logger.error(e.getMessage(), e);
      return null;
    }
  }
}
