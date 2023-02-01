/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.common.pql.expr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.util.Strings;

/**
 * @author zzhb101
 * @time 2023-01-04 12:17 上午
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DurationExpr extends Expr {

  private String s = Strings.EMPTY;

  @Override
  public String explain() {
    StringBuilder sb = new StringBuilder();
    sb.append(s);
    return sb.toString();
  }

}
