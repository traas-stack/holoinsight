/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.common.pql.expr;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zzhb101
 * @time 2023-01-03 4:55 下午
 */

@Data
public class WithArgExpr extends Expr {

  private String name;

  private List<String> args = new ArrayList<>();

  private Expr expr;

  @Override
  public String explain() {
    StringBuilder sb = new StringBuilder();
    sb.append(name);
    if (args.size() > 0) {
      sb.append("(");
      for (int i = 0; i < args.size(); i++) {
        sb.append(args.get(i));
        if (i + 1 < args.size()) {
          sb.append(", ");
        }
      }
      sb.append(")");
    }
    sb.append(" = ");
    sb.append(expr.explain());
    return sb.toString();
  }

}
