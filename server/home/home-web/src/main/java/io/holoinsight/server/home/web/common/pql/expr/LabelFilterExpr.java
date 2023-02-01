/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.common.pql.expr;

import lombok.Data;
import org.apache.logging.log4j.util.Strings;

/**
 * @author zzhb101
 * @time 2023-01-04 11:39 上午
 */

@Data
public class LabelFilterExpr extends Expr {

  private String label = Strings.EMPTY;

  private StringExpr value;

  private Boolean isNegative = false;

  private Boolean isRegexp = false;

  @Override
  public String explain() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.getLabel());
    String op = null;
    if (isNegative) {
      if (isRegexp) {
        op = "!~";
      } else {
        op = "!=";
      }
    } else {
      if (isRegexp) {
        op = "=~";
      } else {
        op = "=";
      }
    }
    sb.append(op).append(value.explain());
    return sb.toString();
  }
}
