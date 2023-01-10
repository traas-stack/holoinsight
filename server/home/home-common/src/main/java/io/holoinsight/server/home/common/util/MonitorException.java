/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util;

/**
 *
 * @author jsy1001de
 * @version 1.0: MonitorException.java, v 0.1 2022年03月15日 12:29 下午 jinsong.yjs Exp $
 */
public class MonitorException extends RuntimeException {
  private static final long serialVersionUID = 3404868904147264514L;

  private ResultCodeEnum resultCodeEnum;

  public MonitorException() {
    super();
  }

  // public MonitorException(ResultCodeEnum resultCodeEnum) {
  // super(resultCodeEnum.getResultMessage());
  // this.resultCodeEnum = resultCodeEnum;
  // }


  public MonitorException(ResultCodeEnum resultCodeEnum, String additionalMessage) {
    super(additionalMessage);
    this.resultCodeEnum = resultCodeEnum;
  }


  public MonitorException(ResultCodeEnum resultCodeEnum, Throwable cause) {
    super(resultCodeEnum.getResultMessage(), cause);
    this.resultCodeEnum = resultCodeEnum;
  }

  public MonitorException(String string) {
    this(ResultCodeEnum.MONITOR_SYSTEM_ERROR, string);
  }


  public ResultCodeEnum getResultCode() {
    return resultCodeEnum;
  }


  public void setResultCode(ResultCodeEnum resultCodeEnum) {
    this.resultCodeEnum = resultCodeEnum;
  }
}
