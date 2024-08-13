/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.log;

import lombok.Data;

import java.util.Map;

/**
 * @author zzhb101
 * @time 2023-04-03 6:13
 */

@Data
public class LogContextRequest {

  /**
   * app name
   */
  private String app;

  /**
   * Obtain the number of log entries from later times
   */
  private Integer backLines;

  /**
   * Obtain the number of log entries from earlier times
   */
  private Integer forwardLines;

  /**
   * log path
   */
  private String path;

  /**
   * query context
   */
  private Map<String, String> context;

  /**
   * type
   */
  private String type;
}
