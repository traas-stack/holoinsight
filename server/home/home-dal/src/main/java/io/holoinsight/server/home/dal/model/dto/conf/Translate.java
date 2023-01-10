/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto.conf;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: Translate.java, v 0.1 2022年03月14日 8:19 下午 jinsong.yjs Exp $
 */
@Data
public class Translate implements Serializable {
  private static final long serialVersionUID = 8300941265450477069L;

  /**
   * 转换函数
   */
  public List<ColumnCalExpr> exprs;

}
