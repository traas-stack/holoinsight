/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.log;

import lombok.Data;

import java.util.Map;

/**
 * @author zzhb101
 * @time 2023-04-03 5:25
 */

@Data
public class LogPathRequest {

  /**
   * app name
   */
  private String app;

  /**
   * query start time :s
   */
  private Integer from;

  /**
   * query end time :s
   */
  private Integer to;

  /**
   * query context
   */
  private Map<String, String> context;
}
