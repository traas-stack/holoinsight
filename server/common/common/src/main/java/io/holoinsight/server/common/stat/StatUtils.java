/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.stat;

import com.xzchaoo.commons.stat.StringsKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author masaimu
 * @version 2024-04-24 11:27:00
 */
public class StatUtils {
  private static final Logger LOGGER = LoggerFactory.getLogger("STAT");

  private static final StatManager SM = new StatManager(60000, LOGGER::info, false);
  /** Constant <code>GRPC_TRAFFIC</code> */
  public static final StatAccumulator<StringsKey> GRPC_TRAFFIC = SM.create("GRPC_TRAFFIC");
  /** Constant <code>STORAGE_WRITE</code> */
  public static final StatAccumulator<StringsKey> STORAGE_DETAIL_WRITE =
      SM.create("STORAGE_DETAIL_WRITE");
  public static final StatAccumulator<StringsKey> KAFKA_SEND = SM.create("KAFKA_SEND");
  public static final StatAccumulator<StringsKey> KAFKA_CONSUME = SM.create("KAFKA_CONSUME");
  /** Constant <code>STORAGE_DISCARD</code> */
  public static final StatAccumulator<StringsKey> STORAGE_DISCARD = SM.create("STORAGE_DISCARD");
  private static final StatManager SM1S = new StatManager(1000, LOGGER::info, false);

  static {
    SM.start();
    SM1S.start();
  }

  private StatUtils() {}
}
