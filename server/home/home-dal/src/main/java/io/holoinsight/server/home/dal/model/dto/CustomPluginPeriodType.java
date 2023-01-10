/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;

/**
 *
 * @author jsy1001de
 * @version 1.0: CustomPluginPeriodType.java, v 0.1 2022年03月14日 8:06 下午 jinsong.yjs Exp $
 */
public enum CustomPluginPeriodType {
  SECOND(1000), FIVE_SECOND(5000), MINUTE(60000), HOUR(MINUTE.getDataUnitMs() * 60),;

  public int dataUnitMs; // 一个数据粒度是多少毫秒

  private CustomPluginPeriodType(int dataUnitMs) {
    this.dataUnitMs = dataUnitMs;
  }

  public int getDataUnitMs() {
    return dataUnitMs;
  }

  public void setDataUnitMs(int dataUnitMs) {
    this.dataUnitMs = dataUnitMs;
  }
}
