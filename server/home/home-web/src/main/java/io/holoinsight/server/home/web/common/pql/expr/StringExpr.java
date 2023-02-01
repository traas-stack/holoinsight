/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.common.pql.expr;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zzhb101
 * @time 2023-01-04 11:16 上午
 */

@Data
public class StringExpr extends Expr {

  private String s = Strings.EMPTY;

  private List<String> tokens = new ArrayList<>();

  @Override
  public String explain() {
    StringBuilder sb = new StringBuilder();
    if (!tokens.isEmpty()) {
      String token = tokens.get(0);
      if (StringUtils.isNumeric(token)) {
        sb.append(token);
      } else {
        sb.append(token);
      }
    }
    return sb.toString();
  }
}
