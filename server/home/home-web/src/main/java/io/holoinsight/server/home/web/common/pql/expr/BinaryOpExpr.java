/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.common.pql.expr;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author zzhb101
 * @time 2023-01-03 4:44 下午
 */

@Data
public class BinaryOpExpr extends Expr {

  private String op;

  private Boolean bool = false;

  private ModifierExpr GroupModifier = new ModifierExpr();

  private ModifierExpr JoinModifier = new ModifierExpr();

  private Expr left;

  private Expr right;

  @Override
  public String explain() {
    StringBuilder sb = new StringBuilder();
    if (left.getClass().getSimpleName().equals("BinaryOpExpr")) {
      sb.append("(");
      sb.append(left.explain());
      sb.append(")");
    } else {
      sb.append(left.explain());
    }

    sb.append(" ");
    sb.append(op);
    if (bool) {
      sb.append(" bool");
    }

    if (!StringUtils.isEmpty(getGroupModifier().getOp())) {
      sb.append(" ");
      sb.append(getGroupModifier().explain());
    }

    if (!StringUtils.isEmpty(getJoinModifier().getOp())) {
      sb.append(" ");
      sb.append(getJoinModifier().explain());
    }

    sb.append(" ");

    if (right.getClass().getSimpleName().equals("BinaryOpExpr")) {
      sb.append("(");
      sb.append(right.explain());
      sb.append(")");
    } else {
      sb.append(right.explain());
    }

    return sb.toString();
  }

  @Override
  public List<String> explainToList() {
    List<String> result = super.explainToList();
    if (left != null) {
      result.add(left.explain());
    }
    if (right != null) {
      result.add(right.explain());
    }
    return result;
  }
}
