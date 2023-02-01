/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.common.pql.expr;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zzhb101
 * @time 2023-01-04 12:11 下午
 */

@Data
@AllArgsConstructor
public class ParensExpr extends Expr {

  private List<Expr> exprs = new ArrayList<>();

  @Override
  public String explain() {
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    for (int i = 0; i < exprs.size(); i++) {
      sb.append(exprs.get(i).explain());
      if (i + 1 < exprs.size()) {
        sb.append(", ");
      }
    }
    sb.append(")");
    return sb.toString();
  }

  @Override
  public List<String> explainToList() {
    List<String> result = super.explainToList();
    for (int i = 0; i < exprs.size(); i++) {
      result.add(exprs.get(i).explain());
    }
    return result;
  }
}
