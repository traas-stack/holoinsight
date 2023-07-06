/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto.conf;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author jsy1001de
 * @version 1.0: ParseType.java, v 0.1 2022年04月01日 10:32 上午 jinsong.yjs Exp $
 */
@Data
public class LogParse implements Serializable {
  private static final long serialVersionUID = -6851861666491078095L;

  // 分隔符切分/左起右至/正则表达式
  public String splitType;

  public LogSeparator separator;
  public LogRegexp regexp;

  public LogPattern pattern;

  public TimeConfig timeConfig;

  /**
   * 指定行头/行尾
   */
  public MultiLine multiLine;

  @Data
  public static class LogSeparator {
    public String separatorPoint;
  }

  @Data
  public static class LogRegexp {
    public String expression;
  }

  public boolean checkIsPattern() {
    return null != pattern && pattern.logPattern;
  }
}
