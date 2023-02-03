/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.extension.ceresdbx.utils;

import com.xzchaoo.commons.stat.StatAccumulator;
import com.xzchaoo.commons.stat.StatManager;
import com.xzchaoo.commons.stat.StringsKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * created at 2022/3/17
 *
 * @author sw1136562366
 */
public final class StatUtils {
  private static final Logger LOGGER = LoggerFactory.getLogger("STAT");

  private static final StatManager SM = new StatManager(60000, LOGGER::info, false);
  public static final StatAccumulator<StringsKey> STORAGE_WRITE = SM.create("STORAGE_WRITE");
  private static final StatManager SM1S = new StatManager(1000, LOGGER::info, false);

  static {
    SM.start();
    SM1S.start();
  }

  private StatUtils() {}
}
