/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.config;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author jsy1001de
 * @version 1.0: ProdLog.java, v 0.1 2022年02月24日 5:18 下午 jinsong.yjs Exp $
 */
@Slf4j
public class ProdLog {


  public static void info(String who, String what, String how) {
    log.info("{},{},{}", who, what, how);
  }

  public static void info(String what) {
    log.info("{}", what);
  }

  public static void info(String who, String what) {
    log.info("{},{}", who, what);
  }

  public static void debug(String who, String what, String how) {
    log.info("{},{},{}", who, what, how);
  }

  public static void debug(String who, String what) {
    log.info("{},{}", who, what);
  }

  public static void warn(String who, String what, Exception e) {
    log.warn("{},{},{}", who, what, e.getMessage());
  }

  public static void warn(String who, String what, String how) {
    log.warn("{},{},{}", who, what, how);
  }

  public static void warn(String who, String what) {
    log.warn("{},{}", who, what);
  }

  public static void error(Object who) {
    log.warn("{}", who);
  }

  public static void error(Object who, String why, Exception e) {
    log.error("{},{}", who, why, e);
  }

  public static void error(Object who, Throwable e) {
    log.error("{}", who, e);
  }

  /**
   *
   * @param who 什么角色
   * @param target 对什么对象
   * @param type 干啥
   * @param subType 干啥的子类型
   * @param success 结果
   * @param cost 耗时
   */
  public static void monitor(String module, String who, String target, String type, String subType,
      boolean success, long cost, long count) {
    log.info("[{}],[{}],[{}],[{}],[{}],[{}],[{}],[{}ms]", module, who, target, type, subType,
        success, cost, count);
  }
}
