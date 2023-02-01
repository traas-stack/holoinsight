/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.common.pql.expr;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zzhb101
 * @time 2023-01-04 12:24 上午
 */

@Data
@AllArgsConstructor
public class NumberExpr extends Expr {

  private Double n;

  private String s;

  @Override
  public String explain() {
    StringBuilder sb = new StringBuilder();
    if (!StringUtils.isEmpty(s)) {
      sb.append(s);
    }
    return sb.toString();
  }
}
