/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.common.pql.expr;

import lombok.Data;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zzhb101
 * @time 2023-01-03 4:46 下午
 */

@Data
public class ModifierExpr extends Expr {

  private String op = Strings.EMPTY;

  private List<String> args = new ArrayList<>();

  @Override
  public String explain() {
    StringBuilder sb = new StringBuilder();
    sb.append(op);
    sb.append(" (");
    for (int i = 0; i < args.size(); i++) {
      sb.append(args.get(i));
      if (i + 1 < args.size()) {
        sb.append(", ");
      }
    }
    sb.append(")");
    return sb.toString();
  }
}
