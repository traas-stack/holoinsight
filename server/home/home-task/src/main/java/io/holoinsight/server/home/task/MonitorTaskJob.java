/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.task;

/**
 *
 * @author jsy1001de
 * @version 1.0: MonitorTaskJob.java, v 0.1 2022年03月17日 7:48 下午 jinsong.yjs Exp $
 */
public abstract class MonitorTaskJob {

  // 执行
  public abstract boolean run() throws Throwable;

  // job id用于追踪
  public abstract String id();

}
