/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller.model;

import lombok.Data;

/**
 * @author zanghaibo
 * @time 2022-12-20 11:54 下午
 */

@Data
public class PqlParseRequest {

  /**
   * raw pql
   */
  private String pql;
}
