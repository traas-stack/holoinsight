/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zanghaibo
 * @time 2022-12-20 11:55 下午
 */

@Data
public class PqlParseResult {

  /**
   * pql关联指标
   */
  private List<String> metrics = new ArrayList<>();

  /**
   * pql嵌套逻辑表达式
   */
  private List<String> exprs = new ArrayList<>();

  /**
   * raw pql
   */
  private String rawPql;
}
