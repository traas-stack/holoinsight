/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.common.pql.expr;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zzhb101
 * @time 2023-01-04 11:36 上午
 */

@Data
public class MetricExpr extends Expr {

  private List<LabelFilter> labelFilters = new ArrayList<>();

  private List<LabelFilterExpr> labelFilterExprs = new ArrayList<>();

  @Override
  public String explain() {
    StringBuilder sb = new StringBuilder();
    List<LabelFilterExpr> lfs = labelFilterExprs;
    if (lfs.size() > 0) {
      LabelFilterExpr lf = lfs.get(0);
      if (lf.getLabel().equals("__name__") && !lf.getIsNegative() && !lf.getIsRegexp()) {
        sb.append(lf.getValue().getTokens().get(0));
        lfs = lfs.subList(1, lfs.size());
      }
    }
    if (lfs.size() > 0) {
      sb.append("{");
      for (int i = 0; i < lfs.size(); i++) {
        sb.append(lfs.get(i).explain());
        if (i + 1 < lfs.size()) {
          sb.append(",");
        }
      }
      sb.append("}");
    } else if (labelFilterExprs.size() == 0) {
      sb.append("{}");
    }

    return sb.toString();
  }
}
