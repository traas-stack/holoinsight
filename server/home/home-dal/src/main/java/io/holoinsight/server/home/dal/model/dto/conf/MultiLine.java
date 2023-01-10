/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto.conf;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author jsy1001de
 * @version 1.0: LogLine.java, v 0.1 2022年03月31日 8:19 下午 jinsong.yjs Exp $
 */
@Data
public class MultiLine implements Serializable {

  private static final long serialVersionUID = 5669276660916689598L;
  /**
   * 日志开头/结尾是否是同一行，是否是多行日志
   */
  public Boolean multi;

  /**
   * 行头/行尾 logHead, logTail
   */
  public String lineType;

  /**
   * 正则表达式
   */
  public String logRegexp;
}
