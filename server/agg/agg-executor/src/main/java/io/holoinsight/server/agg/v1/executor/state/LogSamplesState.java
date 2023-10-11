/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.state;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import io.holoinsight.server.agg.v1.core.data.LogSamples;
import lombok.Data;

/**
 * <p>
 * created at 2023/9/23
 *
 * @author xzchaoo
 */
@Data
public class LogSamplesState {
  public static final int STRATEGY_ANY = 0;

  private static final int MAX_COUNT = 100;
  private static final int MAX_HOST = 10;

  private int strategy;

  private int maxCount;
  private LinkedList<LogSamples.HostLogSample> accumulated;
  private LinkedList<LogSamples.HostLogSample> pending;
  private transient int count;
  private transient int index;

  public boolean isEmpty() {
    return count == 0;
  }

  public boolean isFull() {
    if (maxCount <= 0) {
      return true;
    }
    int size = 0;
    if (accumulated != null) {
      size += accumulated.size();
    }
    if (pending != null) {
      size += pending.size();
    }
    return size >= maxCount;
  }

  public void add(List<LogSamples.HostLogSample> list) {
    if (CollectionUtils.isEmpty(list) || isFull()) {
      return;
    }

    // fix data
    for (LogSamples.HostLogSample hls : list) {
      if (hls.getHostname() == null) {
        hls.setHostname("");
      }
    }

    if (accumulated == null) {
      accumulated = new LinkedList<>();
    }

    for (LogSamples.HostLogSample hls : list) {
      if (isFull() || CollectionUtils.isEmpty(hls.getLogs())) {
        return;
      }

      if (strategy == STRATEGY_ANY) {
        while (count + hls.getLogs().size() > maxCount) {
          hls.getLogs().removeLast();
        }
        count += hls.getLogs().size();
        accumulated.add(hls);
        continue;
      }

      count += hls.getLogs().size();

      if (hls.getLogs().size() == 1) {
        accumulated.add(hls);
        continue;
      }

      // hls.getLogs().size()>1
      if (pending == null) {
        pending = new LinkedList<>();
      }
      pending.add(hls);

      while (count > maxCount && pending.size() > 0) {
        hls = pending.peekFirst();
        while (hls.getLogs().size() > 1 && count > maxCount) {
          hls.getLogs().removeLast();
          count--;
        }
        if (hls.getLogs().size() == 1) {
          pending.removeFirst();
          accumulated.add(hls);
        }
      }
    }
  }

  public void setStrategy(int strategy) {
    this.strategy = strategy;
  }

  public void setStrategy(String strategy) {
    if ("ANY".equals(strategy)) {
      this.strategy = 0;
    } else {
      this.strategy = 1;
    }
  }
}
