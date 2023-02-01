/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.common.pql.expr;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zzhb101
 * @time 2023-01-02 8:26 下午
 */

@Data
public class Expr {

  /**
   * 表达式解释为string
   * 
   * @return
   */
  public String explain() {
    return StringUtils.EMPTY;
  }

  /**
   * 分层表达式解析为list
   * 
   * @return
   */
  public List<String> explainToList() {
    List<String> result = new ArrayList<>();
    result.add(explain());
    return result;
  }

}
