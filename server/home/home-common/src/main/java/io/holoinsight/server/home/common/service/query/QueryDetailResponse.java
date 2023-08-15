/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.service.query;

import lombok.Data;
import java.util.List;

/**
 * @author masaimu
 * @version 2023-08-15 12:57:00
 */
@Data
public class QueryDetailResponse {

  private List<DetailResult> results;

  @Data
  public static class DetailResult {
    private List<String> tables;
    private String sql;
    private List<String> headers;
    private List<Object[]> values;

  }
}
