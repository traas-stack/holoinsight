/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.log;

import lombok.Data;

import java.util.List;

/**
 * @author zzhb101
 * @time 2023-04-03 8:05
 */

@Data
public class LogQueryResponse {
  /**
   * log details
   */
  private List<Log> logs;

  /**
   * query log count
   */
  private Integer count;

  /**
   * query start time :s
   */
  private Integer from;

  /**
   * query end time :s
   */
  private Integer to;

  private String whereQuery;
}
