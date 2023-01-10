/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto.conf;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author jsy1001de
 * @version 1.0: ColumnCalExpr.java, v 0.1 2022年03月14日 8:19 下午 jinsong.yjs Exp $
 */
@Data
public class ColumnCalExpr implements Serializable {
  private static final long serialVersionUID = 4667410956863321660L;
  /**
   * 函数
   */
  private String func;

  /**
   * 参数
   */
  private String params;

  public ColumnCalExpr() {}

  public ColumnCalExpr(String func, String params) {
    this.func = func;
    this.params = params;
  }
}
