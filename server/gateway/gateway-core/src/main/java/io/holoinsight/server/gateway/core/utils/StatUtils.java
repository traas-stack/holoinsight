/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xzchaoo.commons.stat.StatAccumulator;
import com.xzchaoo.commons.stat.StatManager;
import com.xzchaoo.commons.stat.StringsKey;

/**
 * TODO 这东西里的某些字段不要放在这里, 放在各自定义的地方
 * <p>
 * created at 2022/3/17
 *
 * @author sw1136562366
 */
public final class StatUtils {
  private static final Logger LOGGER = LoggerFactory.getLogger("STAT");

  private static final StatManager SM = new StatManager(60000, LOGGER::info, false);
  /** Constant <code>GRPC_TRAFFIC</code> */
  public static final StatAccumulator<StringsKey> GRPC_TRAFFIC = SM.create("GRPC_TRAFFIC");
  /** Constant <code>STORAGE_WRITE</code> */
  public static final StatAccumulator<StringsKey> STORAGE_WRITE = SM.create("STORAGE_WRITE");
  public static final StatAccumulator<StringsKey> KAFKA_SEND = SM.create("KAFKA_SEND");
  /** Constant <code>STORAGE_DISCARD</code> */
  public static final StatAccumulator<StringsKey> STORAGE_DISCARD = SM.create("STORAGE_DISCARD");
  private static final StatManager SM1S = new StatManager(1000, LOGGER::info, false);

  static {
    SM.start();
    SM1S.start();
  }

  private StatUtils() {}
}
