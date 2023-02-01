/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.common.pql.expr;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zzhb101
 * @time 2023-01-03 4:36 下午
 */

@Data
public class WithExpr extends Expr {

  private Expr expr;

  private List<WithArgExpr> withArgExprs = new ArrayList<>();

  @Override
  public String explain() {
    StringBuilder sb = new StringBuilder();
    sb.append("WITH (");
    for (int i = 0; i < withArgExprs.size(); i++) {
      sb.append(withArgExprs.get(i).explain());
      if (i + 1 < withArgExprs.size()) {
        sb.append(", ");
      }
    }
    sb.append(") ");
    sb.append(expr.explain());
    return sb.toString();
  }

}
