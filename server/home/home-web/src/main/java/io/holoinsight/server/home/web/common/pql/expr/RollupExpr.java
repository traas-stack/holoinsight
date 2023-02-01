/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.common.pql.expr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zzhb101
 * @time 2023-01-04 12:15 上午
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RollupExpr extends Expr {

  private Expr expr;

  private DurationExpr window;

  private DurationExpr offset;

  private DurationExpr step;

  private Boolean inheritStep = false;

  private Expr at;

  @Override
  public String explain() {
    StringBuilder sb = new StringBuilder();
    Boolean needParens = false;
    if (expr.getClass().getSimpleName().equals("RollupExpr")
        || expr.getClass().getSimpleName().equals("BinaryOpExpr")
        || expr.getClass().getSimpleName().equals("AggrFuncExpr")
            && !StringUtils.isEmpty(((AggrFuncExpr) expr).getModifierExpr().getOp())) {
      needParens = true;
    }
    if (needParens) {
      sb.append("(");
    }
    sb.append(expr.explain());
    if (needParens) {
      sb.append(")");
    }

    if (window != null || inheritStep || step != null) {
      sb.append("[");
      sb.append(window.explain());
      if (step != null && !StringUtils.isEmpty(step.getS())) {
        sb.append(":");
        sb.append(step.explain());
      } else if (inheritStep) {
        sb.append(":");
      }
      sb.append("]");
    }

    if (offset != null) {
      sb.append(" offset ");
      sb.append(offset.explain());
    }

    if (at != null) {
      sb.append(" @ ");
      Boolean needAtParens = at.getClass().getSimpleName().equals("BinaryOpExpr");
      if (needAtParens) {
        sb.append("(");
      }
      sb.append(at.explain());
      if (needAtParens) {
        sb.append(")");
      }
    }

    return sb.toString();
  }

}
