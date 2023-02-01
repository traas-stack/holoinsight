/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.common.pql.expr;

import lombok.Data;

import java.util.List;

/**
 * @author zzhb101
 * @time 2023-01-04 2:18 下午
 */

@Data
public class FuncExpr extends Expr {

  private String name;

  private List<Expr> exprs;

  private Boolean keepMetricNames = false;

  @Override
  public String explain() {
    StringBuilder sb = new StringBuilder();
    sb.append(name);
    sb.append("(");
    for (int i = 0; i < exprs.size(); i++) {
      sb.append(exprs.get(i).explain());
      if (i + 1 < exprs.size()) {
        sb.append(", ");
      }
    }
    sb.append(")");
    if (keepMetricNames) {
      sb.append(" keep_metric_names");
    }
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
