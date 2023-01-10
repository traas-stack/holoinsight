/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jsy1001de
 * @version 1.0: ProdLog.java, v 0.1 2022年02月24日 5:18 下午 jinsong.yjs Exp $
 */
public class ProdLog {

  private static final Logger logger = LoggerFactory.getLogger(ProdLog.class);

  public static void info(String who, String what, String how) {
    logger.info("[" + who + "],[" + what + "],[" + how + "]");
  }

  public static void info(String what) {
    logger.info("[" + what + "]");
  }

  public static void info(String who, String what) {
    logger.info("[" + who + "],[" + what + "]");
  }

  public static void debug(String who, String what, String how) {
    logger.info("[" + who + "],[" + what + "],[" + how + "]");
  }

  public static void debug(String who, String what) {
    logger.info("[" + who + "],[" + what + "]");
  }

  public static void warn(String who, String what, Exception e) {
    String sb = "[" + who + "],[" + what + "],[" + e.getMessage() + "]";
    logger.warn(sb);
  }

  public static void warn(String who, String what, String how) {
    logger.warn("[" + who + "],[" + what + "],[" + how + "]");
  }

  public static void warn(String who, String what) {
    logger.warn("[" + who + "],[" + what + "]");
  }

  public static void error(Object who) {
    logger.error("[" + who + "]");
  }

  public static void error(Object who, String why, Exception e) {
    logger.error("[" + who + "],[" + why + "]", e);
  }

  public static void error(Object who, Throwable e) {
    logger.error("[" + who + "],[--]", e);
  }
}
