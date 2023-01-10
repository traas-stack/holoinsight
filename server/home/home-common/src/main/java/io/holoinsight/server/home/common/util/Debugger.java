/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author jsy1001de
 * @version 1.0: Debugger.java, v 0.1 2022年08月04日 7:30 下午 jinsong.yjs Exp $
 */
@Slf4j
public class Debugger {
  /**
   * 打印调试信息
   * 
   * @param msg 信息
   * @param args 参数
   */
  public static void print(String ukFlag, String msg) {
    if (GlobalFlag.isDebugEnable(ukFlag)) {
      log.info(msg);
    }
  }

  /**
   * 打印调试信息
   * 
   * @param msg 信息
   * @param args 参数
   */
  public static void print(String ukFlag, String var1, Object... var2) {

    if (GlobalFlag.isDebugEnable(ukFlag)) {
      log.info(var1, var2);
    }
  }

  /**
   * 打印调试信息
   * 
   * @param msg 消息
   * @param t 异常
   */
  public static void print(String ukFlag, String msg, Throwable t) {
    if (GlobalFlag.isDebugEnable(ukFlag)) {
      log.error(msg, t);
    }
  }
}
