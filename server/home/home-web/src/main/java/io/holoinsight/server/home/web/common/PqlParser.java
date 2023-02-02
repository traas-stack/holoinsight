/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.common;

import io.holoinsight.server.home.web.common.pql.Parser;
import io.holoinsight.server.home.web.common.pql.PqlException;
import io.holoinsight.server.home.web.common.pql.expr.Expr;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author zzhb101
 * @time 2022-12-21 12:04 上午
 */

@Service
public class PqlParser {

  public List<String> parseList(String pql) throws PqlException {
    if (StringUtils.isEmpty(pql)) {
      return Collections.emptyList();
    }
    Parser p = new Parser();
    p.initLexer(pql);
    Expr e = p.parse(pql);
    return e.explainToList();
  }
}
