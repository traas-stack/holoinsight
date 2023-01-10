/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.core.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ForkJoinPool;

/**
 *
 * @author jsy1001de
 * @version 1.0: DimForkJoinPool.java, v 0.1 2022年03月07日 8:29 下午 jinsong.yjs Exp $
 */
public class DimForkJoinPool {
  private static final Logger logger = LoggerFactory.getLogger(DimForkJoinPool.class);


  public static final int PROCESSOR_NUM;

  private static volatile ForkJoinPool DIM_FORK_JOIN_POOL;

  static {
    int _processorNum = 16;
    try {
      int availableProcessors = Runtime.getRuntime().availableProcessors();
      if (availableProcessors > 0) {
        _processorNum = availableProcessors;
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
    PROCESSOR_NUM = _processorNum;
  }

  public static ForkJoinPool get() {
    if (DIM_FORK_JOIN_POOL == null) {
      synchronized (DimForkJoinPool.class) {
        if (DIM_FORK_JOIN_POOL == null) {
          DIM_FORK_JOIN_POOL = new ForkJoinPool(PROCESSOR_NUM);
          logger.info("init dim fork join pool, size={}.", PROCESSOR_NUM);
        }
      }
    }
    return DIM_FORK_JOIN_POOL;
  }
}
