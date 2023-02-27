/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.plugin;

import io.holoinsight.server.home.biz.plugin.model.ScheduleTimeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author masaimu
 * @version 2022-10-31 13:56:00
 */
@Component
public class PluginScheduleQueue {
  private static final Logger LOGGER = LoggerFactory.getLogger(PluginScheduleQueue.class);

  private LinkedList<Entry> queue_5_sec = new LinkedList<>();
  private LinkedList<Entry> queue_15_sec = new LinkedList<>();
  private LinkedList<Entry> queue_30_sec = new LinkedList<>();
  private LinkedList<Entry> queue_45_sec = new LinkedList<>();
  private LinkedList<Entry> queue_1_min = new LinkedList<>();
  private LinkedList<Entry> queue_5_min = new LinkedList<>();
  private LinkedList<Entry> queue_10_min = new LinkedList<>();

  ScheduledExecutorService syncExecutorService =
      new ScheduledThreadPoolExecutor(7, r -> new Thread(r, "ScheduleQueue"));

  ThreadPoolExecutor workExecutor = new ThreadPoolExecutor(10, 10, 1, TimeUnit.HOURS,
      new ArrayBlockingQueue<>(100), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
          return new Thread(r, "ScheduleWorker");
        }
      });

  @PostConstruct
  public void start() {
    syncExecutorService.scheduleAtFixedRate(new Worker(queue_5_sec, ScheduleTimeEnum.WAIT_5_SEC), 5,
        5, TimeUnit.SECONDS);
    syncExecutorService.scheduleAtFixedRate(new Worker(queue_15_sec, ScheduleTimeEnum.WAIT_15_SEC),
        5, 15, TimeUnit.SECONDS);
    syncExecutorService.scheduleAtFixedRate(new Worker(queue_30_sec, ScheduleTimeEnum.WAIT_30_SEC),
        5, 30, TimeUnit.SECONDS);
    syncExecutorService.scheduleAtFixedRate(new Worker(queue_45_sec, ScheduleTimeEnum.WAIT_45_SEC),
        5, 45, TimeUnit.SECONDS);
    syncExecutorService.scheduleAtFixedRate(new Worker(queue_1_min, ScheduleTimeEnum.WAIT_1_MIN), 5,
        60, TimeUnit.SECONDS);
    syncExecutorService.scheduleAtFixedRate(new Worker(queue_5_min, ScheduleTimeEnum.WAIT_5_MIN), 5,
        300, TimeUnit.SECONDS);
    syncExecutorService.scheduleAtFixedRate(new Worker(queue_10_min, ScheduleTimeEnum.WAIT_10_MIN),
        5, 600, TimeUnit.SECONDS);
  }

  public boolean addQueue(ScheduleTimeEnum waitTime, NotifyChain chain) {
    long cur = System.currentTimeMillis();
    switch (waitTime) {
      case WAIT_5_SEC:
        queue_5_sec.add(new Entry(chain, cur));
        break;
      case WAIT_15_SEC:
        queue_15_sec.add(new Entry(chain, cur));
        break;
      case WAIT_30_SEC:
        queue_30_sec.add(new Entry(chain, cur));
        break;
      case WAIT_45_SEC:
        queue_45_sec.add(new Entry(chain, cur));
        break;
      case WAIT_1_MIN:
        queue_1_min.add(new Entry(chain, cur));
        break;
      case WAIT_5_MIN:
        queue_5_min.add(new Entry(chain, cur));
        break;
      case WAIT_10_MIN:
        queue_10_min.add(new Entry(chain, cur));
        break;
      default:
        return false;
    }
    return true;
  }

  private static class Entry {
    NotifyChain chain;
    long createTime;

    public Entry(NotifyChain chain, long createTime) {
      this.chain = chain;
      this.createTime = createTime;
    }
  }

  private class Worker implements Runnable {
    private LinkedList<Entry> queue;
    private ScheduleTimeEnum timeEnum;

    public Worker(LinkedList<Entry> queue, ScheduleTimeEnum timeEnum) {
      this.queue = queue;
      this.timeEnum = timeEnum;
    }

    @Override
    public void run() {
      LOGGER.debug("PluginScheduleWorker {} run.", timeEnum);
      if (CollectionUtils.isEmpty(queue) || timeEnum == null) {
        return;
      }
      long cur = System.currentTimeMillis();
      Entry entry = queue.peek();
      if (entry == null || entry.chain == null) {
        return;
      }
      if (cur <= entry.createTime + timeEnum.getTimeRange()) {
        workExecutor.execute(entry.chain);
      }
    }
  }
}
