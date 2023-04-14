/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.log;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author zzhb101
 * @time 2023-04-03 6:17
 */

@Data
public class LogContextResponse {

  /**
   * Obtain the number of log entries from later times
   */
  private Integer backLines;

  /**
   * Obtain the number of log entries from earlier times
   */
  private Integer forwardLines;

  /**
   * log details
   */
  private List<Log> logs;

  /**
   * query log count
   */
  private Integer count;

  /**
   * query context
   */
  private Map<String, String> params;
}
