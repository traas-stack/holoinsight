/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.common.pql.expr;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author zzhb101
 * @time 2023-01-04 2:32 下午
 */

@Data
public class AggrFuncExpr extends Expr {

  private String name;

  private List<Expr> args;

  private ModifierExpr modifierExpr;

  private int limit;

  @Override
  public String explain() {
    StringBuilder sb = new StringBuilder();
    sb.append(name);
    sb.append("(");
    for (int i = 0; i < args.size(); i++) {
      sb.append(args.get(i).explain());
      if (i + 1 < args.size()) {
        sb.append(", ");
      }
    }
    sb.append(")");
    if (!StringUtils.isEmpty(modifierExpr.getOp())) {
      sb.append(" ");
      sb.append(modifierExpr.explain());
    }
    if (limit > 0) {
      sb.append("limit ");
      sb.append(limit);
    }

    return sb.toString();
  }

  @Override
  public List<String> explainToList() {
    List<String> result = super.explainToList();
    for (int i = 0; i < args.size(); i++) {
      result.add(args.get(i).explain());
    }
    return result;
  }
}
