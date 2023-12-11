/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.service.query;

import lombok.Data;

import java.util.List;

@Data
public class QueryResponse {

  /**
   * data
   */
  private List<Result> results;

  /**
   * 齐全度
   */
  private List<Result> completes;

}
