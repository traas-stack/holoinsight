/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common;

import com.xzchaoo.commons.stat.StatManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * created at 2022/4/15
 *
 * @author zzhb101
 */
public final class MetricsUtils {
  private static final Logger STAT = LoggerFactory.getLogger("STAT");
  private static final Logger STAT1S = LoggerFactory.getLogger("STAT1S");

  public static final StatManager SM =
      new StatManager(TimeUnit.MINUTES.toMillis(1), STAT::info, false);
  public static final StatManager SM1S =
      new StatManager(TimeUnit.SECONDS.toMillis(1), STAT1S::info, false);

  static {
    SM.start();
    SM1S.start();
  }
}
